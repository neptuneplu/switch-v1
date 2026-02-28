package me.card.switchv1.app.config;

import jakarta.annotation.PostConstruct;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import me.card.switchv1.component.BackofficeURL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@PropertySource("/visa/application.properties")
public class Params {
  private static final Logger logger = LoggerFactory.getLogger(Params.class);

  @Value("${name}")
  private String name;

  @Value("${localAddress}")
  private String localAddress;

  @Value("${localPort}")
  private String localPort;

  @Value("${sourceAddress}")
  private String sourceAddress;

  @Value("${sourcePort}")
  private String sourcePort;

  @Value("${destinationAddress}")
  private String destinationAddress;

  @Value("${destinationPort}")
  private String destinationPort;

  @Value("${uri}")
  private String destinationPortUri;

  @Value("${readIdleTime}")
  private String readIdleTime;

  @Value("${serverType}")
  private String connectorType;

  @Value("${processor.Threads}")
  private int processorThreads;

  @Value("${processor.acq.timeout}")
  private int acqTimeoutSeconds;

  @Value("${processor.iss.timeout}")
  private int issTimeoutSeconds;

  @Value("${apiClient.connectTimeoutSeconds}")
  private int apiClientConnectTimeoutSeconds;

  @Value("${apiClient.requestTimeoutSeconds}")
  private int apiClientRequestTimeoutSeconds;


  public InetSocketAddress localAddress() {
    return new InetSocketAddress(localAddress, Integer.parseInt(localPort));
  }

  public InetSocketAddress sourceAddress() {
    return new InetSocketAddress(sourceAddress, Integer.parseInt(sourcePort));
  }

  public BackofficeURL destinationURL() {
    try {
      return new BackofficeURL(
          new InetSocketAddress(destinationAddress, Integer.parseInt(destinationPort)),
          new URI(destinationPortUri));
    } catch (URISyntaxException e) {
      throw new ParamsException("uri syntax error");
    }

  }

  public String name() {
    return name;
  }

  public String readIdleTime() {
    return readIdleTime;
  }

  public String connectorType() {
    return connectorType;
  }

  public int processorThreads() {
    return processorThreads;
  }

  @Override
  public String toString() {
    return "Params{" +
        "name='" + name + '\'' +
        ", localAddress='" + localAddress + '\'' +
        ", localPort='" + localPort + '\'' +
        ", sourceAddress='" + sourceAddress + '\'' +
        ", sourcePort='" + sourcePort + '\'' +
        ", destinationAddress='" + destinationAddress + '\'' +
        ", destinationPort='" + destinationPort + '\'' +
        ", destinationPortUri='" + destinationPortUri + '\'' +
        ", readIdleTime='" + readIdleTime + '\'' +
        ", connectorType='" + connectorType + '\'' +
        ", processorThreads=" + processorThreads +
        ", acqTimeoutSeconds=" + acqTimeoutSeconds +
        ", issTimeoutSeconds=" + issTimeoutSeconds +
        ", apiClientConnectTimeoutSeconds=" + apiClientConnectTimeoutSeconds +
        ", apiClientRequestTimeoutSeconds=" + apiClientRequestTimeoutSeconds +
        '}';
  }

  public int acqTimeoutSeconds() {
    return acqTimeoutSeconds;
  }

  public int issTimeoutSeconds() {
    return issTimeoutSeconds;
  }

  public int getApiClientConnectTimeoutSeconds() {
    return apiClientConnectTimeoutSeconds;
  }

  public int getApiClientRequestTimeoutSeconds() {
    return apiClientRequestTimeoutSeconds;
  }

  @PostConstruct
  public void print() {
    if (logger.isInfoEnabled()) {
      logger.info(String.format("visa config: %s", this));
    }
  }

}
