package com.obsidiandynamics.yconf;

import static junit.framework.TestCase.*;

import java.io.*;
import java.util.*;

import org.junit.*;
import org.junit.runner.*;
import org.junit.runners.*;
import org.junit.runners.Parameterized.*;

@RunWith(Parameterized.class)
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
  
  @Parameters
  public static Collection<Object[]> data() {
    return Arrays.asList(new Object[][] {
      { "link-from.json", new GsonParser() }, 
      { "link-from.yaml", new SnakeyamlParser() }
    });
  }
  
  @Parameter(0)
  public String file;

  @Parameter(1)
  public Parser parser;
  
  @Test
  public void test() throws IOException {
    final LinkFrom from = new MappingContext()
        .withParser(parser)
        .withDomTransform(new JuelTransform())
        .fromStream(LinkedObjectsTest.class.getClassLoader().getResourceAsStream(file))
        .map(LinkFrom.class);
    assertNotNull(from);
    assertNotNull(from.link);
    final LinkTo to = from.link;
    assertEquals("Some string", to.aString);
  }
}
