package me.card.switchv1.visaserver;

import javax.annotation.Resource;
import me.card.switchv1.core.ServerMonitor;
import me.card.switchv1.core.SwitchServer;
import me.card.switchv1.core.SwitchServerBuilder;
import me.card.switchv1.core.component.ApiCoder;
import me.card.switchv1.core.component.HeartBeat;
import me.card.switchv1.core.component.Id;
import me.card.switchv1.core.component.PersistentWorker;
import me.card.switchv1.core.component.Prefix;
import me.card.switchv1.visaapi.VisaApi;
import me.card.switchv1.visaserver.config.VisaExternalConfig;
import me.card.switchv1.visaserver.message.jpos.VisaMessageByJpos;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component
public class VisaStarter {
  private static final Logger logger = LoggerFactory.getLogger(VisaStarter.class);

  @Resource
  private VisaExternalConfig visaConfig;

  @Resource
  private ApiCoder apiCoder;

  @Resource
  private HeartBeat heartBeat;

  @Resource
  private Prefix prefix;

  @Resource
  private Class<VisaApi> apiClz;

  @Resource
  private PersistentWorker persistentWorker;

  @Resource
  private Id id;

  @Resource
  private SwitchServerBuilder switchServerBuilder;


  private SwitchServer switchServer;


  public void start() {

    Assert.notNull(switchServerBuilder, "switchServerBuilder is null");
    Assert.notNull(visaConfig, "visaConfig is null");
    Assert.notNull(apiCoder, "apiCoder is null");
    Assert.notNull(heartBeat, "heartBeat is null");
    Assert.notNull(prefix, "prefix is null");
    Assert.notNull(apiClz, "apiClz is null");

    switchServer = switchServerBuilder
        .name(visaConfig.name())
        .serverType(visaConfig.serverType())
        .localAddress(visaConfig.localAddress())
        .sourceAddress(visaConfig.sourceAddress())
        .destinationURL(visaConfig.destinationURL())
        .readIdleTime(visaConfig.readIdleTime())
        .prefix(prefix)
        .heartBeat(heartBeat)
        .messageSupplier(VisaMessageByJpos::new)
        .apiCoder(apiCoder)
        .responseApiClz(apiClz)
        .persistentWorker(persistentWorker)
        .id(id)
        .build();

    switchServer.start();
  }

  public void stop() {
    Assert.notNull(switchServer, "visa server not start");

    switchServer.stop();
  }

  public ServerMonitor status() {
    return switchServer.status();
  }

  public boolean isServerNull() {
    return switchServer == null;
  }


}
