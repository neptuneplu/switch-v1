package me.card.switchv1.cupserver.config;

import me.card.switchv1.core.SwitchServerBuilder;
import me.card.switchv1.core.component.Message;
import me.card.switchv1.core.component.PersistentWorker;
import me.card.switchv1.cupapi.CupApi;
import me.card.switchv1.cupserver.message.CupHeartBeat;
import me.card.switchv1.cupserver.message.CupPersistentWorker;
import me.card.switchv1.cupserver.message.CupPrefix;
import me.card.switchv1.cupserver.message.jpos.CupApiCoder;
import me.card.switchv1.cupserver.message.jpos.CupMessageByJpos;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CupInternalConfig {


  @Bean
  public SwitchServerBuilder switchServerBuilder() {
    return new SwitchServerBuilder();
  }

  @Bean
  public CupApiCoder cupApiCoder() {
    return new CupApiCoder();
  }

  @Bean
  public CupHeartBeat cupHeartBeat() {
    return new CupHeartBeat();
  }

  @Bean
  public CupPrefix cupPrefix() {
    return new CupPrefix();
  }

  @Bean
  public Class<CupApi> api() {
    return CupApi.class;
  }

  @Bean
  public PersistentWorker persistentWorker() {
    return new CupPersistentWorker();
  }

  // for check iso packager
  @Bean
  public Message message() {
    return new CupMessageByJpos();
  }
}
