package me.card.switchv1.visaserver.config;

import me.card.switchv1.core.client.ApiClient;
import me.card.switchv1.core.client.okhttp.ApiClientOkHttp;
import me.card.switchv1.core.component.Api;
import me.card.switchv1.core.component.ApiCoder;
import me.card.switchv1.core.component.DefaultId;
import me.card.switchv1.core.component.DefaultMessageCoder;
import me.card.switchv1.core.component.Id;
import me.card.switchv1.core.component.Message;
import me.card.switchv1.core.component.MessageCoder;
import me.card.switchv1.core.processor.DefaultProcessor;
import me.card.switchv1.core.processor.Processor;
import me.card.switchv1.core.server.SwitchServerBuilder;
import me.card.switchv1.visaapi.VisaApi;
import me.card.switchv1.visaserver.message.VisaHeartBeat;
import me.card.switchv1.visaserver.message.VisaPersistentWorkerByDB;
import me.card.switchv1.visaserver.message.VisaPrefix;
import me.card.switchv1.visaserver.message.jpos.VisaApiCoder;
import me.card.switchv1.visaserver.message.jpos.VisaMessageByJpos;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class VisaBeans {

  @Bean
  public VisaHeartBeat visaHeartBeat() {
    return new VisaHeartBeat();
  }

  @Bean
  public VisaPrefix visaPrefix() {
    return new VisaPrefix();
  }

  @Bean
  public Class<VisaApi> apiClz() {
    return VisaApi.class;
  }

  @Bean
  public ApiCoder<VisaApi, VisaMessageByJpos> apiCoder() {
    return new VisaApiCoder();
  }

  // for check iso packager
  @Bean
  public Message message() {
    return new VisaMessageByJpos();
  }

  @Bean
  public Id id() {
    return new DefaultId();
  }

  @Bean
  public MessageCoder messageCoder() {
    return new DefaultMessageCoder(VisaMessageByJpos::new, id());
  }

  @Bean
  public VisaPersistentWorkerByDB persistentWorker() {
    return new VisaPersistentWorkerByDB();
  }

  @Bean
  public ApiClient apiClient() {
    return new ApiClientOkHttp((Class<Api>) (Class<?>) apiClz());
  }

  @Bean
  public Processor processor() {
    return new DefaultProcessor(apiCoder(), messageCoder(), apiClient(), persistentWorker());
  }

  @Bean
  public SwitchServerBuilder switchServerBuilder() {
    return new SwitchServerBuilder();
  }
}
