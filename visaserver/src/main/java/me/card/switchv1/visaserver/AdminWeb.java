package me.card.switchv1.visaserver;

import javax.annotation.Resource;
import me.card.switchv1.core.server.ConnectorMonitor;
import me.card.switchv1.visaapi.VisaApi;
import me.card.switchv1.visaserver.db.VisaLogPo;
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
  private VisaServer visaServer;

  @GetMapping("/visa/start")
  public ConnectorMonitor start() {
    logger.debug("visa start request");
    return visaServer.start();
  }

  @GetMapping("/visa/stop")
  public ConnectorMonitor stop() {
    logger.debug("visa stop request");
    return visaServer.stop();
  }

  @GetMapping("/visa/status")
  public ConnectorMonitor status() {
    logger.debug("visa status request");
    return visaServer.status();
  }

  @GetMapping("/visa/signOn")
  public ConnectorMonitor signOn() {
    logger.debug("visa signOn request");
    return visaServer.signOn();
  }

  @GetMapping("/visa/signOff")
  public ConnectorMonitor signOff() {
    logger.debug("visa signOff request");
    return visaServer.signOff();
  }

  @PostMapping("/visa/query/raw")
  public VisaLogPo queryRaw(@RequestBody VisaQueryRequest request) {
    return visaServer.queryRawMessage(request.getSeqNo(), request.getDirection());
  }

  @PostMapping("/visa/query/api")
  public VisaApi queryApi(@RequestBody VisaQueryRequest request) {
    return visaServer.queryApi(request.getSeqNo(), request.getDirection());
  }


}