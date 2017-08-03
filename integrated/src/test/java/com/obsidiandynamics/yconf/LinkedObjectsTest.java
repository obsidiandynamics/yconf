package com.obsidiandynamics.yconf;

import static junit.framework.TestCase.*;

import java.io.*;

import org.junit.*;

public final class LinkedObjectsTest {
  @Y
  public static class LinkFrom {
    @YInject
    public LinkTo link;
  }
  
  @Y
  public static class LinkTo {
    @YInject
    public String aString;
  }
  
  @Test
  public void test() throws IOException {
    final LinkFrom from = new MappingContext()
        .withParser(new SnakeyamlParser())
        .withDomTransform(new JuelTransform())
        .fromStream(LinkedObjectsTest.class.getClassLoader().getResourceAsStream("link-from.yaml"), LinkFrom.class);
    assertNotNull(from);
    assertNotNull(from.link);
    final LinkTo to = (LinkTo) from.link;
    assertEquals("Some string", to.aString);
  }
}
