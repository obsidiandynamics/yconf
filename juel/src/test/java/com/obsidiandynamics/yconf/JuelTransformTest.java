package com.obsidiandynamics.yconf;

import static org.junit.Assert.*;

import org.junit.*;

public final class JuelTransformTest {
  @Test
  public void testNonString() {
    final Object in = new Object();
    final Object out = new JuelTransform().transform(in, new MappingContext());
    assertEquals(in, out);
  }

  @Test
  public void testPlainString() {
    final Object in = "Hello";
    final Object out = new JuelTransform().transform(in, new MappingContext());
    assertEquals(in, out);
  }
  
  @Test
  public void testVariable() {
    final Object out = new JuelTransform()
        .withVariable("foo", "bar")
        .transform("${foo}", new MappingContext());
    assertEquals("bar", out);
  }
  
  @Test
  public void testFunctionNoNamespace() throws NoSuchMethodException, SecurityException {
    final Object out = new JuelTransform()
        .withFunction("round", Math.class.getMethod("round", double.class))
        .transform("${round(round(12.34))}", new MappingContext());
    assertEquals(12L, out);
  }
  
  @Test
  public void testFunctionNamespace() throws NoSuchMethodException, SecurityException {
    final Object out = new JuelTransform()
        .withFunction("math", "round", Math.class.getMethod("round", double.class))
        .transform("${math:round(12.34)}", new MappingContext());
    assertEquals(12L, out);
  }
  
  @Test(expected=RuntimeException.class)
  public void testConfiguratorException() {
    new JuelTransform().configure(t -> {
      throw new Exception();
    });
  }

  @Test
  public void testGetEnvExisting() {
    final String value = JuelTransform.getEnv("HOME", null);
    assertNotNull(value);
  }

  @Test
  public void testGetEnvNonExistent() {
    final String defaultValue = "someDefaultValue";
    final String value = JuelTransform.getEnv("GIBB_BB_BBERISH", defaultValue);
    assertSame(defaultValue, value);
  }

  @Test
  public void testNullCoerce() {
    assertEquals("nonNullString", JuelTransform.nullCoerce("nonNullString"));
    assertNull(JuelTransform.nullCoerce(null));
    assertNull(JuelTransform.nullCoerce(""));
  }
}
