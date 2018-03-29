package com.obsidiandynamics.yconf.props;

import java.util.*;

public final class Props {
  private Props() {}
  
  public static Properties merge(Properties... propertiesArray) {
    final Properties merged = new Properties();
    for (Properties props : propertiesArray) {
      Collections.list(props.propertyNames()).stream()
      .map(o -> (String) o)
      .forEach(key -> merged.put(key, props.getProperty(key)));
    }
    return merged;
  }
}
