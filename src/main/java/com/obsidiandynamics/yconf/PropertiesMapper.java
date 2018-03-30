package com.obsidiandynamics.yconf;

import java.util.*;

public final class PropertiesMapper implements TypeMapper {
  @Override
  public Object map(YObject y, Class<?> type) {
    final Map<String, YObject> source = y.asMap();
    final Properties target = new Properties();
    for (Map.Entry<String, YObject> entry : source.entrySet()) {
      target.setProperty(entry.getKey(), entry.getValue().map(String.class));
    }
    return target;
  }
}
