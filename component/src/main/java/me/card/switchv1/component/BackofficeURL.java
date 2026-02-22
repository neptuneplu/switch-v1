package me.card.switchv1.component;

import java.net.InetSocketAddress;
import java.net.URI;


public class BackofficeURL {
  private final InetSocketAddress backofficeAddress;
  private final URI uri;

  public BackofficeURL(InetSocketAddress backofficeAddress, URI uri) {
    this.backofficeAddress = backofficeAddress;
    this.uri = uri;
  }

  public InetSocketAddress getBackofficeAddress() {
    return backofficeAddress;
  }

  //todo - substring(1) to omit the '/', the '/'' is in the netty transfer result but I don't have the code changed
  // just modify the jackson dependency, netty's version is the same
  public String getHostStr() {
    return backofficeAddress.toString().substring(1);
  }

  public URI getUri() {
    return uri;
  }

  public String getUrlString() {
    return "http:/" + backofficeAddress.getAddress() + ":" + backofficeAddress.getPort() +
        uri.toString();
  }
}
