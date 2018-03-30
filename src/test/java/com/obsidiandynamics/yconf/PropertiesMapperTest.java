package com.obsidiandynamics.yconf;

import static org.junit.Assert.*;

import java.io.*;
import java.util.*;

import org.junit.*;

public final class PropertiesMapperTest {
  @Y
  public static final class MapMapperTestClass {}

  @Test
  public void test() throws IOException {
    final Properties props = new MappingContext()
    .withParser(reader -> {
      final Map<String, Object> map = new HashMap<>();
      map.put("a", "A");
      map.put("b", false);
      map.put("c", 100);
      return map;
    })
    .fromReader(null)
    .map(Properties.class);
    
    assertEquals(3, props.size());
    assertEquals("A", props.getProperty("a"));
    assertEquals("false", props.getProperty("b"));
    assertEquals("100", props.getProperty("c"));
  }
}
