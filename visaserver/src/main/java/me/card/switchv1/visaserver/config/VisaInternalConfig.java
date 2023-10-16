package me.card.switchv1.visaserver.config;

import me.card.switchv1.core.SwitchServerBuilder;
import me.card.switchv1.core.component.Message;
import me.card.switchv1.core.component.PersistentWorker;
import me.card.switchv1.visaapi.VisaApi;
import me.card.switchv1.visaserver.message.VisaHeartBeat;
import me.card.switchv1.visaserver.message.VisaPersistentWorker;
import me.card.switchv1.visaserver.message.VisaPrefix;
import me.card.switchv1.visaserver.message.jpos.VisaApiCoder;
import me.card.switchv1.visaserver.message.jpos.VisaMessageByJpos;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class VisaInternalConfig {

  @Bean
  public SwitchServerBuilder switchServerBuilder() {
    return new SwitchServerBuilder();
  }

  @Bean
  public VisaApiCoder visaApiCoder() {
    return new VisaApiCoder();
  }

  @Bean
  public VisaHeartBeat visaHeartBeat() {
    return new VisaHeartBeat();
  }

  @Bean
  public VisaPrefix visaPrefix() {
    return new VisaPrefix();
  }

  @Bean
  public Class<VisaApi> api() {
    return VisaApi.class;
  }

  @Bean
  public PersistentWorker persistentWorker() {
    return new VisaPersistentWorker();
  }

  // for check iso packager
  @Bean
  public Message message() {
    return new VisaMessageByJpos();
  }


}
