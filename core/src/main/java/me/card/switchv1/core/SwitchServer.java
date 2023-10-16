package me.card.switchv1.core;

public interface SwitchServer {
  void start();

  void stop();

  ServerMonitor status();
}
