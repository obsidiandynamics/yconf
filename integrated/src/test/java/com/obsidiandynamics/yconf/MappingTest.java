package com.obsidiandynamics.yconf;

import static junit.framework.TestCase.*;

import java.io.*;
import java.util.*;

import org.junit.Test;

import junit.framework.*;

public final class MappingTest {
  public void testWithoutMapperAttribute() throws IOException {
    final String yaml = "a: b";
    final Object obj = new MappingContext()
        .withParser(new SnakeyamlParser())
        .fromReader(new StringReader(yaml), Object.class);
    final Map<String, String> expected = new LinkedHashMap<>();
    expected.put("a", "b");
    assertEquals(expected, obj);
  }

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

  @Test(expected=MappingException.class)
  public void testExplicitTypeNotFound() throws IOException {
    final String yaml = "type: java.Foo";
    new MappingContext()
    .withParser(new SnakeyamlParser())
    .fromString(yaml, Object.class);
  }

  @Test
  public void testFromString() throws IOException {
    final String yaml = "a: b";
    new MappingContext()
    .withParser(new SnakeyamlParser())
    .fromString(yaml, Object.class);
  }

  @Test
  public void testFromReader() throws IOException {
    final String yaml = "a: b";
    new MappingContext()
    .withParser(new SnakeyamlParser())
    .fromReader(new StringReader(yaml), Object.class);
  }

  @Y(TestType.Mapper.class)
  private static class TestType {
    static abstract class Mapper implements TypeMapper {}
  }

  @Test(expected=MappingException.class)
  public void testUninstantiableMapper() throws IOException {
    final String yaml = "type: " + TestType.class.getName();
    new MappingContext()
    .withParser(new SnakeyamlParser())
    .fromString(yaml, Object.class);
  }

  @Test(expected=IllegalArgumentException.class)
  public void testMapYObject() {
    new MappingContext().map(new YObject(null, new MappingContext()), null);
  }

  @Test
  public void testGetContext() throws IOException {
    final Object out = new MappingContext()
        .withParser(new SnakeyamlParser())
        .withMapper(Void.class, (y, type) -> {
          return y.getContext().map(y.getAttribute("f").value(), String.class);
        }).fromString("f: foo", Void.class);
    TestCase.assertEquals("foo", out);
  }

  @Test
  public void testIsNotType() throws IOException {
    final Object out = new MappingContext()
        .withParser(new SnakeyamlParser())
        .withMapper(Void.class, (y, type) -> {
          assertTrue(y.is(Map.class));
          return "done";
        }).fromString("f: foo", Void.class);
    TestCase.assertEquals("done", out);
  }

  @Test
  public void testIsType() throws IOException {
    final Object out = new MappingContext()
        .withParser(new SnakeyamlParser())
        .withMapper(Void.class, (y, type) -> {
          assertFalse(y.is(Integer.class));
          return "done";
        }).fromString("f: foo", Void.class);
    TestCase.assertEquals("done", out);
  }

  @Test
  public void testIsTypeWithNull() throws IOException {
    final Object out = new MappingContext()
        .withParser(new SnakeyamlParser())
        .withMapper(Void.class, (y, type) -> {
          assertFalse(y.getAttribute("a").is(Integer.class));
          return "done";
        }).fromString("f: foo", Void.class);
    TestCase.assertEquals("done", out);
  }

  @Test(expected=NullPointerException.class)
  public void testAsListNPE() throws IOException {
    new MappingContext()
    .withParser(new SnakeyamlParser())
    .withMapper(Void.class, (y, type) -> {
      y.getAttribute("a").asList();
      return null;
    }).fromString("f: foo", Void.class);
  }
}
