package me.card.switchv1.core.component;

import io.netty.buffer.ByteBuf;

public interface MessageCoder {

  Message extract(ByteBuf byteBuf);

  ByteBuf compress(Message message);
}
