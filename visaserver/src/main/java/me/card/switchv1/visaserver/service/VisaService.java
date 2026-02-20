package me.card.switchv1.visaserver.service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import me.card.switchv1.core.client.DefaultApiClient;
import me.card.switchv1.core.component.Api;
import me.card.switchv1.core.component.ApiCoder;
import me.card.switchv1.core.component.HeartBeat;
import me.card.switchv1.core.component.Id;
import me.card.switchv1.core.component.Message;
import me.card.switchv1.core.component.MessageCoder;
import me.card.switchv1.core.component.MessageContext;
import me.card.switchv1.core.component.Prefix;
import me.card.switchv1.core.connector.Connector;
import me.card.switchv1.core.connector.ConnectorException;
import me.card.switchv1.core.connector.ConnectorMonitor;
import me.card.switchv1.core.connector.SchemeConnectorBuilder;
import me.card.switchv1.core.processor.Processor;
import me.card.switchv1.core.processor.ProcessorBuilder;
import me.card.switchv1.visaapi.VisaApi;
import me.card.switchv1.visaserver.config.VisaParams;
import me.card.switchv1.visaserver.db.VisaLogDao;
import me.card.switchv1.visaserver.message.SignOnAndOffMessage;
import me.card.switchv1.visaserver.message.jpos.VisaMessageByJpos;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component
public class VisaService {
  private static final Logger logger = LoggerFactory.getLogger(VisaService.class);

  @Resource
  private VisaParams visaParams;

  @Resource
  private HeartBeat heartBeat;

  @Resource
  private Prefix prefix;

  @Resource
  private Id id;

  @Resource
  private ApiCoder<Api, Message> apiCoder;

  @Resource
  private MessageCoder messageCoder;

  @Resource
  private Class<VisaApi> apiClz;

  @Resource
  private VisaLogService visaLogService;

  @Resource
  private VisaLogDao visaLogDao;

  @Resource
  private SchemeConnectorBuilder schemeConnectorBuilder;

  @Resource
  private ProcessorBuilder processorBuilder;

  private Connector connector;

  private Processor processor;


  public ConnectorMonitor start() {

    try {
      startCheck();
    } catch (IllegalArgumentException e) {
      return getNewServerMonitor(e.getMessage());
    }

    connector().start();
    return getNewServerMonitor("visa server is starting");
  }

  public ConnectorMonitor stop() {

    try {
      stopCheck();
    } catch (IllegalArgumentException e) {
      return getNewServerMonitor(e.getMessage());
    }

    connector.stop();
    connector = null;
    return getNewServerMonitor("visa server is stopping");
  }

  public ConnectorMonitor status() {
    try {
      statusCheck();
    } catch (IllegalArgumentException e) {
      return getNewServerMonitor(e.getMessage());
    }
    return connector.status();
  }

  public ConnectorMonitor signOn() {
    try {
      statusCheck();
      connector.signOn();
    } catch (IllegalArgumentException | ConnectorException e) {
      return getNewServerMonitor(e.getMessage());
    }
    return getNewServerMonitor("visa server signOn message send");
  }

  public ConnectorMonitor signOff() {
    try {
      statusCheck();
      connector.signOff();
    } catch (IllegalArgumentException | ConnectorException e) {
      return getNewServerMonitor(e.getMessage());
    }
    return getNewServerMonitor("visa server signOff message send");
  }

  private void startCheck() {
    Assert.isNull(connector, "visa server already exist");
    Assert.notNull(schemeConnectorBuilder, "switchServerBuilder is null");
    Assert.notNull(visaParams, "visaParams is null");
    Assert.notNull(apiCoder, "apiCoder is null");
    Assert.notNull(heartBeat, "heartBeat is null");
    Assert.notNull(prefix, "prefix is null");

  }

  private void stopCheck() {
    Assert.notNull(connector, "visa server not start");
  }

  private void statusCheck() {
    Assert.notNull(connector, "visa server not start");
  }

  private Connector connector() {
    connector = schemeConnectorBuilder
        .name(visaParams.name())
        .connectorType(visaParams.connectorType())
        .localAddress(visaParams.localAddress())
        .sourceAddress(visaParams.sourceAddress())
        .destinationURL(visaParams.destinationURL())
        .readIdleTime(visaParams.readIdleTime())
        .destinationURL(visaParams.destinationURL())
        .prefix(prefix)
        .heartBeat(heartBeat)
        .messageSupplier(VisaMessageByJpos::new)
        .signOnMessageSupplier(SignOnAndOffMessage::signOnMessage)
        .signOffMessageSupplier(SignOnAndOffMessage::signOffMessage)
        .processor(processor)
        .build();

    return connector;
  }

  public ConnectorMonitor getNewServerMonitor(String desc) {
    ConnectorMonitor connectorMonitor = new ConnectorMonitor();
    connectorMonitor.setDesc(desc);
    return connectorMonitor;
  }


  public CompletableFuture<VisaApi> sendOutgoRequestAsync(VisaApi visaApi) {
    MessageContext context;
    if (connector != null) {
      context = generateRequestContext();
    } else {
      logger.error("send outgo error, connector is not started");
      visaApi.setF39("96");
      return CompletableFuture.completedFuture(visaApi);
    }

    context.setOutgoApi(visaApi);

    return processor.handleOutgoRequestAsync(context)
        .thenApply(api -> (VisaApi) api)
        .orTimeout(10, TimeUnit.SECONDS)
        .exceptionally(ex -> {
              if (ex instanceof TimeoutException) {
                visaApi.setF39("90");
              } else {
                visaApi.setF39("96");
              }
              return visaApi;
            }
        );
  }

  private MessageContext generateRequestContext() {
    return connector.context();
  }

  @PostConstruct
  public void init() {
    processor = processorBuilder
        .apiClient(new DefaultApiClient())
        .responseApiClz(apiClz)
        .apiCoder(apiCoder)
        .messageCoder(messageCoder)
        .persistentWorker(visaLogService)
        .backofficeURL(visaParams.destinationURL())
        .build();
  }
}
