package com.obsidiandynamics.yconf;

import static org.junit.Assert.*;

import java.util.*;

import org.junit.*;

import com.obsidiandynamics.indigo.util.*;

public final class FixDoublesTest {
  @Test
  public void testConformance() throws Exception {
    TestSupport.assertUtilityClassWellDefined(FixDoubles.class);
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
    assertEquals(Arrays.asList(8_000_000_000L), FixDoubles.fix(Arrays.asList(8_000_000_000.0)));
  }
  
  @Test
  public void testMap() {
    assertEquals(singletonMap(Arrays.asList(8_000_000_000L)), 
                 singletonMap(FixDoubles.fix(Arrays.asList(8_000_000_000.0))));
  }
  
  private static Map<?, ?> singletonMap(Object value) {
    final Map<Object, Object> map = new LinkedHashMap<>();
    map.put("key", value);
    return map;
  }
}
