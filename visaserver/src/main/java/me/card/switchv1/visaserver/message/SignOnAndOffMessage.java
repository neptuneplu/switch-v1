package me.card.switchv1.visaserver.message;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.concurrent.atomic.AtomicInteger;
import me.card.switchv1.visaserver.message.jpos.VisaMessageByJpos;
import org.jpos.iso.ISOUtil;

public class SignOnAndOffMessage {
  private static final AtomicInteger f11Value = new AtomicInteger(0);

  private SignOnAndOffMessage() {}

  public static VisaMessageByJpos signOnMessage() {
    VisaMessageByJpos visaMessage = getMessage();
    visaMessage.setField(70, "071");
    return visaMessage;
  }

  public static VisaMessageByJpos signOffMessage() {
    VisaMessageByJpos visaMessage = getMessage();
    visaMessage.setField(70, "072");
    return visaMessage;
  }

  private static VisaMessageByJpos getMessage() {
    VisaMessageByJpos visaMessage = new VisaMessageByJpos();
    LocalDate currentDate = LocalDate.now();
    LocalTime currentTime = LocalTime.now();

    visaMessage.setSourceId("00000000000");
    visaMessage.setDestinationId("00000000000");
    visaMessage.setMti("0800");
    visaMessage.setField(7, strF7(currentDate, currentTime));
    visaMessage.setField(11, strF11());
    visaMessage.setField(37, strF37(currentDate, currentTime, visaMessage.getField(11)));
    return visaMessage;
  }

  public static String strF7(LocalDate currentDate, LocalTime currentTime) {
    String mm = ISOUtil.zeropad(currentDate.getMonthValue(), 2);
    String dd = ISOUtil.zeropad(currentDate.getDayOfMonth(), 2);
    String hh = ISOUtil.zeropad(currentTime.getHour(), 2);
    String minute = ISOUtil.zeropad(currentTime.getMinute(), 2);
    String ss = ISOUtil.zeropad(currentTime.getSecond(), 2);

    return mm + dd + hh + minute + ss;
  }

  public static String strF11() {
    return ISOUtil.zeropad(f11Value.incrementAndGet(), 6);
  }

  public static String strF37(LocalDate currentDate, LocalTime currentTime, String f11) {
    String yddd = String.valueOf(currentDate.getYear()).substring(3) +
        ISOUtil.zeropad(currentDate.getDayOfYear(), 3);
    String ss = ISOUtil.zeropad(currentTime.getSecond(), 2);

    return yddd + ss + f11;
  }
}
