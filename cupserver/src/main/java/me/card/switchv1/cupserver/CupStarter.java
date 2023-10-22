package me.card.switchv1.cupserver;

import javax.annotation.Resource;
import me.card.switchv1.core.server.ServerMonitor;
import me.card.switchv1.core.server.SwitchServer;
import me.card.switchv1.core.server.SwitchServerBuilder;
import me.card.switchv1.core.component.ApiCoder;
import me.card.switchv1.core.component.HeartBeat;
import me.card.switchv1.core.component.PersistentWorker;
import me.card.switchv1.core.component.Prefix;
import me.card.switchv1.cupapi.CupApi;
import me.card.switchv1.cupserver.config.CupExternalConfig;
import me.card.switchv1.cupserver.message.jpos.CupMessageByJpos;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component
public class CupStarter {
  private static final Logger logger = LoggerFactory.getLogger(CupStarter.class);

  @Resource
  private CupExternalConfig cupConfig;

  @Resource
  private ApiCoder apiCoder;

  @Resource
  private HeartBeat heartBeat;

  @Resource
  private Prefix prefix;

  @Resource
  private Class<CupApi> apiClz;

  @Resource
  private PersistentWorker persistentWorker;

  @Resource
  private SwitchServerBuilder switchServerBuilder;

  private SwitchServer switchServer;


  public void start() {

    Assert.notNull(switchServerBuilder, "switchServerBuilder is null");
    Assert.notNull(cupConfig, "cupConfig is null");
    Assert.notNull(apiCoder, "apiCoder is null");
    Assert.notNull(heartBeat, "heartBeat is null");
    Assert.notNull(prefix, "prefix is null");
    Assert.notNull(apiClz, "apiClz is null");

    switchServer = switchServerBuilder
        .name(cupConfig.name())
        .serverType(cupConfig.serverType())
        .localAddress(cupConfig.localAddress())
        .sourceAddress(cupConfig.sourceAddress())
        .destinationURL(cupConfig.destinationURL())
        .readIdleTime(cupConfig.readIdleTime())
        .prefix(prefix)
        .heartBeat(heartBeat)
        .messageSupplier(CupMessageByJpos::new)
        .apiCoder(apiCoder)
        .responseApiClz(apiClz)
        .persistentWorker(persistentWorker)
        .build();

    switchServer.start();
  }

  public void stop() {
    Assert.notNull(switchServer, "cup server not start");

    switchServer.stop();
  }

  public ServerMonitor status() {
    return switchServer.status();
  }

  public boolean isServerNull() {
    return switchServer == null;
  }


}
