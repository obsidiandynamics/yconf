package com.obsidiandynamics.yconf;

import static org.junit.Assert.*;

import java.io.*;
import java.util.*;

import org.junit.*;

public final class SecretTest {
  @Test
  public void testDirect() {
    final String value = "truth";
    final Secret secret = Secret.of(value);
    assertNotEquals(value, secret.toString());
    assertEquals(value, Secret.unmask(secret));
  }
  
  @Test
  public void testMapper() throws IOException {
    final String value = "truth";
    final Secret secret = new MappingContext()
        .map(Collections.singletonMap("value", value), Secret.class);
    assertNotNull(secret);
    assertEquals(value, Secret.unmask(secret));
  }
  
  @Test
  public void testNonSecret() {
    final String value = "plain";
    assertEquals(value, Secret.unmask(value));
  }
  
  @Test
  public void testNull() {
    assertNull(Secret.unmask(null));
  }
}
