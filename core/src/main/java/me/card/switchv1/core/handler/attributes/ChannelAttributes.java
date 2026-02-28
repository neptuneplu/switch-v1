package me.card.switchv1.core.handler.attributes;

import io.netty.util.AttributeKey;
import me.card.switchv1.component.Message;

public class ChannelAttributes {
  public static final AttributeKey<Message> MESSAGE = AttributeKey.valueOf("MESSAGE");
  public static final AttributeKey<Boolean> ON_LINE_FLAG = AttributeKey.valueOf("ON_LINE");

}
