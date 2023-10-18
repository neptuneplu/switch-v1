package me.card.switchv1.visaserver;

import javax.annotation.Resource;
import me.card.switchv1.core.ServerMonitor;
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
  private VisaStarter visaStarter;

  @RequestMapping("/start")
  public ServerMonitor start() {
    logger.debug("visa start request");

    //todo - prevent from repeat start
    visaStarter.start();

    return getNewServerMonitor("visa server is starting");
  }


  @RequestMapping("/stop")
  public ServerMonitor stop() {
    logger.debug("visa stop request");

    if (visaStarter.isServerNull()) {
      return getNewServerMonitor("visa server not start");
    }

    visaStarter.stop();

    return getNewServerMonitor("visa server is stopping");

  }

  @RequestMapping("/status")
  public ServerMonitor status() {
    logger.debug("visa status request");

    if (visaStarter.isServerNull()) {
      return getNewServerMonitor("visa server not start");
    }
    return visaStarter.status();

  }

  public ServerMonitor getNewServerMonitor(String desc) {
    ServerMonitor serverMonitor = new ServerMonitor();
    serverMonitor.setDesc(desc);
    return serverMonitor;
  }
}