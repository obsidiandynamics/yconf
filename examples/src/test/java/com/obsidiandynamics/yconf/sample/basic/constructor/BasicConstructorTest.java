package com.obsidiandynamics.yconf.sample.basic.constructor;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.*;

import java.io.*;

import org.junit.*;

import com.obsidiandynamics.yconf.*;

public final class BasicConstructorTest {
  @Test
  public void test() throws IOException {
    final Top top = new MappingContext()
        .withParser(new SnakeyamlParser())
        .withDomTransform(new JuelTransform())
        .fromStream(BasicConstructorTest.class.getClassLoader().getResourceAsStream("sample-basic.yaml"))
        .map(Top.class);
    assertNotNull(top);
    assertEquals(3.14, top.aNumber, 0.0001);
    assertEquals("hello", top.aString);
    assertNotNull(top.inner);
    assertTrue(top.inner.aBool);
    assertArrayEquals(new String[] {"a", "b", "c"}, top.inner.anArray);
  }
}
