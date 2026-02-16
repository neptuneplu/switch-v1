package me.card.switchv1.core.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import me.card.switchv1.core.component.Api;
import me.card.switchv1.core.component.RequestContext;
import me.card.switchv1.core.handler.HandlerException;
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

public class ApiClientOkHttp implements ApiClient {
  private static final Logger logger = LoggerFactory.getLogger(ApiClientOkHttp.class);

  private final OkHttpClient httpClient;
  protected Class<Api> responseApiClz;

  public ApiClientOkHttp(Class<Api> responseApiClz) {
    Dispatcher dispatcher = new Dispatcher(Executors.newFixedThreadPool(2,
        new ThreadFactoryBuilder()
            .setNameFormat("okhttp-pool-%d")
            .setDaemon(true)
            .build()));

    this.httpClient = new OkHttpClient.Builder()
        .dispatcher(dispatcher)
        .connectionPool(new ConnectionPool(5, 3, TimeUnit.MINUTES))
        .connectTimeout(3, TimeUnit.SECONDS)
        .readTimeout(5, TimeUnit.SECONDS)
        .writeTimeout(5, TimeUnit.SECONDS)
        .retryOnConnectionFailure(true)
        .build();

    this.responseApiClz = responseApiClz;
  }


  public void call(RequestContext context,
                   Consumer<RequestContext> responseConsumer,
                   Consumer<RequestContext> errorConsumer) {
    context.markHttpStart();
    logger.info("[stage3/5] HTTP invoke: thread={}", Thread.currentThread().getName());

    ObjectMapper mapper = new ObjectMapper();
    MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    Api api = (Api) context.getBusinessData().get("requestApi");
    RequestBody requestBody;
    try {
      String json = mapper.writeValueAsString(api);
      requestBody = RequestBody.create(json, JSON);
    } catch (IOException e) {
      throw new RuntimeException("对象转JSON失败", e);
    }


    try {
      //
      Request request = getRequest(context, requestBody);

      //
      httpClient.newCall(request).enqueue(new Callback() {
        @Override
        public void onFailure(Call call, IOException e) {
          //
          logger.error("[stage4/5] HTTP invoke failure: thread={}, error={}",
              Thread.currentThread().getName(), e.getMessage());
          //
          context.setError(e);
          errorConsumer.accept(context);
        }

        @Override
        public void onResponse(Call call, Response response) {
          // response to api
          try {
            String responseBody = response.body().string();
            ObjectMapper mapper = new ObjectMapper();
            Api api;
            api = mapper.readValue(responseBody, responseApiClz);
            context.getBusinessData().put("responseApi", api);
          } catch (Exception e) {
            logger.error("jackson parser read error", e);
            throw new HandlerException("jackson parser read error");
          }

          //
          context.markHttpEnd();
          logger.info("[stage4/5] HTTP invoke successful: thread={}, statu scode={}",
              Thread.currentThread().getName(), response.code());
          //
          responseConsumer.accept(context);
        }
      });

    } catch (Exception e) {
      logger.error("发起HTTP调用失败", e);
      handleHttpError(context, e);
    }
  }

  private void handleHttpError(RequestContext context, Exception e) {
    logger.error("HTTP调用失败", e);
    sendErrorResponse(context, "后台服务调用失败: " + e.getMessage());
  }

  private void sendErrorResponse(RequestContext context, String errorMessage) {
    logger.error("error response", errorMessage);
  }

  private Request getRequest(RequestContext context, RequestBody requestBody) {
    return new Request.Builder()
        .url(context.getDestinationURL().getUrlString())
        .post(requestBody)
//          .addHeader("X-Trace-ID", context.ctx.channel().id().asShortText())
//          .addHeader("X-User-ID", (String) context.businessData.get("userId"))
        .build();
  }
}
