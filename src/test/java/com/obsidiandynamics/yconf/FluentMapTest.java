package com.obsidiandynamics.yconf;

import static org.junit.Assert.*;

import org.junit.*;

public final class FluentMapTest {
  @Test
  public void test() {
    final FluentMap<?, ?> m = new FluentMap<>()
        .with("a", "A")
        .with("b", "B");
    
    assertEquals(2, m.size());
    assertEquals("A", m.get("a"));
    assertEquals("B", m.get("b"));
  }
}
