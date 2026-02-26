package me.card.switchv1.core.connector;

import java.util.function.Consumer;
import me.card.switchv1.component.Message;

public interface Connector {

  void start();

  void stop();

  ConnectorMonitor status();

  void signOn();

  void signOff();

  void write(Message outgoMessage, Consumer<Message> succCallback, Consumer<Message> failCallback);

}
