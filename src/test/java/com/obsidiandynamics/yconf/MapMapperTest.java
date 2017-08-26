package com.obsidiandynamics.yconf;

import static org.junit.Assert.*;

import java.util.*;

import org.junit.*;

public final class MapMapperTest {
  @Y
  public static final class MapMapperTestClass {}

  @Test
  public void test() {
    final Map<String, Object> dom = new LinkedHashMap<>();
    dom.put("a", Collections.singletonMap("type", MapMapperTestClass.class.getName()));
    dom.put("b", Collections.singletonMap("type", MapMapperTestClass.class.getName()));
    dom.put("c", Collections.singletonMap("type", MapMapperTestClass.class.getName()));
    final Map<?, ?> map = new MappingContext()
        .map(dom, Map.class);
    assertEquals(3, map.size());
    assertEquals(MapMapperTestClass.class, map.get("a").getClass());
    assertEquals(MapMapperTestClass.class, map.get("b").getClass());
    assertEquals(MapMapperTestClass.class, map.get("c").getClass());
  }
}
