package com.obsidiandynamics.yconf;

import static junit.framework.TestCase.*;

import java.io.*;
import java.util.*;

import org.junit.*;
import org.junit.runner.*;
import org.junit.runners.*;
import org.junit.runners.Parameterized.*;

@RunWith(Parameterized.class)
public final class MapTest {
  @Parameters
  public static Collection<Object[]> data() {
    return Arrays.asList(new Object[][] {
      { "map-test.json", new GsonParser() }, 
      { "map-test.yaml", new SnakeyamlParser() }
    });
  }
  
  @Parameter(0)
  public String file;

  @Parameter(1)
  public Parser parser;
  
  @Test
  public void test() throws IOException {
    final Map<?, ?> map = new MappingContext()
        .withParser(parser)
        .fromStream(MapTest.class.getClassLoader().getResourceAsStream(file))
        .map(Map.class);
    assertNotNull(map);
    assertEquals(2, map.size());
    
    final MapConf a = (MapConf) map.get("a");
    assertEquals(2, a.map.size());
    assertEquals("AA", a.map.get("aa"));
    assertEquals("AB", a.map.get("ab"));
    
    final MapConf b = (MapConf) map.get("b");
    assertEquals(2, b.map.size());
    assertEquals("BA", b.map.get("ba"));
    assertEquals("BB", b.map.get("bb"));
  }
}
