package me.card.switchv1.core.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.EventLoop;
import io.netty.channel.SimpleChannelInboundHandler;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import me.card.switchv1.core.component.Api;
import me.card.switchv1.core.component.ApiCoder;
import me.card.switchv1.core.component.DestinationURL;
import me.card.switchv1.core.component.Message;
import me.card.switchv1.core.component.MessageCoder;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.ConnectionPool;
import okhttp3.Dispatcher;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ChannelHandler.Sharable
public class BackOfficeHandler extends SimpleChannelInboundHandler<ByteBuf> {
  private static final Logger logger = LoggerFactory.getLogger(BackOfficeHandler.class);
  public static final String NAME = "BackOfficeHandler";

  private final ExecutorService businessExecutor;
  private final OkHttpClient httpClient;
  private final ApiCoder<Api, Message> apiCoder;
  private final MessageCoder messageCoder;
  private final Class<? extends Api> responseApiClz;
  private final DestinationURL destinationURL;


  private static class RequestContext {
    final ChannelHandlerContext ctx;        // Netty上下文（用于写回）
    final ByteBuf originalRequest;            // 原始请求
    final long startTime;                     // 开始时间
    final Map<String, Object> businessData;   // 业务数据暂存区

    // 各阶段时间统计（用于监控）
    long nettyReceiveTime;
    long businessPreTime;
    long httpStartTime;
    long httpEndTime;
    long businessPostTime;

    RequestContext(ChannelHandlerContext ctx, ByteBuf request) {
      this.ctx = ctx;
      this.originalRequest = request;
      this.startTime = System.currentTimeMillis();
      this.nettyReceiveTime = startTime;
      this.businessData = new ConcurrentHashMap<>();
    }

    // 记录各阶段时间
    void markBusinessPre() {
      businessPreTime = System.currentTimeMillis();
    }

    void markHttpStart() {
      httpStartTime = System.currentTimeMillis();
    }

    void markHttpEnd() {
      httpEndTime = System.currentTimeMillis();
    }

    void markBusinessPost() {
      businessPostTime = System.currentTimeMillis();
    }

    // 打印性能日志
    void logPerformance() {
      logger.info("性能统计 - 总耗时: {}ms, 业务预处理: {}ms, HTTP调用: {}ms, 业务后处理: {}ms",
          (businessPostTime - nettyReceiveTime),
          (httpStartTime - businessPreTime),
          (httpEndTime - httpStartTime),
          (businessPostTime - httpEndTime));
    }
  }

  public BackOfficeHandler(ApiCoder<Api, Message> apiCoder,
                           MessageCoder messageCoder,
                           Class<? extends Api> responseApiClz,
                           DestinationURL destinationURL) {
    this.businessExecutor = Executors.newFixedThreadPool(3,
        new ThreadFactoryBuilder()
            .setNameFormat("business-pool-%d")
            .setDaemon(true)
            .build());

    Dispatcher dispatcher = new Dispatcher(Executors.newFixedThreadPool(2,
        new ThreadFactoryBuilder()
            .setNameFormat("okhttp-pool-%d")
            .setDaemon(true)
            .build()));

    this.httpClient = new OkHttpClient.Builder()
        .dispatcher(dispatcher)
        .connectionPool(new ConnectionPool(5, 3, TimeUnit.MINUTES))
        .connectTimeout(3, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .retryOnConnectionFailure(true)
        .build();

    this.apiCoder = apiCoder;
    this.messageCoder = messageCoder;
    this.responseApiClz = responseApiClz;
    this.destinationURL = destinationURL;
  }

  @Override
  protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) {
    logger.debug("BackOfficeHandler read start");

    RequestContext context = new RequestContext(ctx, msg);

    logger.info("[stage1/5] Netty received: thread={}, rawMessage={}",
        Thread.currentThread().getName(), msg);

    businessExecutor.submit(() -> processBusinessPre(context));
  }

  private void processBusinessPre(RequestContext context) {
    context.markBusinessPre();
    logger.info("[stage2/5] processBusinessPre start: thread={}", Thread.currentThread().getName());

    try {

      //
      Api api = apiCoder.messageToApi(messageCoder.extract(context.originalRequest));
      context.businessData.put("backendUrl", destinationURL);
      context.businessData.put("requestBody", api);

      //
      callBackendApi(context);

    } catch (Exception e) {
      logger.error("业务预处理失败", e);
      sendErrorResponse(context, "业务处理失败: " + e.getMessage());
    }
  }

  private void callBackendApi(RequestContext context) {
    context.markHttpStart();
    logger.info("[stage3/5] HTTP invoke: thread={}", Thread.currentThread().getName());

    ObjectMapper mapper = new ObjectMapper();
    MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    Api api = (Api) context.businessData.get("requestBody");
    RequestBody requestBody;
    try {
      String json = mapper.writeValueAsString(api);
      requestBody = RequestBody.create(json, JSON);
    } catch (IOException e) {
      throw new RuntimeException("对象转JSON失败", e);
    }


    try {
      //
      Request request = new Request.Builder()
          .url(destinationURL.getUrlString())
          .post(requestBody)
//          .addHeader("X-Trace-ID", context.ctx.channel().id().asShortText())
//          .addHeader("X-User-ID", (String) context.businessData.get("userId"))
          .build();

      //
      httpClient.newCall(request).enqueue(new Callback() {
        @Override
        public void onFailure(Call call, IOException e) {
          //
          logger.error("[stage4/5] HTTP invoke failure: thread={}, error={}",
              Thread.currentThread().getName(), e.getMessage());
          //
          businessExecutor.submit(() -> handleHttpError(context, e));
        }

        @Override
        public void onResponse(Call call, Response response) {
          //
          context.markHttpEnd();
          logger.info("[stage4/5] HTTP response: thread={}, statu scode={}",
              Thread.currentThread().getName(), response.code());
          //
          businessExecutor.submit(() -> processBusinessPost(context, response));
        }
      });

    } catch (Exception e) {
      logger.error("发起HTTP调用失败", e);
      handleHttpError(context, e);
    }
  }

  private void processBusinessPost(RequestContext context, Response httpResponse) {
    context.markBusinessPost();
    logger.info("[阶段4/5] 业务后处理开始: 线程={}", Thread.currentThread().getName());

    try {

      //
      String responseBody = httpResponse.body().string();

      //
      ObjectMapper mapper = new ObjectMapper();
      Api api;
      try {
        api = mapper.readValue(responseBody, responseApiClz);
      } catch (Exception e) {
        logger.error("jackson parser read error", e);
        throw new HandlerException("jackson parser read error");
      }
      Message message = apiCoder.apiToMessage(api);
      ByteBuf result = Unpooled.unreleasableBuffer(messageCoder.compress(message));

      //
      sendSuccessResponse(context, result);

    } catch (Exception e) {
      logger.error("业务后处理失败", e);
      sendErrorResponse(context, "后处理失败: " + e.getMessage());
    }
  }

  private void sendSuccessResponse(RequestContext context, ByteBuf result) {
    //
    EventLoop nettyEventLoop = context.ctx.channel().eventLoop();

    logger.info("[阶段5/5] 提交写回任务: 当前线程={}, 目标线程={}",
        Thread.currentThread().getName(), nettyEventLoop);

    //
    nettyEventLoop.execute(() -> {
      //
      logger.info("[阶段5/5] Netty写回: 线程={}", Thread.currentThread().getName());

      context.ctx.writeAndFlush(result)
          .addListener((ChannelFutureListener) future -> {
            if (future.isSuccess()) {
              context.logPerformance();
              logger.info("响应发送成功, 耗时: {}ms",
                  System.currentTimeMillis() - context.startTime);
            } else {
              logger.error("响应发送失败", future.cause());
            }
          });
    });
  }

  private void sendErrorResponse(RequestContext context, String errorMessage) {
    logger.error("error response", errorMessage);
  }

  private void handleHttpError(RequestContext context, Exception e) {
    logger.error("HTTP调用失败", e);
    sendErrorResponse(context, "后台服务调用失败: " + e.getMessage());
  }
}
