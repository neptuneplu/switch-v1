package me.card.switchv1.core.server;

import java.net.InetSocketAddress;
import java.util.function.Supplier;
import me.card.switchv1.core.component.Api;
import me.card.switchv1.core.component.BackofficeURL;
import me.card.switchv1.core.component.HeartBeat;
import me.card.switchv1.core.component.Id;
import me.card.switchv1.core.component.Message;
import me.card.switchv1.core.component.Prefix;
import me.card.switchv1.core.processor.Processor;

public class AbstractSchemeConnector {

  protected final ConnectorMonitor connectorMonitor;
  protected String name;
  protected InetSocketAddress localAddress;
  protected InetSocketAddress sourceAddress;
  protected BackofficeURL backofficeURL;
  protected int readIdleTime;
  protected Prefix prefix;
  protected HeartBeat heartBeat;
  protected Id id;
  protected Supplier<Message> messageSupplier;
  protected Supplier<Message> signOnMessageSupplier;
  protected Supplier<Message> signOffMessageSupplier;
  protected Processor processor;

  public AbstractSchemeConnector() {
    this.connectorMonitor = new ConnectorMonitor();
  }

  void setProcessor(Processor processor) {
    this.processor = processor;
  }

  void setName(String name) {
    this.name = name;
  }

  void setLocalAddress(InetSocketAddress localAddress) {
    this.localAddress = localAddress;
  }

  void setSourceAddress(InetSocketAddress sourceAddress) {
    this.sourceAddress = sourceAddress;
  }

  void setDestinationURL(BackofficeURL backofficeURL) {
    this.backofficeURL = backofficeURL;
  }

  void setPrefix(Prefix prefix) {
    this.prefix = prefix;
  }

  void setHeartBeat(HeartBeat heartBeat) {
    this.heartBeat = heartBeat;
  }

  public void setMessageSupplier(Supplier<Message> messageSupplier) {
    this.messageSupplier = messageSupplier;
  }

  public void setReadIdleTime(int readIdleTime) {
    this.readIdleTime = readIdleTime;
  }

  public void setId(Id id) {
    this.id = id;
  }

  public void setSignOnMessageSupplier(
      Supplier<Message> signOnMessageSupplier) {
    this.signOnMessageSupplier = signOnMessageSupplier;
  }

  public void setSignOffMessageSupplier(
      Supplier<Message> signOffMessageSupplier) {
    this.signOffMessageSupplier = signOffMessageSupplier;
  }

}
