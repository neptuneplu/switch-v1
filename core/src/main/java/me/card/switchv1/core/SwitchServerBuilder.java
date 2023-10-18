package me.card.switchv1.core;

import java.net.InetSocketAddress;
import java.util.function.Supplier;
import me.card.switchv1.core.component.ApiCoder;
import me.card.switchv1.core.component.DestinationURL;
import me.card.switchv1.core.component.HeartBeat;
import me.card.switchv1.core.component.Message;
import me.card.switchv1.core.component.PersistentWorker;
import me.card.switchv1.core.component.Prefix;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SwitchServerBuilder {
  private static final Logger logger = LoggerFactory.getLogger(SwitchServerBuilder.class);

  private String name;
  private String serverType;
  private InetSocketAddress localAddress;
  private InetSocketAddress sourceAddress;
  private DestinationURL destinationURL;
  private HeartBeat heartBeat;
  private Prefix prefix;
  private Supplier<Message> messageSupplier;
  private ApiCoder apiCoder;
  private Class responseApiClz;
  private String readIdleTime;
  private PersistentWorker persistentWorker;


  public SwitchServerBuilder name(String name) {
    this.name = name;
    return this;
  }

  public SwitchServerBuilder serverType(String serverType) {
    this.serverType = serverType;
    return this;
  }

  public SwitchServerBuilder localAddress(InetSocketAddress localAddress) {
    this.localAddress = localAddress;
    return this;
  }

  public SwitchServerBuilder sourceAddress(InetSocketAddress sourceAddress) {
    this.sourceAddress = sourceAddress;
    return this;
  }

  public SwitchServerBuilder destinationURL(DestinationURL destinationURL) {
    this.destinationURL = destinationURL;
    return this;
  }

  public SwitchServerBuilder heartBeat(HeartBeat heartBeat) {
    this.heartBeat = heartBeat;
    return this;
  }

  public SwitchServerBuilder prefix(Prefix prefix) {
    this.prefix = prefix;
    return this;
  }

  public SwitchServerBuilder messageSupplier(Supplier<Message> messageSupplier) {
    this.messageSupplier = messageSupplier;
    return this;
  }

  public SwitchServerBuilder apiCoder(ApiCoder apiCoder) {
    this.apiCoder = apiCoder;
    return this;
  }

  public SwitchServerBuilder responseApiClz(Class responseApiClz) {
    this.responseApiClz = responseApiClz;
    return this;
  }

  public SwitchServerBuilder readIdleTime(String readIdleTime) {
    this.readIdleTime = readIdleTime;
    return this;
  }

  public SwitchServerBuilder persistentWorker(
      PersistentWorker persistentWorker) {
    this.persistentWorker = persistentWorker;
    return this;
  }

  public SwitchServer build() {
    logger.debug("server build start");
    return getServerByType(serverType);
  }

  private SwitchServer getServerByType(String serverType) {
    switch (serverType) {
      case "activeBio":
        ActiveSwitchServerBio activeSwitchServerBio = new ActiveSwitchServerBio();
        activeServerBioSetup(activeSwitchServerBio);
        return activeSwitchServerBio;
      case "activeBioPlus":
        ActiveSwitchServerBioPlus activeSwitchServerBioPlus = new ActiveSwitchServerBioPlus();
        activeSwitchServerBioPlusSetup(activeSwitchServerBioPlus);
        return activeSwitchServerBioPlus;
      case "activeNio":
        ActiveSwitchServerNio activeSwitchServerNio = new ActiveSwitchServerNio();
        activeSwitchServerNioSetup(activeSwitchServerNio);
        return activeSwitchServerNio;
      case "passiveBio":
        PassiveSwitchServerBio passiveSwitchServerBio = new PassiveSwitchServerBio();
        passiveServerBioSetup(passiveSwitchServerBio);
        return passiveSwitchServerBio;
      default:
        throw new RuntimeException("server type error");

    }
  }

  private void activeServerBioSetup(ActiveSwitchServerBio server) {
    server.setName(this.name);
    server.setLocalAddress(this.localAddress);
    server.setSourceAddress(this.sourceAddress);
    server.setDestinationURL(this.destinationURL);
    server.setHeartBeat(this.heartBeat);
    server.setPrefix(this.prefix);
    server.setMessageSupplier(this.messageSupplier);
    server.setApiCoder(this.apiCoder);
    server.setResponseApiClz(this.responseApiClz);
    server.setReadIdleTime(this.readIdleTime);
    server.setPersistentWorker(this.persistentWorker);
  }

  private void activeSwitchServerBioPlusSetup(ActiveSwitchServerBioPlus server) {
    server.setName(this.name);
    server.setLocalAddress(this.localAddress);
    server.setSourceAddress(this.sourceAddress);
    server.setDestinationURL(this.destinationURL);
    server.setHeartBeat(this.heartBeat);
    server.setPrefix(this.prefix);
    server.setMessageSupplier(this.messageSupplier);
    server.setApiCoder(this.apiCoder);
    server.setResponseApiClz(this.responseApiClz);
    server.setReadIdleTime(this.readIdleTime);
    server.setPersistentWorker(this.persistentWorker);
  }


  private void activeSwitchServerNioSetup(ActiveSwitchServerNio server) {
    server.setName(this.name);
    server.setLocalAddress(this.localAddress);
    server.setSourceAddress(this.sourceAddress);
    server.setDestinationURL(this.destinationURL);
    server.setHeartBeat(this.heartBeat);
    server.setPrefix(this.prefix);
    server.setMessageSupplier(this.messageSupplier);
    server.setApiCoder(this.apiCoder);
    server.setResponseApiClz(this.responseApiClz);
    server.setReadIdleTime(this.readIdleTime);
    server.setPersistentWorker(this.persistentWorker);
  }

  private void passiveServerBioSetup(PassiveSwitchServerBio server) {
    server.setName(this.name);
    server.setLocalAddress(this.localAddress);
    server.setSourceAddress(this.sourceAddress);
    server.setDestinationURL(this.destinationURL);
    server.setHeartBeat(this.heartBeat);
    server.setPrefix(this.prefix);
    server.setMessageSupplier(this.messageSupplier);
    server.setApiCoder(this.apiCoder);
    server.setResponseApiClz(this.responseApiClz);
    server.setReadIdleTime(this.readIdleTime);
    server.setPersistentWorker(this.persistentWorker);

  }
}
