package com.obsidiandynamics.yconf;

import static junit.framework.TestCase.*;

import java.io.*;
import java.util.*;
import java.util.stream.*;

import org.junit.*;
import org.junit.runner.*;
import org.junit.runners.*;
import org.junit.runners.Parameterized.*;

@RunWith(Parameterized.class)
public final class MappingTest {
  public interface FooBarIface {}

  public static final class FooBar implements FooBarIface {
    Foo foo;
    Object bar;

    FooBar(Foo foo, Object bar) {
      this.foo = foo;
      this.bar = bar;
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((bar == null) ? 0 : bar.hashCode());
      result = prime * result + ((foo == null) ? 0 : foo.hashCode());
      return result;
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj)
        return true;
      if (obj == null)
        return false;
      if (getClass() != obj.getClass())
        return false;
      FooBar other = (FooBar) obj;
      if (bar == null) {
        if (other.bar != null)
          return false;
      } else if (!bar.equals(other.bar))
        return false;
      if (foo == null) {
        if (other.foo != null)
          return false;
      } else if (!foo.equals(other.foo))
        return false;
      return true;
    }

    @Override
    public String toString() {
      return "FooBar [foo=" + foo + ", bar=" + bar + "]";
    }
  }

  @Y(Foo.Mapper.class)
  public static final class Foo {
    static final class Mapper implements TypeMapper {
      @Override
      public Object map(YObject y, Class<?> type) {
        return new Foo(y.mapAttribute("a", String.class),
                       y.mapAttribute("b", Integer.class),
                       y.mapAttribute("c", Boolean.class));
      }
    }

    String a;
    int b;
    Boolean c;

    Foo(String a, int b, Boolean c) {
      this.a = a;
      this.b = b;
      this.c = c;
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((a == null) ? 0 : a.hashCode());
      result = prime * result + b;
      result = prime * result + ((c == null) ? 0 : c.hashCode());
      return result;
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj)
        return true;
      if (obj == null)
        return false;
      if (getClass() != obj.getClass())
        return false;
      Foo other = (Foo) obj;
      if (a == null) {
        if (other.a != null)
          return false;
      } else if (!a.equals(other.a))
        return false;
      if (b != other.b)
        return false;
      if (c == null) {
        if (other.c != null)
          return false;
      } else if (!c.equals(other.c))
        return false;
      return true;
    }

    @Override
    public String toString() {
      return "Foo [a=" + a + ", b=" + b + ", c=" + c + "]";
    }
  }

  @Y(Bar.Mapper.class)
  public static final class Bar {
    static final class Mapper implements TypeMapper {
      @Override
      public Object map(YObject y, Class<?> type) {
        final List<YObject> itemsYaml = y.getAttribute("items").asList();
        final List<Object> items = itemsYaml.stream().map(itemYaml -> itemYaml.map(Object.class)).collect(Collectors.toList());
        return new Bar(y.mapAttribute("num", Integer.class), items);
      }
    }

    Integer num;

    List<Object> items;

    Bar(Integer num, List<Object> items) {
      this.num = num;
      this.items = items;
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((items == null) ? 0 : items.hashCode());
      result = prime * result + ((num == null) ? 0 : num.hashCode());
      return result;
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj)
        return true;
      if (obj == null)
        return false;
      if (getClass() != obj.getClass())
        return false;
      Bar other = (Bar) obj;
      if (items == null) {
        if (other.items != null)
          return false;
      } else if (!items.equals(other.items))
        return false;
      if (num == null) {
        if (other.num != null)
          return false;
      } else if (!num.equals(other.num))
        return false;
      return true;
    }

    @Override
    public String toString() {
      return "Bar [num=" + num + ", items=" + items + "]";
    }
  }

  @Parameters
  public static Collection<Object[]> data() {
    return Arrays.asList(new Object[][] {
      { "mapping-test.json", new GsonParser() }, 
      { "mapping-test.yaml", new SnakeyamlParser() }
    });
  }
  
  @Parameter(0)
  public String file;

  @Parameter(1)
  public Parser parser;
  
  @Test
  public void testNormal() throws IOException {
    final FooBar fb = (FooBar) new MappingContext()
        .withParser(parser)
        .withMapper(Object.class, new RuntimeMapper()
                    .withTypeAttribute("_type")
                    .withTypeFormatter("com.obsidiandynamics.yconf."::concat))
        .withMapper(FooBar.class, (y, type) -> new FooBar(y.getAttribute("foo").map(Foo.class), y.mapAttribute("bar", Object.class)))
        .fromStream(MappingTest.class.getClassLoader().getResourceAsStream(file))
        .map(FooBarIface.class);

    final FooBar expected = new FooBar(new Foo("A string", 123, false), 
                                       new Bar(42, Arrays.asList(new Foo("Another string", 456, null),
                                                                 new Foo(null, 789, null),
                                                                 null,
                                                                 null,
                                                                 new Foo(null, 789, null))));
    assertEquals(expected, fb);
  }

  @Test
  public void testWithoutMapper() throws IOException {
    final Object obj = new MappingContext()
        .withParser(parser)
        .fromStream(MappingTest.class.getClassLoader().getResourceAsStream(file))
        .map(FooBar.class);
    assertNotNull(obj);
    assertEquals(LinkedHashMap.class, obj.getClass());
  }
}
