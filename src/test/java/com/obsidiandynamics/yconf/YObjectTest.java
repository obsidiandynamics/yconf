package com.obsidiandynamics.yconf;

import static junit.framework.TestCase.*;

import org.junit.*;

public final class YObjectTest {
  @Test(expected=IllegalArgumentException.class)
  public void testWrapObject() {
    new YObject(new YObject("foo", new MappingContext()), null);
  }

  @Test
  public void testObjectToString() {
    assertEquals("foo", new YObject("foo", new MappingContext()).toString());
  }

  @Test(expected=NullPointerException.class)
  public void testNullList() {
    new YObject(null, null).asList();
  }

  @Test(expected=IllegalArgumentException.class)
  public void testMapYObject() {
    new MappingContext().map(new YObject(null, new MappingContext()), null);
  }
}
