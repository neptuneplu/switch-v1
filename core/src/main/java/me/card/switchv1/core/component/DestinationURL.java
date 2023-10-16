package me.card.switchv1.core.component;

import java.net.InetSocketAddress;
import java.net.URI;


public class DestinationURL {
  private final InetSocketAddress destinationAddress;
  private final URI uri;

  public DestinationURL(InetSocketAddress destinationAddress, URI uri) {
    this.destinationAddress = destinationAddress;
    this.uri = uri;
  }

  public InetSocketAddress getDestinationAddress() {
    return destinationAddress;
  }

  public URI getUri() {
    return uri;
  }

  public String getUrlString() {
    return "http:/" + destinationAddress.getAddress() + ":" + destinationAddress.getPort() +
        uri.toString();
  }
}
