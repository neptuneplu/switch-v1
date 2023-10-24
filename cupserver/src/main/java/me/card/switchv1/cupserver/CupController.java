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
  private CupManager cupManager;

  @RequestMapping("/start")
  public ServerMonitor start() {
    logger.debug("cup start request");
    return cupManager.start();
  }

  @RequestMapping("/stop")
  public ServerMonitor stop() {
    logger.debug("cup stop request");
    return cupManager.stop();
  }

  @RequestMapping("/status")
  public ServerMonitor status() {
    logger.debug("cup status request");
    return cupManager.status();
  }
}