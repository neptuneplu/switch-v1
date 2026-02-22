package me.card.switchv1.api.visa;

public class MTI {
  public static final String REQUEST_0100 = "0100";
  public static final String RESPONSE_0110 = "0110";
  public static final String REQUEST_0200 = "0200";
  public static final String RESPONSE_0210 = "0210";
  public static final String REQUEST_0800 = "0800";
  public static final String RESPONSE_0810 = "0810";


  public static String mapResponseMTI(String mti) {
    switch (mti) {
      case REQUEST_0100:
        return RESPONSE_0110;
      case REQUEST_0200:
        return RESPONSE_0210;
      case REQUEST_0800:
        return RESPONSE_0810;
      default:
        throw new IllegalArgumentException("MTI error: " + mti);
    }

  }

}
