package me.card.switchv1.cupserver;

import javax.annotation.Resource;
import me.card.switchv1.core.connector.ConnectorMonitor;
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
  public ConnectorMonitor start() {
    logger.debug("cup start request");
    return cupManager.start();
  }

  @GetMapping("/cup/stop")
  public ConnectorMonitor stop() {
    logger.debug("cup stop request");
    return cupManager.stop();
  }

  @GetMapping("/cup/status")
  public ConnectorMonitor status() {
    logger.debug("cup status request");
    return cupManager.status();
  }
}