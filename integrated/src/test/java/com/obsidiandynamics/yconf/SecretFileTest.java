package com.obsidiandynamics.yconf;

import static org.junit.Assert.*;

import java.io.*;

import org.junit.*;

public final class SecretFileTest {
  @Y
  public static final class ConfigWithSecret {
    @YInject
    String plain;
    
    @YInject
    Secret secret;
  }
  
  @Test
  public void testConfig() throws IOException {
    final ConfigWithSecret config = new MappingContext()
        .withParser(new SnakeyamlParser())
        .withDomTransform(new JuelTransform())
        .fromStream(SecretFileTest.class.getClassLoader().getResourceAsStream("secret-test.yaml"))
        .map(ConfigWithSecret.class);
    assertNotNull(config);
    assertEquals("plainValue", config.plain);
    assertEquals("secretValue", config.secret.unmask());
  }
}
