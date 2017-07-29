package com.obsidiandynamics.yconf;

import java.io.*;
import java.util.concurrent.atomic.*;

import org.junit.Test;

import junit.framework.*;

public final class YConditionalTest {
  @Test
  public void testConditionalValueSet() throws IOException {
    final AtomicReference<?> out = new MappingContext()
        .withParser(new SnakeyamlParser())
        .withMapper(AtomicReference.class, (y, type) -> {
          final AtomicReference<?> ref = new AtomicReference<>("original");
          y.when("f").then(v -> ref.set(v.value()));
          return ref;
        }).fromString("f: foo", AtomicReference.class);
    TestCase.assertEquals("foo", out.get());
  }

  @Test
  public void testConditionalValueNotSet() throws IOException {
    final AtomicReference<?> out = new MappingContext()
        .withParser(new SnakeyamlParser())
        .withMapper(AtomicReference.class, (y, type) -> {
          final AtomicReference<?> ref = new AtomicReference<>("original");
          y.when("x").then(v -> ref.set(v.value()));
          return ref;
        }).fromString("f: foo", AtomicReference.class);
    TestCase.assertEquals("original", out.get());
  }

  @Test
  public void testConditionalMapSet() throws IOException {
    final AtomicReference<?> out = new MappingContext()
        .withParser(new SnakeyamlParser())
        .withMapper(AtomicReference.class, (y, type) -> {
          final AtomicReference<Object> ref = new AtomicReference<>("original");
          y.when("f").thenMap(Object.class, ref::set);
          return ref;
        }).fromString("f: foo", AtomicReference.class);
    TestCase.assertEquals("foo", out.get());
  }

  @Test
  public void testConditionalMapNotSet() throws IOException {
    final AtomicReference<?> out = new MappingContext()
        .withParser(new SnakeyamlParser())
        .withMapper(AtomicReference.class, (y, type) -> {
          final AtomicReference<Object> ref = new AtomicReference<>("original");
          y.when("x").thenMap(Object.class, ref::set);
          return ref;
        }).fromString("f: foo", AtomicReference.class);
    TestCase.assertEquals("original", out.get());
  }
}
