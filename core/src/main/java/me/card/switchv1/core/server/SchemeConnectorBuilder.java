package me.card.switchv1.core.server;

import java.net.InetSocketAddress;
import java.util.function.Supplier;
import me.card.switchv1.core.component.BackofficeURL;
import me.card.switchv1.core.component.HeartBeat;
import me.card.switchv1.core.component.Id;
import me.card.switchv1.core.component.Message;
import me.card.switchv1.core.component.Prefix;
import me.card.switchv1.core.processor.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SchemeConnectorBuilder {
  private static final Logger logger = LoggerFactory.getLogger(SchemeConnectorBuilder.class);

  private String name;
  private String serverType;
  private InetSocketAddress localAddress;
  private InetSocketAddress sourceAddress;
  private BackofficeURL backofficeURL;
  private int readIdleTime;
  private HeartBeat heartBeat;
  private Prefix prefix;
  private Id id;
  private Supplier<Message> messageSupplier;
//  private Class<Api> responseApiClz;
//  private ApiCoder<Api, Message> apiCoder;
  private Supplier<Message> signOnMessageSupplier;
  private Supplier<Message> signOffMessageSupplier;
//  private MessageCoder messageCoder;
  private int processorThreads;
  private Processor processor;
//  private PersistentWorker persistentWorker;

  public SchemeConnectorBuilder name(String name) {
    this.name = name;
    return this;

  }

  public SchemeConnectorBuilder serverType(String serverType) {
    this.serverType = serverType;
    return this;
  }

  public SchemeConnectorBuilder localAddress(InetSocketAddress localAddress) {
    this.localAddress = localAddress;
    return this;
  }

  public SchemeConnectorBuilder sourceAddress(InetSocketAddress sourceAddress) {
    this.sourceAddress = sourceAddress;
    return this;
  }

  public SchemeConnectorBuilder destinationURL(BackofficeURL backofficeURL) {
    this.backofficeURL = backofficeURL;
    return this;
  }

  public SchemeConnectorBuilder setId(Id id) {
    this.id = id;
    return this;
  }

  public SchemeConnectorBuilder heartBeat(HeartBeat heartBeat) {
    this.heartBeat = heartBeat;
    return this;
  }

  public SchemeConnectorBuilder prefix(Prefix prefix) {
    this.prefix = prefix;
    return this;
  }

  public SchemeConnectorBuilder messageSupplier(Supplier<Message> messageSupplier) {
    this.messageSupplier = messageSupplier;
    return this;
  }

//  public SwitchServerBuilder apiCoder(ApiCoder<Api, Message> apiCoder) {
//    this.apiCoder = apiCoder;
//    return this;
//  }

//  public SwitchServerBuilder responseApiClz(Class<Api> responseApiClz) {
//    this.responseApiClz = responseApiClz;
//    return this;
//  }

  public SchemeConnectorBuilder readIdleTime(String readIdleTime) {
    this.readIdleTime = Integer.parseInt(readIdleTime);
    return this;
  }


  public SchemeConnectorBuilder signOnMessageSupplier(
      Supplier<Message> signOnMessageSupplier) {
    this.signOnMessageSupplier = signOnMessageSupplier;
    return this;
  }

  public SchemeConnectorBuilder signOffMessageSupplier(
      Supplier<Message> signOffMessageSupplier) {
    this.signOffMessageSupplier = signOffMessageSupplier;
    return this;
  }

//  public SwitchServerBuilder messageCoder(MessageCoder messageCoder) {
//    this.messageCoder = messageCoder;
//    return this;
//  }

  public SchemeConnectorBuilder processorThreads(String processorThreads) {
    this.processorThreads = Integer.parseInt(processorThreads);
    return this;
  }

  public SchemeConnectorBuilder processor(Processor processor) {
    this.processor = processor;
    return this;
  }

//  public SwitchServerBuilder persistentWorker(PersistentWorker persistentWorker) {
//    this.persistentWorker = persistentWorker;
//    return this;
//  }

  public Connector build() {
    logger.debug("server build start");
    check();
    return getServerByType(serverType);
  }

  private void check() {

  }

  private Connector getServerByType(String serverType) {
    Connector server;

    switch (serverType) {
      case "active":
        server = new ActiveSchemeConnector();
        break;
      default:
        throw new ConnectorException("server type error");
    }

    serverSetup((AbstractSchemeConnector) server);

    return server;
  }

  private void serverSetup(AbstractSchemeConnector server) {
    server.setName(this.name);
    server.setLocalAddress(this.localAddress);
    server.setSourceAddress(this.sourceAddress);
    server.setDestinationURL(this.backofficeURL);
    server.setHeartBeat(this.heartBeat);
    server.setPrefix(this.prefix);
    server.setId(this.id);
    server.setReadIdleTime(this.readIdleTime);
    server.setMessageSupplier(this.messageSupplier);
//    server.setApiCoder(this.apiCoder);
//    server.setResponseApiClz(this.responseApiClz);
    server.setSignOnMessageSupplier(signOnMessageSupplier);
    server.setSignOffMessageSupplier(signOffMessageSupplier);
    server.setProcessor(processor);
  }

}
