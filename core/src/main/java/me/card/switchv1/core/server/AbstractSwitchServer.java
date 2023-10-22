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

  final ServerMonitor serverMonitor;
  String name;
  InetSocketAddress localAddress;
  InetSocketAddress sourceAddress;
  DestinationURL destinationURL;
  String readIdleTime;
  Prefix prefix;
  HeartBeat heartBeat;
  Supplier<Message> messageSupplier;
  Class responseApiClz;
  ApiCoder apiCoder;
  PersistentWorker persistentWorker;
  Id id;

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

  void setApiCoder(ApiCoder<? extends Api, ? extends Message> apiCoder) {
    this.apiCoder = apiCoder;
  }


  public void setResponseApiClz(Class<? extends Api> responseApiClz) {
    this.responseApiClz = responseApiClz;
  }

  public void setReadIdleTime(String readIdleTime) {
    this.readIdleTime = readIdleTime;
  }

  public void setPersistentWorker(PersistentWorker persistentWorker) {
    this.persistentWorker = persistentWorker;
  }

  public void setId(Id id) {
    this.id = id;
  }
}
