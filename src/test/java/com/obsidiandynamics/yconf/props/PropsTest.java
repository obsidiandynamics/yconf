package com.obsidiandynamics.yconf.props;

import static org.junit.Assert.*;

import java.util.*;

import org.junit.*;

import com.obsidiandynamics.assertion.*;

public final class PropsTest {
  @Test
  public void testConformance() throws Exception {
    Assertions.assertUtilityClassWellDefined(Props.class);
  }
  
  @Test
  public void testMerge() {
    final Properties a = new Properties();
    a.setProperty("a", "A");
    final Properties b = new Properties();
    b.setProperty("b", "B");
    final Properties merged = Props.merge(a, b);
    assertEquals("A", merged.getProperty("a"));
    assertEquals("B", merged.getProperty("b"));
  }
}
