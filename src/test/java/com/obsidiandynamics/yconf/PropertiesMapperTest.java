package com.obsidiandynamics.yconf;

import static org.junit.Assert.*;

import java.io.*;
import java.util.*;

import org.junit.*;

public final class PropertiesMapperTest {
  @Test
  public void test() throws IOException {
    final Map<String, Object> dom = new HashMap<>();
    dom.put("a", "A");
    dom.put("b", false);
    dom.put("c", 100);
    
    final Properties props = new MappingContext().map(dom, Properties.class);
    
    assertEquals(3, props.size());
    assertEquals("A", props.getProperty("a"));
    assertEquals("false", props.getProperty("b"));
    assertEquals("100", props.getProperty("c"));
  }
}
