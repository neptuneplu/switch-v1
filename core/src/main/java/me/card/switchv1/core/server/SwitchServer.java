package me.card.switchv1.core.server;

public interface SwitchServer {

  void start();

  void stop();

  ServerMonitor status();

  void signOn();

  void signOff();

}
