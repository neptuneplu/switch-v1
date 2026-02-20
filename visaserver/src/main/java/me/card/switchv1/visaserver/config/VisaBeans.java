package me.card.switchv1.visaserver.config;

import me.card.switchv1.core.component.ApiCoder;
import me.card.switchv1.core.component.DefaultId;
import me.card.switchv1.core.component.DefaultMessageCoder;
import me.card.switchv1.core.component.Id;
import me.card.switchv1.core.component.MessageCoder;
import me.card.switchv1.core.processor.ProcessorBuilder;
import me.card.switchv1.core.connector.SchemeConnectorBuilder;
import me.card.switchv1.visaapi.VisaApi;
import me.card.switchv1.visaserver.message.VisaHeartBeat;
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
