package me.card.switchv1.core.server;

import io.netty.buffer.ByteBuf;
import me.card.switchv1.core.component.RequestContext;

public interface SwitchServer {

  void start();

  void stop();

  ServerMonitor status();

  void signOn();

  void signOff();

  RequestContext context();

}
