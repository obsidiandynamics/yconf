package com.obsidiandynamics.juel;

import static org.junit.Assert.*;

import org.junit.*;

public final class ELTransformTest {
  @Test
  public void testNonString() {
    final Object in = new Object();
    final Object out = new ELTransform().apply(in);
    assertEquals(in, out);
  }
  
  @Test
  public void testVariable() {
    final Object out = new ELTransform()
        .withVariable("foo", "bar")
        .apply("${foo}");
    assertEquals("bar", out);
  }
  
  @Test
  public void testFunctionNoNamespace() throws NoSuchMethodException, SecurityException {
    final Object out = new ELTransform()
        .withFunction("round", Math.class.getMethod("round", double.class))
        .apply("${round(12.34)}");
    assertEquals(12L, out);
  }
  
  @Test
  public void testFunctionNamespace() throws NoSuchMethodException, SecurityException {
    final Object out = new ELTransform()
        .withFunction("math", "round", Math.class.getMethod("round", double.class))
        .apply("${math:round(12.34)}");
    assertEquals(12L, out);
  }
  
  @Test(expected=RuntimeException.class)
  public void testConfiguratorException() {
    new ELTransform().configure(t -> {
      throw new Exception();
    });
  }
}
