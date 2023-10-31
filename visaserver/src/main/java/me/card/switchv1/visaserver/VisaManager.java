package me.card.switchv1.visaserver;

import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import me.card.switchv1.core.component.Api;
import me.card.switchv1.core.component.ApiCoder;
import me.card.switchv1.core.component.DefaultMessageCoder;
import me.card.switchv1.core.component.HeartBeat;
import me.card.switchv1.core.component.Id;
import me.card.switchv1.core.component.Message;
import me.card.switchv1.core.component.MessageCoder;
import me.card.switchv1.core.component.PersistentWorker;
import me.card.switchv1.core.component.Prefix;
import me.card.switchv1.core.server.ServerException;
import me.card.switchv1.core.server.ServerMonitor;
import me.card.switchv1.core.server.SwitchServer;
import me.card.switchv1.core.server.SwitchServerBuilder;
import me.card.switchv1.visaapi.VisaApi;
import me.card.switchv1.visaserver.config.VisaExternalConfig;
import me.card.switchv1.visaserver.db.VisaLogPo;
import me.card.switchv1.visaserver.db.VisaLogService;
import me.card.switchv1.visaserver.message.SignOnAndOffMessage;
import me.card.switchv1.visaserver.message.jpos.VisaMessageByJpos;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component
public class VisaManager {
  private static final Logger logger = LoggerFactory.getLogger(VisaManager.class);

  @Resource
  private VisaExternalConfig visaConfig;

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
  private Id id;

  @Resource
  private SwitchServerBuilder switchServerBuilder;

  @Resource
  private VisaLogService visaLogService;

  private SwitchServer switchServer;

  private MessageCoder messageCoder;


  public ServerMonitor start() {

    try {
      startCheck();
    } catch (IllegalArgumentException e) {
      return getNewServerMonitor(e.getMessage());
    }

    getServer().start();
    return getNewServerMonitor("visa server is starting");
  }

  public ServerMonitor stop() {

    try {
      stopCheck();
    } catch (IllegalArgumentException e) {
      return getNewServerMonitor(e.getMessage());
    }

    switchServer.stop();
    switchServer = null;
    return getNewServerMonitor("visa server is stopping");
  }

  public ServerMonitor status() {
    try {
      statusCheck();
    } catch (IllegalArgumentException e) {
      return getNewServerMonitor(e.getMessage());
    }
    return switchServer.status();
  }

  public ServerMonitor signOn() {
    try {
      statusCheck();
      switchServer.signOn();
    } catch (IllegalArgumentException | ServerException e) {
      return getNewServerMonitor(e.getMessage());
    }
    return getNewServerMonitor("visa server signOn message send");
  }

  public ServerMonitor signOff() {
    try {
      statusCheck();
      switchServer.signOff();
    } catch (IllegalArgumentException | ServerException e) {
      return getNewServerMonitor(e.getMessage());
    }
    return getNewServerMonitor("visa server signOff message send");
  }

  private void startCheck() {
    Assert.isNull(switchServer, "visa server already exist");
    Assert.notNull(switchServerBuilder, "switchServerBuilder is null");
    Assert.notNull(visaConfig, "visaConfig is null");
    Assert.notNull(apiCoder, "apiCoder is null");
    Assert.notNull(heartBeat, "heartBeat is null");
    Assert.notNull(prefix, "prefix is null");
    Assert.notNull(apiClz, "apiClz is null");
    Assert.notNull(persistentWorker, "persistentWorker is null");

  }

  private void stopCheck() {
    Assert.notNull(switchServer, "visa server not start");
  }

  private void statusCheck() {
    Assert.notNull(switchServer, "visa server not start");
  }

  private SwitchServer getServer() {
    switchServer = switchServerBuilder
        .name(visaConfig.name())
        .serverType(visaConfig.serverType())
        .localAddress(visaConfig.localAddress())
        .sourceAddress(visaConfig.sourceAddress())
        .destinationURL(visaConfig.destinationURL())
        .readIdleTime(visaConfig.readIdleTime())
        .sendThreads(visaConfig.sendThreads())
        .persistentThreads(visaConfig.persistentThreads())
        .prefix(prefix)
        .heartBeat(heartBeat)
        .messageSupplier(VisaMessageByJpos::new)
        .apiCoder(apiCoder)
        .responseApiClz(apiClz)
        .persistentWorker(persistentWorker)
        .id(id)
        .signOnMessageSupplier(SignOnAndOffMessage::signOnMessage)
        .signOffMessageSupplier(SignOnAndOffMessage::signOffMessage)
        .build();

    return switchServer;
  }

  public ServerMonitor getNewServerMonitor(String desc) {
    ServerMonitor serverMonitor = new ServerMonitor();
    serverMonitor.setDesc(desc);
    return serverMonitor;
  }

  public VisaLogPo queryRawMessage(String seqNo, String direction) {
    return visaLogService.query(seqNo, direction);
  }

  public VisaApi queryApi(String seqNo, String direction) {
    VisaLogPo visaLogPo = queryRawMessage(seqNo, direction);
    VisaMessageByJpos visaMessageByJpos = (VisaMessageByJpos) messageCoder.extract(
            Unpooled.wrappedBuffer(ByteBufUtil.decodeHexDump(visaLogPo.getHexMessage())));

    return (VisaApi) apiCoder.messageToApi(visaMessageByJpos);

  }


  @PostConstruct
  public void init() {
    messageCoder = new DefaultMessageCoder(VisaMessageByJpos::new, id);
  }
}
