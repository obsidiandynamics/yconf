package com.obsidiandynamics.yconf;

import static org.junit.Assert.*;

import java.io.*;
import java.util.*;

import org.junit.*;

import com.obsidiandynamics.assertion.*;

public final class FixDoublesTest {
  @Test
  public void testConformance() throws Exception {
    Assertions.assertUtilityClassWellDefined(FixDoubles.class);
  }
  
  @Test
  public void testDouble() {
    assertEquals(123.4, FixDoubles.fix(123.4));
  }
  
  @Test
  public void testInt() {
    assertEquals(123, FixDoubles.fix(123.0));
  }
  
  @Test
  public void testLong() {
    assertEquals(8_000_000_000L, FixDoubles.fix(8_000_000_000.0));
  }
  
  @Test
  public void testList() {
    assertEquals(Collections.singletonList(8_000_000_000L), FixDoubles.fix(Collections.singletonList(8_000_000_000.0)));
  }
  
  @Test
  public void testMap() {
    assertEquals(singletonMap(Collections.singletonList(8_000_000_000L)),
                 singletonMap(FixDoubles.fix(Collections.singletonList(8_000_000_000.0))));
  }
  
  @Test
  public void testParserFix() throws IOException {
    final Object out = new GsonParser().withFixDoubles(true).load(new StringReader("123.0"));
    assertEquals(Integer.class, out.getClass());
    assertEquals(123, out);
  }
  
  @Test
  public void testParserNoFix() throws IOException {
    final Object out = new GsonParser().withFixDoubles(false).load(new StringReader("123.0"));
    assertEquals(Double.class, out.getClass());
    assertEquals(123.0, out);
  }
  
  private static Map<?, ?> singletonMap(Object value) {
    final Map<Object, Object> map = new LinkedHashMap<>();
    map.put("key", value);
    return map;
  }
}
