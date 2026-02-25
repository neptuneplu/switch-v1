package me.card.switchv1.core.client;


import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import me.card.switchv1.component.Api;
import me.card.switchv1.core.internal.MessageContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultApiClient implements ApiClient {
  private static final Logger logger = LoggerFactory.getLogger(DefaultApiClient.class);
  private static final ObjectMapper mapper = new ObjectMapper();
  private static final int DEFAULT_TIMEOUT_SECONDS = 30;

  private final HttpClient httpClient;

  public DefaultApiClient() {
    this.httpClient = HttpClient.newBuilder()
        .connectTimeout(Duration.ofSeconds(10))
        .build();

  }

  public CompletableFuture<Api> call(MessageContext context) {

    context.markRemoteStart();

    logger.info("[stage DefaultApiClient] HTTP invoke start: thread={}",
        Thread.currentThread().getName());

    return httpClient.sendAsync(getRequest(context), HttpResponse.BodyHandlers.ofString())
        .thenApply(response -> {
          context.markRemoteEnd();
          return str2api(getBody(response), context.getResponseApiClz());
        })
        .exceptionally(ex -> {
          logger.error("[stage DefaultApiClient] HTTP invoke exception: ", ex);
          //todo
          throw new ClientException("HTTP invoke exception");
        });
  }

  private HttpRequest getRequest(MessageContext context) {
    String str = api2str(context.getIncomeApi());

    return HttpRequest.newBuilder()
        .uri(URI.create(context.getDestinationURL().getUrlString()))
        .timeout(Duration.ofSeconds(DEFAULT_TIMEOUT_SECONDS))
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

