package me.card.switchv1.core.client.okhttp;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import me.card.switchv1.core.client.ApiClient;
import me.card.switchv1.core.client.ClientException;
import me.card.switchv1.core.component.Api;
import me.card.switchv1.core.component.RequestContext;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.ConnectionPool;
import okhttp3.Dispatcher;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ApiClientOkHttp implements ApiClient {
  private static final Logger logger = LoggerFactory.getLogger(ApiClientOkHttp.class);
  private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
  private static final ObjectMapper mapper = new ObjectMapper();

  private final OkHttpClient httpClient;

  public ApiClientOkHttp() {
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

  }

  public void call(RequestContext context,
                      Consumer<RequestContext> responseConsumer,
                      Consumer<RequestContext> errorConsumer) {

    context.markHttpStart();

    logger.info("[stage3/5] iss HTTP invoke: thread={}", Thread.currentThread().getName());

    try {
      Request request = getRequest(context, fromApi(context.getRequestApi()));

      httpClient.newCall(request).enqueue(new Callback() {

        // backoffice pcs failure
        @Override
        public void onFailure(@NotNull Call call, @NotNull IOException e) {

          logger.error("[stage4/5] HTTP invoke failure: thread={}, error={}",
              Thread.currentThread().getName(), e.getMessage());

          context.setError(e);
          errorConsumer.accept(context);
        }

        // backoffice pcs successful
        @Override
        public void onResponse(@NotNull Call call, @NotNull Response response) {
          //
          context.markHttpEnd();

          logger.info("[stage4/5] HTTP invoke successful: thread={}, status code={}",
              Thread.currentThread().getName(), response.code());

          // todo should check status code first
          if (response.isSuccessful() && response.body() != null) {
            context.setReponseApi(toApi(response.body(), context.getResponseApiClz()));

          }
          //
          responseConsumer.accept(context);
        }
      });

    } catch (Exception e) {
      logger.error("HTTP调用失败", e);
      context.setError(e);
      errorConsumer.accept(context);
    }
  }



  private Request getRequest(RequestContext context, RequestBody requestBody) {
    return new Request.Builder()
        .url(context.getDestinationURL().getUrlString())
        .post(requestBody)
        /*
        .addHeader("X-Trace-ID", context.ctx.channel().id().asShortText())
        .addHeader("X-User-ID", (String) context.businessData.get("userId"))
         */
        .build();
  }

  public static RequestBody fromApi(Api api) {
    try {
      String json = mapper.writeValueAsString(api);
      return RequestBody.create(json, JSON);
    } catch (IOException e) {
      throw new ClientException("jackson api to request body error");
    }
  }

  public static Api toApi(ResponseBody responseBody, Class<Api> responseApiClz) {
    try {
      String strResponseBody = responseBody.string();
      return mapper.readValue(strResponseBody, responseApiClz);
    } catch (IOException e) {
      throw new ClientException("jackson response body to api error");
    }

  }
}
