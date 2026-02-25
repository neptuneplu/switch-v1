package me.card.switchv1.app;

import jakarta.annotation.Resource;
import me.card.switchv1.app.db.MessageLogPo;
import me.card.switchv1.app.service.LogService;
import me.card.switchv1.app.service.SchemeService;
import me.card.switchv1.component.Api;
import me.card.switchv1.core.connector.ConnectorMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class AdminWeb {
  private static final Logger logger = LoggerFactory.getLogger(AdminWeb.class);

  @Resource
  private SchemeService schemeService;

  @Resource
  private LogService logService;

  @GetMapping("/scheme/start")
  public ConnectorMonitor start() {
    logger.debug("scheme start request");
    return schemeService.start();
  }

  @GetMapping("/scheme/stop")
  public ConnectorMonitor stop() {
    logger.debug("scheme stop request");
    return schemeService.stop();
  }

  @GetMapping("/scheme/status")
  public ConnectorMonitor status() {
    logger.debug("scheme status request");
    return schemeService.status();
  }

  @GetMapping("/scheme/pendings")
  public ConnectorMonitor pendings() {
    logger.debug("scheme pendings request");
    return schemeService.pendingOutgos();
  }

  @GetMapping("/scheme/signOn")
  public ConnectorMonitor signOn() {
    logger.debug("scheme signOn request");
    return schemeService.signOn();
  }

  @GetMapping("/scheme/signOff")
  public ConnectorMonitor signOff() {
    logger.debug("scheme signOff request");
    return schemeService.signOff();
  }

  @PostMapping("/scheme/query/raw")
  public MessageLogPo queryRaw(@RequestBody QueryRequest request) {
    return logService.queryRawMessage(request.getSeqNo(), request.getDirection());
  }

  @PostMapping("/scheme/query/api")
  public Api queryApi(@RequestBody QueryRequest request) {
    return logService.queryApi(request.getSeqNo(), request.getDirection());
  }


}