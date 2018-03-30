package com.obsidiandynamics.yconf.sample.inline;

import static org.junit.Assert.*;

import java.io.*;

import org.junit.*;

import com.obsidiandynamics.yconf.*;

public final class LogConfigTest {
  @Test
  public void test() throws IOException {
    final LogConfig config = new MappingContext()
        .withParser(new SnakeyamlParser())
        .fromStream(LogConfigTest.class.getClassLoader().getResourceAsStream("sample-inline.yaml"))
        .map(LogConfig.class);
    assertNotNull(config.getLogger());
    assertEquals("org.myproject.MyLogger", config.getLogger().getName());
  }
}
