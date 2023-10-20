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

  //todo - substring(1) to omit the '/', the '/'' is in the netty transfer result but I don't have the code changed
  // just modify the jackson dependency, netty's version is the same
  public String getHostStr() {
    return destinationAddress.toString().substring(1);
  }

  public URI getUri() {
    return uri;
  }

  public String getUrlString() {
    return "http:/" + destinationAddress.getAddress() + ":" + destinationAddress.getPort() +
        uri.toString();
  }
}
