package me.card.switchv1.visaserver.message;

import me.card.switchv1.core.component.AbstractPrefix;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOUtil;


public class VisaPrefix extends AbstractPrefix {

  public static final int PREFIX_LENGTH = 2;

  public VisaPrefix() {
    super.prefixLength = PREFIX_LENGTH;
  }

  @Override
  public int getIntPrefix(byte[] bytePrefix) {
    return ISOUtil.byte2int(bytePrefix);
  }

  public byte[] getBytePrefix(int length) {
    byte[] byteLength = new byte[this.prefixLength];

    try {
      String padStr = ISOUtil.zeropad(Integer.toHexString(length), prefixLength * 2);
      ISOUtil.str2hex(padStr, true, byteLength, 0);
    } catch (ISOException e) {
      throw new VisaMessageException("zero pad error");
    }
    return byteLength;

  }
}
