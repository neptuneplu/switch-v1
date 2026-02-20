package me.card.switchv1.core.client;


import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import me.card.switchv1.core.component.Api;
import me.card.switchv1.core.component.MessageContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultApiClient implements ApiClient {
  private static final Logger logger = LoggerFactory.getLogger(DefaultApiClient.class);
  private static final ObjectMapper mapper = new ObjectMapper();

  private final HttpClient httpClient;
  private final int defaultTimeoutSeconds = 30;

  public DefaultApiClient() {
    this.httpClient = HttpClient.newBuilder()
        .connectTimeout(Duration.ofSeconds(10))
//        .executor(Executors.newFixedThreadPool(20))o
        .build();

  }

  public CompletableFuture<Api> call(MessageContext context) {

    context.markHttpStart();

    logger.info("[stage3/5] iss HTTP invoke: thread={}", Thread.currentThread().getName());

    return httpClient.sendAsync(getRequest(context), HttpResponse.BodyHandlers.ofString())
        .thenApply(response -> str2api(getBody(response), context.getResponseApiClz()))
        .exceptionally(ex -> {
          logger.error("[stage3/5] iss HTTP exception: ", ex);
          //todo
          throw new ClientException("HTTP 请求失败");
        });
  }

  private HttpRequest getRequest(MessageContext context) {
    String str = api2str(context.getIncomeApi());

    return HttpRequest.newBuilder()
        .uri(URI.create(context.getDestinationURL().getUrlString()))
        .timeout(Duration.ofSeconds(defaultTimeoutSeconds))
        .header("Content-Type", "application/json")
        .header("Accept", "application/json")
        .POST(HttpRequest.BodyPublishers.ofString(str))
        .build();
  }

  private String getBody(HttpResponse<String> response) {
    if (response.statusCode() >= 200 && response.statusCode() < 300) {
      return response.body();
    } else {
      throw new ClientException(
          String.format("Backoffice response not ok, HTTP status code %d, body %s",
              response.statusCode(), response.body()));
    }
  }

  public String api2str(Api api) {
    try {
      return mapper.writeValueAsString(api);
    } catch (IOException e) {
      throw new ClientException("jackson api to request body error");
    }
  }

  public Api str2api(String str, Class<? extends Api> responseApiClz) {
    try {
      return mapper.readValue(str, responseApiClz);
    } catch (IOException e) {
      throw new ClientException("jackson response body to api error");
    }

  }
}

