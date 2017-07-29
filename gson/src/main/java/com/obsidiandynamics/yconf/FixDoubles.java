package com.obsidiandynamics.yconf;

import java.util.*;

final class FixDoubles {
  private FixDoubles() {}
  
  static Object fix(Object orig) {
    if (orig instanceof List) {
      final List<?> list = MappingContext.cast(orig);
      final List<Object> copy = new ArrayList<>(list.size());
      for (Object obj : list) {
        copy.add(fix(obj));
      }
      return copy;
    } else if (orig instanceof Map) {
      final Map<?, ?> map = MappingContext.cast(orig);
      final Map<Object, Object> copy = new LinkedHashMap<>(map.size());
      for (Map.Entry<?, ?> entry : map.entrySet()) {
        copy.put(entry.getKey(), fix(entry.getValue()));
      }
      return copy;
    } else if (orig instanceof Double) {
      final Double d = (Double) orig;
      if (d.intValue() == d) {
        return d.intValue();
      } else if (d.longValue() == d) {
        return d.longValue();
      } else {
        return d;
      }
    } else {
      return orig;
    }
  }
}
