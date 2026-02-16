package me.card.switchv1.core.server;

import java.net.InetSocketAddress;
import java.util.function.Supplier;
import me.card.switchv1.core.component.Api;
import me.card.switchv1.core.component.ApiCoder;
import me.card.switchv1.core.component.DestinationURL;
import me.card.switchv1.core.component.HeartBeat;
import me.card.switchv1.core.component.Id;
import me.card.switchv1.core.component.Message;
import me.card.switchv1.core.component.PersistentWorker;
import me.card.switchv1.core.component.Prefix;

public class AbstractSwitchServer {

  protected final ServerMonitor serverMonitor;
  protected String name;
  protected InetSocketAddress localAddress;
  protected InetSocketAddress sourceAddress;
  protected DestinationURL destinationURL;
  protected int readIdleTime;
  protected Prefix prefix;
  protected HeartBeat heartBeat;
  protected Supplier<Message> messageSupplier;
  protected Class<Api> responseApiClz;
  protected ApiCoder<Api, Message> apiCoder;
  protected Id id;
  protected Supplier<Message> signOnMessageSupplier;
  protected Supplier<Message> signOffMessageSupplier;

  public AbstractSwitchServer() {
    this.serverMonitor = new ServerMonitor();
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

  void setDestinationURL(DestinationURL destinationURL) {
    this.destinationURL = destinationURL;
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

  void setApiCoder(ApiCoder<Api, Message> apiCoder) {
    this.apiCoder = apiCoder;
  }


  public void setResponseApiClz(Class<Api> responseApiClz) {
    this.responseApiClz = responseApiClz;
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
