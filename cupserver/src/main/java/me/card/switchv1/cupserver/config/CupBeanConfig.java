package me.card.switchv1.cupserver.config;

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
@PropertySource("/application-cup.properties")
public class CupBeanConfig {
  private static final Logger logger = LoggerFactory.getLogger(CupBeanConfig.class);

  @Value("${cup.name}")
  private String name;

  @Value("${cup.localAddress}")
  private String localAddress;

  @Value("${cup.localPort}")
  private String localPort;

  @Value("${cup.sourceAddress}")
  private String sourceAddress;

  @Value("${cup.sourcePort}")
  private String sourcePort;

  @Value("${cup.destinationAddress}")
  private String destinationAddress;

  @Value("${cup.destinationPort}")
  private String destinationPort;

  @Value("${cup.uri}")
  private String destinationPortUri;

  @Value("${cup.readIdleTime}")
  private String readIdleTime;

  @Value("${cup.serverType}")
  private String serverType;

  @Value("${cup.sendThreads}")
  private String processorThreads;

  @Value("${cup.persistentThreads}")
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
      throw new CupConfigException("uri syntax error");
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

  public String processorThreads() {
    return processorThreads;
  }

  public String persistentThreads() {
    return persistentThreads;
  }

  @PostConstruct
  public void print() {
    if (logger.isInfoEnabled()) {
      logger.info(String.format("cup config: %s", this));
    }
  }

  @Override
  public String toString() {
    return "CupExternalConfig{" +
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
        ", processorThreads='" + processorThreads + '\'' +
        ", persistentThreads='" + persistentThreads + '\'' +
        '}';
  }
}
