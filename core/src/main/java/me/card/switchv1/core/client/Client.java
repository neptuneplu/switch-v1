package me.card.switchv1.core.client;

import io.netty.channel.EventLoop;
import io.netty.util.concurrent.Promise;

public interface Client<T> {
  void sendAsync(Promise<T> promise, EventLoop eventLoop, T t);

  Client<T> init();
}
