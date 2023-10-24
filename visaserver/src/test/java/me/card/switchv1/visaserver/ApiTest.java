package me.card.switchv1.visaserver;

import me.card.switchv1.visaserver.message.SignOnAndOffMessage;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ApiTest {
  private static final Logger logger = LoggerFactory.getLogger(NioClientTest.class);


  @Test
  public void test01() throws Exception {
    System.out.println(SignOnAndOffMessage.signOnMessage().getField(37));
  }
}
