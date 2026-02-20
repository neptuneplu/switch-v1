package me.card.switchv1.visaserver;

import javax.annotation.Resource;
import me.card.switchv1.core.connector.ConnectorMonitor;
import me.card.switchv1.visaapi.VisaApi;
import me.card.switchv1.visaserver.db.VisaLogPo;
import me.card.switchv1.visaserver.service.VisaLogService;
import me.card.switchv1.visaserver.service.VisaService;
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
  private VisaService visaService;

  @Resource
  private VisaLogService visaLogService;

  @GetMapping("/visa/start")
  public ConnectorMonitor start() {
    logger.debug("visa start request");
    return visaService.start();
  }

  @GetMapping("/visa/stop")
  public ConnectorMonitor stop() {
    logger.debug("visa stop request");
    return visaService.stop();
  }

  @GetMapping("/visa/status")
  public ConnectorMonitor status() {
    logger.debug("visa status request");
    return visaService.status();
  }

  @GetMapping("/visa/signOn")
  public ConnectorMonitor signOn() {
    logger.debug("visa signOn request");
    return visaService.signOn();
  }

  @GetMapping("/visa/signOff")
  public ConnectorMonitor signOff() {
    logger.debug("visa signOff request");
    return visaService.signOff();
  }

  @PostMapping("/visa/query/raw")
  public VisaLogPo queryRaw(@RequestBody VisaQueryRequest request) {
    return visaLogService.queryRawMessage(request.getSeqNo(), request.getDirection());
  }

  @PostMapping("/visa/query/api")
  public VisaApi queryApi(@RequestBody VisaQueryRequest request) {
    return visaLogService.queryApi(request.getSeqNo(), request.getDirection());
  }


}