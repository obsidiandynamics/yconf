package com.obsidiandynamics.yconf.util;

import static junit.framework.TestCase.*;
import static org.junit.Assert.assertNotNull;

import java.io.*;
import java.util.*;

import org.junit.*;

import com.obsidiandynamics.yconf.*;

public final class PropsBuilderTest {
  @Test
  public void testApi() {
    final PropsBuilder builder = new PropsBuilder()
        .with("foo", "bar")
        .with("null", null)
        .withSystemDefault("amount", 100);

    final Properties props = builder.build();
    assertEquals("bar", props.get("foo"));
    assertEquals(props.toString(), builder.toString());
    assertEquals("100", props.getProperty("amount"));
  }

  @Test
  public void testDefault() {
    final Properties defaults = new PropsBuilder()
        .with("amount", 100)
        .build();

    final Properties props = new PropsBuilder()
        .withDefault("amount", defaults, 200)
        .withDefault("amountUseSupplied", defaults, 300)
        .withDefault("amountUseNull", defaults, null)
        .build();

    assertEquals("100", props.getProperty("amount"));
    assertEquals("300", props.getProperty("amountUseSupplied"));
    assertNull(props.getProperty("amountUseNull"));
  }

  @Test
  public void testConfig() throws IOException {
    final PropsBuilder builder = new MappingContext()
        .withParser(reader -> new FluentMap<>()
                    .with("a", "A")
                    .with("b", "B")
                    .with("c", "C"))
        .fromReader(null)
        .map(PropsBuilder.class);

    assertNotNull(builder);
    assertEquals(new PropsBuilder()
                 .with("a", "A")
                 .with("b", "B")
                 .with("c", "C")
                 .build(),
                 builder.build());
  }
}
