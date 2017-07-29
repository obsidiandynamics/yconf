package com.obsidiandynamics.yconf;

import static junit.framework.TestCase.*;

import java.io.*;
import java.util.*;

import org.junit.Test;

import junit.framework.*;

public final class BasicMappingTest {
  private static Map<?, ?> getFooMap() {
    return Collections.singletonMap("f", "foo");
  }
  
  @Test
  public void testWithoutMapperAttribute() throws IOException {
    final Object obj = new MappingContext()
        .map(Collections.singletonMap("a", "b"), Object.class);
    assertEquals(Collections.singletonMap("a", "b"), obj);
  }

  @Test(expected=MappingException.class)
  public void testExplicitTypeNotFound() throws IOException {
    new MappingContext()
    .map(Collections.singletonMap("type", "java.Foo"), Object.class);
  }

  @Y(TestType.Mapper.class)
  private static class TestType {
    static abstract class Mapper implements TypeMapper {}
  }

  @Test(expected=MappingException.class)
  public void testUninstantiableMapper() throws IOException {
    new MappingContext()
    .map(Collections.singletonMap("type", TestType.class.getName()), Object.class);
  }

  @Test
  public void testGetContext() throws IOException {
    final Object out = new MappingContext()
        .withMapper(Void.class, (y, type) -> {
          return y.getContext().map(y.getAttribute("f").value(), String.class);
        }).map(getFooMap(), Void.class);
    TestCase.assertEquals("foo", out);
  }

  @Test
  public void testIsNotType() throws IOException {
    final Object out = new MappingContext()
        .withMapper(Void.class, (y, type) -> {
          assertTrue(y.is(Map.class));
          return "done";
        }).map(getFooMap(), Void.class);
    TestCase.assertEquals("done", out);
  }

  @Test
  public void testIsType() throws IOException {
    final Object out = new MappingContext()
        .withMapper(Void.class, (y, type) -> {
          assertFalse(y.is(Integer.class));
          return "done";
        }).map(getFooMap(), Void.class);
    TestCase.assertEquals("done", out);
  }

  @Test
  public void testIsTypeWithNull() throws IOException {
    final Object out = new MappingContext()
        .withMapper(Void.class, (y, type) -> {
          assertFalse(y.getAttribute("a").is(Integer.class));
          return "done";
        }).map(getFooMap(), Void.class);
    TestCase.assertEquals("done", out);
  }

  @Test(expected=NullPointerException.class)
  public void testAsListNPE() throws IOException {
    new MappingContext()
    .withMapper(Void.class, (y, type) -> {
      y.getAttribute("a").asList();
      return null;
    }).map(getFooMap(), Void.class);
  }
}