package com.obsidiandynamics.yconf.sample.custom;

import java.net.*;
import java.util.*;

import com.obsidiandynamics.yconf.*;

@Y(WebConfig.Mapper.class)
public final class WebConfig {
  public static final class Mapper implements TypeMapper {
    @Override public Object map(YObject y, Class<?> type) {
      final Map<String, URI> servers = new HashMap<>();
      for (YObject server : y.asList()) {
        final String name = server.mapAttribute("name", String.class);
        if (servers.containsKey(name)) {
          throw new WebConfigException("Duplicate server name " + name, null);
        }
        
        final String protocol = server.mapAttribute("protocol", String.class);
        final String host = server.mapAttribute("host", String.class);
        final Integer port = server.mapAttribute("port", Integer.class);
        final String path = server.mapAttribute("path", String.class);
        final URI uri;
        try {
          uri = new URI(protocol, null, host, port != null ? port : -1, path, null, null);
        } catch (URISyntaxException e) {
          throw new WebConfigException("Error parsing URI", e);
        }
        servers.put(name, uri);
      }
      return new WebConfig(servers);
    }
  }
  
  static final class WebConfigException extends MappingException {
    private static final long serialVersionUID = 1L;
    WebConfigException(String m, Exception cause) { super(m, cause); }
  }
  
  final Map<String, URI> servers;

  WebConfig(Map<String, URI> servers) {
    this.servers = servers;
  }
}
