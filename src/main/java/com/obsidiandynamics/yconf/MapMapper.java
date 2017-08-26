package com.obsidiandynamics.yconf;

import java.util.*;

public final class MapMapper implements TypeMapper {
  @Override
  public Object map(YObject y, Class<?> type) {
    final Map<String, YObject> source = y.asMap();
    final Map<String, Object> target = new LinkedHashMap<>(source.size());
    for (Map.Entry<String, YObject> entry : source.entrySet()) {
      target.put(entry.getKey(), entry.getValue().map(Object.class));
    }
    return target;
  }
}
