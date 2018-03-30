package com.obsidiandynamics.yconf;

import java.util.*;

public final class FluentMap<K, V> extends LinkedHashMap<K, V> {
  private static final long serialVersionUID = 1L;

  public FluentMap<K, V> with(K key, V value) {
    put(key, value);
    return this;
  }
}
