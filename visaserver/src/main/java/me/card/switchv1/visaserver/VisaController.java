package me.card.switchv1.visaserver;

import javax.annotation.Resource;
import me.card.switchv1.core.server.ServerMonitor;
import me.card.switchv1.visaapi.VisaApi;
import me.card.switchv1.visaserver.db.VisaLogPo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class VisaController {
  private static final Logger logger = LoggerFactory.getLogger(VisaController.class);

  @Resource
  private VisaManager visaManager;

  @GetMapping("/visa/start")
  public ServerMonitor start() {
    logger.debug("visa start request");
    return visaManager.start();
  }

  @GetMapping("/visa/stop")
  public ServerMonitor stop() {
    logger.debug("visa stop request");
    return visaManager.stop();
  }

  @GetMapping("/visa/status")
  public ServerMonitor status() {
    logger.debug("visa status request");
    return visaManager.status();
  }

  @GetMapping("/visa/signOn")
  public ServerMonitor signOn() {
    logger.debug("visa signOn request");
    return visaManager.signOn();
  }

  @GetMapping("/visa/signOff")
  public ServerMonitor signOff() {
    logger.debug("visa signOff request");
    return visaManager.signOff();
  }

  @PostMapping("/visa/query/raw")
  public VisaLogPo queryRaw(@RequestBody VisaQueryRequest request) {
    return visaManager.queryRawMessage(request.getSeqNo(), request.getDirection());
  }

  @PostMapping("/visa/query/api")
  public VisaApi queryApi(@RequestBody VisaQueryRequest request) {
    return visaManager.queryApi(request.getSeqNo(), request.getDirection());
  }


}