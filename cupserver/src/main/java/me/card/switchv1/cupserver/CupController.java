package me.card.switchv1.cupserver;

import javax.annotation.Resource;
import me.card.switchv1.core.server.ServerMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping(value = "/cup", method = RequestMethod.GET)
public class CupController {
  private static final Logger logger = LoggerFactory.getLogger(CupController.class);

  @Resource
  private CupStarter cupStarter;

  @RequestMapping("/start")
  public ServerMonitor start() {
    logger.debug("cup start request");

    //todo - prevent from repeat start
    cupStarter.start();

    return getNewServerMonitor("cup server is starting");
  }


  @RequestMapping("/stop")
  public ServerMonitor stop() {
    logger.debug("cup stop request");

    if (cupStarter.isServerNull()) {
      return getNewServerMonitor("cup server not start");
    }

    cupStarter.stop();

    return getNewServerMonitor("cup server is stopping");

  }

  @RequestMapping("/status")
  public ServerMonitor status() {
    logger.debug("cup status request");

    if (cupStarter.isServerNull()) {
      return getNewServerMonitor("cup server not start");
    }
    return cupStarter.status();

  }

  public ServerMonitor getNewServerMonitor(String desc) {
    ServerMonitor serverMonitor = new ServerMonitor();
    serverMonitor.setDesc(desc);
    return serverMonitor;
  }
}