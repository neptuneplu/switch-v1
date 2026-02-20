package me.card.switchv1.cupserver;

import javax.annotation.Resource;
import me.card.switchv1.core.component.Api;
import me.card.switchv1.core.component.ApiCoder;
import me.card.switchv1.core.component.HeartBeat;
import me.card.switchv1.core.component.Id;
import me.card.switchv1.core.component.Message;
import me.card.switchv1.core.component.Prefix;
import me.card.switchv1.core.connector.ConnectorMonitor;
import me.card.switchv1.core.connector.Connector;
import me.card.switchv1.core.connector.SchemeConnectorBuilder;
import me.card.switchv1.cupserver.config.CupParams;
import me.card.switchv1.cupserver.message.jpos.CupMessageByJpos;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component
public class CupManager {
  private static final Logger logger = LoggerFactory.getLogger(CupManager.class);

  @Resource
  private CupParams cupConfig;

  @Resource
  private ApiCoder<Api, Message> apiCoder;

  @Resource
  private HeartBeat heartBeat;

  @Resource
  private Prefix prefix;

  @Resource
  private Class<Api> apiClz;

  @Resource
  private SchemeConnectorBuilder schemeConnectorBuilder;

  @Resource
  private Id id;

  private Connector connector;


  public ConnectorMonitor start() {

    try {
      startCheck();
    } catch (IllegalArgumentException e) {
      return getNewServerMonitor(e.getMessage());
    }

    getServer().start();
    return getNewServerMonitor("cup server is starting");
  }

  public ConnectorMonitor stop() {

    try {
      stopCheck();
    } catch (IllegalArgumentException e) {
      return getNewServerMonitor(e.getMessage());
    }

    connector.stop();
    connector = null;
    return getNewServerMonitor("cup server is stopping");
  }

  public ConnectorMonitor status() {
    try {
      statusCheck();
    } catch (IllegalArgumentException e) {
      return getNewServerMonitor(e.getMessage());
    }
    return connector.status();
  }

  private void startCheck() {
    Assert.isNull(connector, "cup server already exist");
    Assert.notNull(schemeConnectorBuilder, "switchServerBuilder is null");
    Assert.notNull(cupConfig, "cupConfig is null");
    Assert.notNull(apiCoder, "apiCoder is null");
    Assert.notNull(heartBeat, "heartBeat is null");
    Assert.notNull(prefix, "prefix is null");
    Assert.notNull(apiClz, "apiClz is null");
  }

  private void stopCheck() {
    Assert.notNull(connector, "cup server not start");
  }

  private void statusCheck() {
    Assert.notNull(connector, "cup server not start");
  }

  private Connector getServer() {
    connector = schemeConnectorBuilder
        .name(cupConfig.name())
        .connectorType(cupConfig.connectorType())
        .localAddress(cupConfig.localAddress())
        .sourceAddress(cupConfig.sourceAddress())
        .destinationURL(cupConfig.destinationURL())
        .readIdleTime(cupConfig.readIdleTime())
        .prefix(prefix)
        .heartBeat(heartBeat)
        .messageSupplier(CupMessageByJpos::new)
        .build();

    return connector;
  }

  public ConnectorMonitor getNewServerMonitor(String desc) {
    ConnectorMonitor connectorMonitor = new ConnectorMonitor();
    connectorMonitor.setDesc(desc);
    return connectorMonitor;
  }


}
