package com.obsidiandynamics.yconf;

import java.util.*;

@Y(MapConf.Mapper.class)
public final class MapConf {
  public static final class Mapper implements TypeMapper {
    @Override
    public Object map(YObject y, Class<?> type) {
      final MapConf r = new MapConf();
      y.asMap().forEach((k, v) -> r.map.put(k, v.map(Object.class)));
      return r;
    }
  }
  
  final Map<String, Object> map = new HashMap<>();
}