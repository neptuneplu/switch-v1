package me.card.switchv1.core.server;

import io.netty.channel.Channel;

public interface Queryable {

  void setupChannel(Channel channel);
}
