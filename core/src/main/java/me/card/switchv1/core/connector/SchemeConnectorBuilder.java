package me.card.switchv1.core.connector;

import java.net.InetSocketAddress;
import java.util.function.Supplier;

import me.card.switchv1.component.BackofficeURL;
import me.card.switchv1.component.HeartBeat;
import me.card.switchv1.component.Id;
import me.card.switchv1.component.Message;
import me.card.switchv1.component.MessageCoder;
import me.card.switchv1.component.Prefix;
import me.card.switchv1.core.processor.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SchemeConnectorBuilder {
  private static final Logger logger = LoggerFactory.getLogger(SchemeConnectorBuilder.class);

  private String name;
  private String connectorType;
  private InetSocketAddress localAddress;
  private InetSocketAddress sourceAddress;
  private BackofficeURL backofficeURL;
  private int readIdleTime;
  private HeartBeat heartBeat;
  private Prefix prefix;
  private Id id;
  private Supplier<Message> messageSupplier;
  private Supplier<Message> signOnMessageSupplier;
  private Supplier<Message> signOffMessageSupplier;
  private Processor processor;
  private MessageCoder messageCoder;

  public SchemeConnectorBuilder name(String name) {
    this.name = name;
    return this;

  }

  public SchemeConnectorBuilder connectorType(String connectorType) {
    this.connectorType = connectorType;
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

  public SchemeConnectorBuilder processor(Processor processor) {
    this.processor = processor;
    return this;
  }

  public SchemeConnectorBuilder messageCoder(
      MessageCoder messageCoder) {
    this.messageCoder = messageCoder;
    return this;
  }

  public Connector build() {
    logger.debug("connector build start");
    check();
    return buildConnectorByType(connectorType);
  }

  private void check() {

  }

  private Connector buildConnectorByType(String connectorType) {
    Connector connector;

    switch (connectorType) {
      case "active":
        connector = new ActiveSchemeConnector();
        break;
      default:
        throw new ConnectorException("connector type error");
    }

    connectorSetup((AbstractSchemeConnector) connector);

    return connector;
  }

  private void connectorSetup(AbstractSchemeConnector connector) {
    connector.setName(this.name);
    connector.setLocalAddress(this.localAddress);
    connector.setSourceAddress(this.sourceAddress);
    connector.setDestinationURL(this.backofficeURL);
    connector.setHeartBeat(this.heartBeat);
    connector.setPrefix(this.prefix);
    connector.setId(this.id);
    connector.setReadIdleTime(this.readIdleTime);
    connector.setMessageSupplier(this.messageSupplier);
    connector.setSignOnMessageSupplier(signOnMessageSupplier);
    connector.setSignOffMessageSupplier(signOffMessageSupplier);
    connector.setProcessor(processor);
    connector.setMessageCoder(messageCoder);
  }

}
