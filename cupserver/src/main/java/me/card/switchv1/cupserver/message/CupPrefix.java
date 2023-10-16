package me.card.switchv1.cupserver.message;

import java.nio.charset.StandardCharsets;
import me.card.switchv1.core.component.AbstractPrefix;
import org.apache.commons.lang3.StringUtils;

public class CupPrefix extends AbstractPrefix {
  public static final int PREFIX_LENGTH = 4;

  public CupPrefix() {
    super.prefixLength = PREFIX_LENGTH;
  }

  @Override
  public int getIntPrefix(byte[] bytePrefix) {
    return Integer.parseInt(new String(bytePrefix, StandardCharsets.US_ASCII));
  }

  @Override
  public byte[] getBytePrefix(int length) {
    return StringUtils.leftPad(String.valueOf(length), prefixLength, '0').getBytes();
  }


}
