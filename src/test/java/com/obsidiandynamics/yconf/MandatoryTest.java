package com.obsidiandynamics.yconf;

import static org.junit.Assert.*;

import java.io.*;
import java.util.*;

import org.junit.*;

import com.obsidiandynamics.assertion.*;
import com.obsidiandynamics.yconf.Mandatory.*;

public final class MandatoryTest {
  @Test
  public void testConformance() throws Exception {
    Assertions.assertUtilityClassWellDefined(Mandatory.class);
  }
  
  @Test
  public void testNonNullValue() {
    final Object val = "test";
    assertEquals(val, Mandatory.of(val, "error"));
  }

  @Test(expected=MissingValueException.class)
  public void testNullValue() {
    Mandatory.of(null, "error");
  }
  
  @Test
  public void testMapperNonNullValue() throws IOException {
    final Map<String, String> dom = new HashMap<>();
    dom.put("value", "test");
    dom.put("error", "bad");
    final Object obj = new MappingContext()
        .map(dom, Mandatory.class);
    assertEquals("test", obj);
  }
  
  @Test(expected=MissingValueException.class)
  public void testMapperNullValue() throws IOException {
    new MappingContext()
    .map(Collections.singletonMap("error", "bad"), Mandatory.class);
  }
}
