package me.card.switchv1.app.config;


import me.card.switchv1.component.ApiCoder;
import me.card.switchv1.component.DefaultId;
import me.card.switchv1.component.DefaultMessageCoder;
import me.card.switchv1.component.Id;
import me.card.switchv1.component.MessageCoder;
import me.card.switchv1.core.processor.ProcessorBuilder;
import me.card.switchv1.core.connector.SchemeConnectorBuilder;
import me.card.switchv1.api.visa.VisaApi;
import me.card.switchv1.message.visa.VisaHeartBeat;
import me.card.switchv1.message.visa.VisaPrefix;
import me.card.switchv1.message.visa.jpos.VisaApiCoder;
import me.card.switchv1.message.visa.jpos.VisaMessageByJpos;
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

  // for check iso packager
  @Bean
  public VisaMessageByJpos message() {
    return new VisaMessageByJpos();
  }

  @Bean
  public Id id() {
    return new DefaultId();
  }

  @Bean
  public Class<VisaApi> apiClz() {
    return VisaApi.class;
  }

  @Bean
  public ApiCoder<VisaApi, VisaMessageByJpos> apiCoder() {
    return new VisaApiCoder();
  }

  @Bean
  public MessageCoder messageCoder() {
    return new DefaultMessageCoder(VisaMessageByJpos::new, id());
  }

  @Bean
  public SchemeConnectorBuilder switchServerBuilder() {
    return new SchemeConnectorBuilder();
  }

  @Bean
  public ProcessorBuilder processorBuilder() {
    return new ProcessorBuilder();
  }
}
