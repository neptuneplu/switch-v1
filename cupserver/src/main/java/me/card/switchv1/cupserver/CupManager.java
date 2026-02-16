package me.card.switchv1.cupserver;

import javax.annotation.Resource;
import me.card.switchv1.core.component.Api;
import me.card.switchv1.core.component.ApiCoder;
import me.card.switchv1.core.component.HeartBeat;
import me.card.switchv1.core.component.Id;
import me.card.switchv1.core.component.Message;
import me.card.switchv1.core.component.PersistentWorker;
import me.card.switchv1.core.component.Prefix;
import me.card.switchv1.core.server.ServerMonitor;
import me.card.switchv1.core.server.SwitchServer;
import me.card.switchv1.core.server.SwitchServerBuilder;
import me.card.switchv1.cupserver.config.CupExternalConfig;
import me.card.switchv1.cupserver.message.jpos.CupMessageByJpos;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component
public class CupManager {
  private static final Logger logger = LoggerFactory.getLogger(CupManager.class);

  @Resource
  private CupExternalConfig cupConfig;

  @Resource
  private ApiCoder<Api, Message> apiCoder;

  @Resource
  private HeartBeat heartBeat;

  @Resource
  private Prefix prefix;

  @Resource
  private Class<Api> apiClz;

  @Resource
  private PersistentWorker persistentWorker;

  @Resource
  private SwitchServerBuilder switchServerBuilder;

  @Resource
  private Id id;

  private SwitchServer switchServer;


  public ServerMonitor start() {

    try {
      startCheck();
    } catch (IllegalArgumentException e) {
      return getNewServerMonitor(e.getMessage());
    }

    getServer().start();
    return getNewServerMonitor("cup server is starting");
  }

  public ServerMonitor stop() {

    try {
      stopCheck();
    } catch (IllegalArgumentException e) {
      return getNewServerMonitor(e.getMessage());
    }

    switchServer.stop();
    switchServer = null;
    return getNewServerMonitor("cup server is stopping");
  }

  public ServerMonitor status() {
    try {
      statusCheck();
    } catch (IllegalArgumentException e) {
      return getNewServerMonitor(e.getMessage());
    }
    return switchServer.status();
  }

  private void startCheck() {
    Assert.isNull(switchServer, "cup server already exist");
    Assert.notNull(switchServerBuilder, "switchServerBuilder is null");
    Assert.notNull(cupConfig, "cupConfig is null");
    Assert.notNull(apiCoder, "apiCoder is null");
    Assert.notNull(heartBeat, "heartBeat is null");
    Assert.notNull(prefix, "prefix is null");
    Assert.notNull(apiClz, "apiClz is null");
  }

  private void stopCheck() {
    Assert.notNull(switchServer, "cup server not start");
  }

  private void statusCheck() {
    Assert.notNull(switchServer, "cup server not start");
  }

  private SwitchServer getServer() {
    switchServer = switchServerBuilder
        .name(cupConfig.name())
        .serverType(cupConfig.serverType())
        .localAddress(cupConfig.localAddress())
        .sourceAddress(cupConfig.sourceAddress())
        .destinationURL(cupConfig.destinationURL())
        .readIdleTime(cupConfig.readIdleTime())
        .processorThreads(cupConfig.processorThreads())
        .persistentThreads(cupConfig.persistentThreads())
        .prefix(prefix)
        .heartBeat(heartBeat)
        .messageSupplier(CupMessageByJpos::new)
        .apiCoder(apiCoder)
        .responseApiClz(apiClz)
        .persistentWorker(persistentWorker)
        .id(id)
        .build();

    return switchServer;
  }

  public ServerMonitor getNewServerMonitor(String desc) {
    ServerMonitor serverMonitor = new ServerMonitor();
    serverMonitor.setDesc(desc);
    return serverMonitor;
  }


}
