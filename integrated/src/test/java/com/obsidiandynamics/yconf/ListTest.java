package com.obsidiandynamics.yconf;

import static junit.framework.TestCase.*;

import java.io.*;
import java.util.*;

import org.junit.*;
import org.junit.runner.*;
import org.junit.runners.*;
import org.junit.runners.Parameterized.*;

@RunWith(Parameterized.class)
public final class ListTest {
  @Parameters
  public static Collection<Object[]> data() {
    return Arrays.asList(new Object[][] {
      { "list-test.json", new GsonParser() }, 
      { "list-test.yaml", new SnakeyamlParser() }
    });
  }
  
  @Parameter(0)
  public String file;

  @Parameter(1)
  public Parser parser;
  
  @Test
  public void test() throws IOException {
    final List<?> list = new MappingContext()
        .withParser(parser)
        .fromStream(ListTest.class.getClassLoader().getResourceAsStream(file))
        .map(List.class);
    assertNotNull(list);
    assertEquals(2, list.size());
    
    final MapConf a = (MapConf) list.get(0);
    assertEquals(2, a.map.size());
    assertEquals("AA", a.map.get("aa"));
    assertEquals("AB", a.map.get("ab"));
    
    final MapConf b = (MapConf) list.get(1);
    assertEquals(2, b.map.size());
    assertEquals("BA", b.map.get("ba"));
    assertEquals("BB", b.map.get("bb"));
  }
}
