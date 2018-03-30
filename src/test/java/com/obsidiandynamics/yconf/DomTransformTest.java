package com.obsidiandynamics.yconf;

import static junit.framework.TestCase.*;

import java.util.*;
import java.util.stream.*;

import org.junit.*;

public final class DomTransformTest {
  private static final DomTransform STRING_CAPITALIZER = (dom, context) -> {
    if (dom instanceof String) {
      return ((String) dom).toUpperCase();
    } else {
      return dom;
    }
  };

  @Test
  public void testLowerToUpper() {
    final String s = new MappingContext().withDomTransform(STRING_CAPITALIZER).map("hello", String.class);
    assertEquals("HELLO", s);
  }

  @Test
  public void testNull() {
    final String s = new MappingContext().withDomTransform(STRING_CAPITALIZER).map(null, String.class);
    assertNull(s);
  }

  @Test
  public void testList() {
    final List<?> out = new MappingContext()
        .withDomTransform(STRING_CAPITALIZER)
        .withMapper(List.class, (y, type) -> y.asList().stream()
                    .map(i -> i.map(String.class)).collect(Collectors.toList()))
        .map(Arrays.asList("foo", "bar"), List.class);
    assertEquals(Arrays.asList("FOO", "BAR"), out);
  }

  @Test
  public void testMap() {
    final Map<Object, Object> dom = new FluentMap<>()
        .with("f", "foo")
        .with("b", "bar");
    
    class Tuple<K, V> {
      final K k;
      final V v;
      
      Tuple(K k, V v) {
        this.k = k;
        this.v = v;
      }
    }
    
    final Map<?, ?> out = new MappingContext()
        .withDomTransform(STRING_CAPITALIZER)
        .withMapper(Map.class, (y, type) -> y.asMap().entrySet().stream()
                    .map(e -> new Tuple<>(e.getKey(), e.getValue().map(String.class)))
                    .collect(Collectors.toMap(t -> t.k, t -> t.v)))
        .map(dom, Map.class);
    
    final Map<?, ?> upperMap = new FluentMap<>()
        .with("f", "FOO")
        .with("b", "BAR");
    
    assertEquals(upperMap, out);
  }
}
