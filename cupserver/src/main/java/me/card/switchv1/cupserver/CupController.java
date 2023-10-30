package me.card.switchv1.cupserver;

import javax.annotation.Resource;
import me.card.switchv1.core.server.ServerMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class CupController {
  private static final Logger logger = LoggerFactory.getLogger(CupController.class);

  @Resource
  private CupManager cupManager;

  @GetMapping("/cup/start")
  public ServerMonitor start() {
    logger.debug("cup start request");
    return cupManager.start();
  }

  @GetMapping("/cup/stop")
  public ServerMonitor stop() {
    logger.debug("cup stop request");
    return cupManager.stop();
  }

  @GetMapping("/cup/status")
  public ServerMonitor status() {
    logger.debug("cup status request");
    return cupManager.status();
  }
}