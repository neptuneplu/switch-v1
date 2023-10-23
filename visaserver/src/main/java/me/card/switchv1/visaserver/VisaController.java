package me.card.switchv1.visaserver;

import javax.annotation.Resource;
import me.card.switchv1.core.server.ServerMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping(value = "/visa", method = RequestMethod.GET)
public class VisaController {
  private static final Logger logger = LoggerFactory.getLogger(VisaController.class);

  @Resource
  private VisaManager visaStarter;

  @RequestMapping("/start")
  public ServerMonitor start() {
    logger.debug("visa start request");
    return visaStarter.start();
  }

  @RequestMapping("/stop")
  public ServerMonitor stop() {
    logger.debug("visa stop request");
    return visaStarter.stop();
  }

  @RequestMapping("/status")
  public ServerMonitor status() {
    logger.debug("visa status request");
    return visaStarter.status();
  }

}