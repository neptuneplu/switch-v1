package me.card.switchv1.core.connector;

import me.card.switchv1.core.component.MessageContext;

public interface Connector {

  void start();

  void stop();

  ConnectorMonitor status();

  void signOn();

  void signOff();

  MessageContext context();

}
