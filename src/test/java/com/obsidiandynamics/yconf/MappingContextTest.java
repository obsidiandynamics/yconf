package com.obsidiandynamics.yconf;

import java.io.*;

import org.junit.*;

public final class MappingContextTest {
  @Test(expected=MappingException.class)
  public void testNoParser() throws IOException {
    new MappingContext().fromString("", Object.class);
  }
}
