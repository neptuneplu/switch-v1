package me.card.switchv1.component;

import io.netty.buffer.ByteBuf;

public interface MessageCoder {

  Message extract(ByteBuf byteBuf);

  Message postExtractPcs(Message message);

  ByteBuf compress(Message message);

  Message preCompressPcs(Message message);
}
