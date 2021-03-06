package com.obsidiandynamics.yconf;

import static junit.framework.TestCase.*;

import java.io.*;
import java.util.*;

import org.junit.*;
import org.junit.runner.*;
import org.junit.runners.*;
import org.junit.runners.Parameterized.*;

import com.obsidiandynamics.yconf.ReflectiveMapper.*;

@RunWith(Parameterized.class)
public final class ReflectiveTest {
  private enum TestEnum {
    ALPHA, BRAVO, CHARLIE, DELTA
  }
  
  public static abstract class Super {
    @YInject(name="byte", type=byte.class)
    protected byte b;
  }
  
  @Y(Mid.Mapper.class)
  public static class Mid extends Super {
    public static final class Mapper implements TypeMapper {
      @Override public Object map(YObject y, Class<?> type) {
        return y.mapReflectively(new Mid());
      }
    }
    
    @YInject
    public String str;
    
    @YInject(type=Float.class)
    double dub;
    
    @YInject(type=Object.class)
    List<?> list;
    
    @YInject(type=Object.class)
    Map<?, ?> map;
    
    @YInject
    Class<?> cls;
    
    @YInject
    TestEnum en;
  }
  
  @Y(CustomConstruction.Mapper.class)
  public static final class CustomConstruction extends Mid {
    public static final class Mapper implements TypeMapper {
      @Override public Object map(YObject y, Class<?> type) {
        return y.mapReflectively(new CustomConstruction());
      }
    }
    
    @YInject(name="number")
    private int num;
  }

  @Parameters
  public static Collection<Object[]> data() {
    return Arrays.asList(new Object[][] {
      { "reflective-test.json", new GsonParser() }, 
      { "reflective-test.yaml", new SnakeyamlParser() }
    });
  }
  
  @Parameter(0)
  public String file;

  @Parameter(1)
  public Parser parser;
  
  @Test
  public void testInjectAttributesCustomConstruction() throws IOException {
    final CustomConstruction t = new MappingContext()
        .withParser(parser)
        .fromStream(ReflectiveTest.class.getClassLoader().getResourceAsStream(file))
        .map(CustomConstruction.class);
    checkAssertions(t);
    assertEquals(123, t.num);
  }
  
  private static void checkAssertions(Mid m) {
    assertEquals("hello", m.str);
    assertEquals(45.67, m.dub, 0.0001);
    assertEquals(-128, m.b);
    assertEquals(Arrays.asList(1, 2, 3), m.list);
    assertEquals(TestEnum.CHARLIE, m.en);
    
    final Map<String, String> map = new LinkedHashMap<>();
    map.put("a", "foo");
    map.put("b", "bar");
    assertEquals(map, m.map);
    
    assertEquals(String.class, m.cls);
  }
  
  @Y(TestWrongType.Mapper.class)
  public static final class TestWrongType {
    public static final class Mapper implements TypeMapper {
      @Override public Object map(YObject y, Class<?> type) {
        return y.mapReflectively(new TestWrongType());
      }
    }

    @YInject(name="byte", type=String.class)
    public boolean b;
  }
  
  @Y(TestClassNotFound.Mapper.class)
  public static final class TestClassNotFound {
    public static final class Mapper implements TypeMapper {
      @Override public Object map(YObject y, Class<?> type) {
        return y.mapReflectively(new TestClassNotFound());
      }
    }

    @YInject(name="byte")
    public Class<?> cls;
  }

  @Test(expected=MappingException.class)
  public void testReflectiveWrongType() throws IOException {
    new MappingContext()
    .withParser(parser)
    .fromStream(ReflectiveTest.class.getClassLoader().getResourceAsStream(file))
    .map(TestWrongType.class);
  }

  @Test(expected=MappingException.class)
  public void testReflectiveClassNotFound() throws IOException {
    new MappingContext()
    .withParser(parser)
    .fromStream(ReflectiveTest.class.getClassLoader().getResourceAsStream(file))
    .map(TestClassNotFound.class);
  }
  
  @Y(ReflectiveMapper.class)
  public static final class DefaultConstructor extends Mid {
    @YInject(name="number")
    private int num;
  }

  @Test
  public void testDefaultConstructor() throws IOException {
    final DefaultConstructor t = new MappingContext()
        .withParser(parser)
        .fromStream(ReflectiveTest.class.getClassLoader().getResourceAsStream(file))
        .map(DefaultConstructor.class);
    checkAssertions(t);
    assertEquals(123, t.num);
  }
  
  @Y(ReflectiveMapper.class)
  public static final class AnnotatedConstructorMinimal extends Mid {
    private int num;
    
    AnnotatedConstructorMinimal(@YInject(name="number") int num) {
      this.num = num;
    }
  }

  @Test
  public void testAnnotatedConstructorMinimal() throws IOException {
    final AnnotatedConstructorMinimal t = new MappingContext()
        .withParser(parser)
        .fromStream(ReflectiveTest.class.getClassLoader().getResourceAsStream(file)) 
        .map(AnnotatedConstructorMinimal.class);
    checkAssertions(t);
    assertEquals(123, t.num);
  }
  
  @Y(ReflectiveMapper.class)
  public static final class AnnotatedConstructorComplete extends Mid {
    private int num;
    
    AnnotatedConstructorComplete(@YInject(name="byte", type=byte.class) byte b,
                                 @YInject(name="str") String str,
                                 @YInject(name="dub", type=Float.class) double dub,
                                 @YInject(name="list", type=Object.class) List<?> list,
                                 @YInject(name="map", type=Object.class) Map<?, ?> map,
                                 @YInject(name="cls") Class<?> cls,
                                 @YInject(name="number") int num) {
      this.b = b;
      this.str = str;
      this.dub = dub;
      this.list = list;
      this.map = map;
      this.cls = cls;
      this.num = num;
    }
  }

  @Test
  public void testAnnotatedConstructorComplete() throws IOException {
    final AnnotatedConstructorComplete t = new MappingContext()
        .withParser(parser)
        .fromStream(ReflectiveTest.class.getClassLoader().getResourceAsStream(file))
        .map(AnnotatedConstructorComplete.class);
    checkAssertions(t);
    assertEquals(123, t.num);
  }

  @Y(ReflectiveMapper.class)
  public static final class AnnotatedConstructorNoName extends Mid {
    AnnotatedConstructorNoName(@YInject int num) {}
  }

  @Test(expected=NoNameSpecifiedException.class)
  public void testAnnotatedConstructorNoName() throws IOException {
    new MappingContext()
    .withParser(parser)
    .fromStream(ReflectiveTest.class.getClassLoader().getResourceAsStream(file))
    .map(AnnotatedConstructorNoName.class);
  }

  @Y(ReflectiveMapper.class)
  public static final class AnnotatedConstructorNoDefaultConstructor extends Mid {
    AnnotatedConstructorNoDefaultConstructor(int num) {}
  }

  @Test(expected=NoSuitableConstructorException.class)
  public void testAnnotatedConstructorNoDefaultConstructor() throws IOException {
    new MappingContext()
    .withParser(parser)
    .fromStream(ReflectiveTest.class.getClassLoader().getResourceAsStream(file)) 
    .map(AnnotatedConstructorNoDefaultConstructor.class);
  }

  @Y(ReflectiveMapper.class)
  public static final class AnnotatedConstructorPartial extends Mid {
    AnnotatedConstructorPartial(@YInject(name="number") int num, String str) {}
  }

  @Test(expected=NoSuitableConstructorException.class)
  public void testAnnotatedConstructorPartial() throws IOException {
    new MappingContext()
    .withParser(parser)
    .fromStream(ReflectiveTest.class.getClassLoader().getResourceAsStream(file))
    .map(AnnotatedConstructorPartial.class);
  }

  @Y(ReflectiveMapper.class)
  public static final class AnnotatedConstructorPrivate extends Mid {
    private AnnotatedConstructorPrivate() {}
  }

  @Test(expected=NoSuitableConstructorException.class)
  public void testAnnotatedConstructorPrivate() throws IOException {
    new MappingContext()
    .withParser(parser)
    .fromStream(ReflectiveTest.class.getClassLoader().getResourceAsStream(file))
    .map(AnnotatedConstructorPrivate.class);
  }

  @Y(ReflectiveMapper.class)
  public static final class AnnotatedConstructorIllegalArg extends Mid {
    AnnotatedConstructorIllegalArg(@YInject(name="number", type=int.class) char num) {}
  }

  @Test(expected=ObjectInstantiationException.class)
  public void testAnnotatedConstructorIllegalArg() throws IOException {
    new MappingContext()
    .withParser(parser)
    .fromStream(ReflectiveTest.class.getClassLoader().getResourceAsStream(file))
    .map(AnnotatedConstructorIllegalArg.class);
  }

  @Y
  public static final class AttributeDefaultValue extends Mid {
    @YInject(name="nonExistent")
    String defStr = "defaultValue";
  }

  @Test
  public void testAttributeDefaultValue() throws IOException {
    final AttributeDefaultValue t = new MappingContext()
        .withParser(parser)
        .fromStream(ReflectiveTest.class.getClassLoader().getResourceAsStream(file))
        .map(AttributeDefaultValue.class);
    checkAssertions(t);
    assertEquals("defaultValue", t.defStr);
  }
}
