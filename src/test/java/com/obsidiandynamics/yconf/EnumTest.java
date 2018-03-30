package com.obsidiandynamics.yconf;

import static org.junit.Assert.*;

import java.io.*;

import org.junit.*;

public final class EnumTest {
  public enum TestEnum {
    ALPHA, BETA, GAMMA
  }
  
  @Test
  public void test() throws IOException {
    final TestEnum e = new MappingContext().map("ALPHA", TestEnum.class);
    assertNotNull(e);
    assertEquals(TestEnum.ALPHA, e);
  }
}
