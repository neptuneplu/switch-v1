package me.card.switchv1.visaserver.config;

import javax.annotation.Resource;
import me.card.switchv1.core.component.ApiCoder;
import me.card.switchv1.core.component.DefaultId;
import me.card.switchv1.core.component.Id;
import me.card.switchv1.core.component.Message;
import me.card.switchv1.core.component.PersistentWorker;
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
public class VisaInternalConfig {

  @Resource
  VisaPersistentWorkerByDB visaPersistentWorkerByDB;

  @Bean
  public SwitchServerBuilder switchServerBuilder() {
    return new SwitchServerBuilder();
  }

  @Bean
  public ApiCoder<VisaApi, VisaMessageByJpos> apiCoder() {
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
  public Class<VisaApi> apiClz() {
    return VisaApi.class;
  }

  @Bean
  public PersistentWorker persistentWorker() {
//    return new VisaPersistentWorkerByLog();
    return visaPersistentWorkerByDB;
  }

  @Bean
  public Id id() {
    return new DefaultId();
  }


  // for check iso packager
  @Bean
  public Message message() {
    return new VisaMessageByJpos();
  }


}
