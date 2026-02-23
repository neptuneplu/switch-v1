package me.card.switchv1.app.service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import me.card.switchv1.app.config.Params;
import me.card.switchv1.component.HeartBeat;
import me.card.switchv1.core.client.DefaultApiClient;
import me.card.switchv1.component.Api;
import me.card.switchv1.component.ApiCoder;
import me.card.switchv1.component.Id;
import me.card.switchv1.component.Message;
import me.card.switchv1.component.MessageCoder;
import me.card.switchv1.core.internal.MessageContext;
import me.card.switchv1.component.Prefix;
import me.card.switchv1.core.connector.Connector;
import me.card.switchv1.core.connector.ConnectorException;
import me.card.switchv1.core.connector.ConnectorMonitor;
import me.card.switchv1.core.connector.SchemeConnectorBuilder;
import me.card.switchv1.core.processor.Processor;
import me.card.switchv1.core.processor.ProcessorBuilder;
import me.card.switchv1.api.visa.VisaApi;

import me.card.switchv1.app.db.MessageLogDao;
import me.card.switchv1.message.visa.SignOnAndOffMessage;
import me.card.switchv1.message.visa.jpos.VisaMessageByJpos;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component
public class SchemeService {
  private static final Logger logger = LoggerFactory.getLogger(SchemeService.class);

  @Resource
  private Params params;

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
  private Class<Api> apiClz;

  @Resource
  private
  LogService logService;

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
      return newServerMonitor(e.getMessage());
    }

    connector().start();
    return newServerMonitor("scheme server is starting");
  }

  public ConnectorMonitor stop() {

    try {
      stopCheck();
    } catch (IllegalArgumentException e) {
      return newServerMonitor(e.getMessage());
    }

    connector.stop();
    connector = null;
    return newServerMonitor("scheme server is stopping");
  }

  public ConnectorMonitor status() {
    try {
      statusCheck();
    } catch (IllegalArgumentException e) {
      return newServerMonitor(e.getMessage());
    }
    return connector.status();
  }

  public ConnectorMonitor signOn() {
    try {
      statusCheck();
      connector.signOn();
    } catch (IllegalArgumentException | ConnectorException e) {
      return newServerMonitor(e.getMessage());
    }
    return newServerMonitor("scheme server signOn message send");
  }

  public ConnectorMonitor signOff() {
    try {
      statusCheck();
      connector.signOff();
    } catch (IllegalArgumentException | ConnectorException e) {
      return newServerMonitor(e.getMessage());
    }
    return newServerMonitor("scheme server signOff message send");
  }

  private void startCheck() {
    Assert.isNull(connector, "scheme server already exist");
    Assert.notNull(processor, "processor is null");
    Assert.notNull(schemeConnectorBuilder, "switchServerBuilder is null");
    Assert.notNull(params, "params is null");
    Assert.notNull(apiCoder, "apiCoder is null");
    Assert.notNull(heartBeat, "heartBeat is null");
    Assert.notNull(prefix, "prefix is null");

  }

  private void stopCheck() {
    Assert.notNull(connector, "scheme server not start");
  }

  private void statusCheck() {
    Assert.notNull(connector, "scheme server not start");
  }

  private Connector connector() {
    connector = schemeConnectorBuilder
        .name(params.name())
        .connectorType(params.connectorType())
        .localAddress(params.localAddress())
        .sourceAddress(params.sourceAddress())
        .destinationURL(params.destinationURL())
        .readIdleTime(params.readIdleTime())
        .destinationURL(params.destinationURL())
        .prefix(prefix)
        .heartBeat(heartBeat)
        .messageSupplier(VisaMessageByJpos::new)
        .signOnMessageSupplier(SignOnAndOffMessage::signOnMessage)
        .signOffMessageSupplier(SignOnAndOffMessage::signOffMessage)
        .processor(processor)
        .messageCoder(messageCoder)
        .build();

    return connector;
  }

  public ConnectorMonitor newServerMonitor(String desc) {
    ConnectorMonitor connectorMonitor = new ConnectorMonitor();
    connectorMonitor.setDesc(desc);
    return connectorMonitor;
  }


  public CompletableFuture<Api> sendOutgoRequestAsync(Api api) {
    MessageContext context;
    if (connector != null) {
      context = generateRequestContext();
    } else {
      logger.error("send outgo error, connector is not started");
      api.toResponse("96");
      return CompletableFuture.completedFuture(api);
    }

    context.setOutgoApi(api);

    return processor.handleOutgoRequestAsync(context)
        .orTimeout(10, TimeUnit.SECONDS)
        .exceptionally(ex -> {
              if (ex instanceof TimeoutException) {
                api.toResponse("91");
              } else {
                api.toResponse("96");
              }
              return api;
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
        .persistentWorker(logService)
        .backofficeURL(params.destinationURL())
        .build();
  }
}
