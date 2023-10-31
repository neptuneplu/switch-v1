package me.card.switchv1.visaserver.config;

import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import javax.annotation.PostConstruct;
import me.card.switchv1.core.component.DestinationURL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@PropertySource("/application-visa.properties")
public class VisaExternalConfig {
  private static final Logger logger = LoggerFactory.getLogger(VisaExternalConfig.class);

  @Value("${visa.name}")
  private String name;

  @Value("${visa.localAddress}")
  private String localAddress;

  @Value("${visa.localPort}")
  private String localPort;

  @Value("${visa.sourceAddress}")
  private String sourceAddress;

  @Value("${visa.sourcePort}")
  private String sourcePort;

  @Value("${visa.destinationAddress}")
  private String destinationAddress;

  @Value("${visa.destinationPort}")
  private String destinationPort;

  @Value("${visa.uri}")
  private String destinationPortUri;

  @Value("${visa.readIdleTime}")
  private String readIdleTime;

  @Value("${visa.serverType}")
  private String serverType;

  @Value("${visa.sendThreads}")
  private String sendThreads;

  @Value("${visa.persistentThreads}")
  private String persistentThreads;

  public InetSocketAddress localAddress() {
    return new InetSocketAddress(localAddress, Integer.parseInt(localPort));
  }

  public InetSocketAddress sourceAddress() {
    return new InetSocketAddress(sourceAddress, Integer.parseInt(sourcePort));
  }

  public DestinationURL destinationURL() {
    try {
      return new DestinationURL(
          new InetSocketAddress(destinationAddress, Integer.parseInt(destinationPort)),
          new URI(destinationPortUri));
    } catch (URISyntaxException e) {
      throw new VisaConfigException("uri syntax error");
    }

  }

  public String name() {
    return name;
  }

  public String readIdleTime() {
    return readIdleTime;
  }

  public String serverType() {
    return serverType;
  }

  public String sendThreads() {
    return sendThreads;
  }

  public String persistentThreads() {
    return persistentThreads;
  }

  @PostConstruct
  public void print() {
    if (logger.isInfoEnabled()) {
      logger.info(String.format("visa config: %s", this));
    }
  }

  @Override
  public String toString() {
    return "VisaExternalConfig{" +
        "name='" + name + '\'' +
        ", localAddress='" + localAddress + '\'' +
        ", localPort='" + localPort + '\'' +
        ", sourceAddress='" + sourceAddress + '\'' +
        ", sourcePort='" + sourcePort + '\'' +
        ", destinationAddress='" + destinationAddress + '\'' +
        ", destinationPort='" + destinationPort + '\'' +
        ", destinationPortUri='" + destinationPortUri + '\'' +
        ", readIdleTime='" + readIdleTime + '\'' +
        ", serverType='" + serverType + '\'' +
        ", sendThreads='" + sendThreads + '\'' +
        ", persistentThreads='" + persistentThreads + '\'' +
        '}';
  }
}
