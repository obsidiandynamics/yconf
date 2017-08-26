package com.obsidiandynamics.yconf;

import java.util.*;
import java.util.stream.*;

public final class ListMapper implements TypeMapper {
  @Override
  public Object map(YObject y, Class<?> type) {
    return y.asList().stream().map(o -> o.map(Object.class)).collect(Collectors.toCollection(ArrayList::new));
  }
}
