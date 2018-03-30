package com.obsidiandynamics.yconf;

import static org.junit.Assert.*;

import java.util.*;

import org.junit.*;

import com.obsidiandynamics.yconf.RuntimeMapper.*;

public final class RuntimeMapperTest {
  @Y(RuntimeTestClass.Mapper.class)
  public static final class RuntimeTestClass {
    public static final class Mapper implements TypeMapper {
      @Override
      public Object map(YObject y, Class<?> type) {
        final RuntimeTestClass r = new RuntimeTestClass();
        y.asMap().forEach((k, v) -> r.map.put(k, v.map(Object.class)));
        return r;
      }
    }
    
    final Map<String, Object> map = new HashMap<>();
  }
  
  @Test
  public void testDefaultTypeAttribute() {
    final Object obj = new MappingContext()
        .map(Collections.singletonMap("type", RuntimeTestClass.class.getName()), 
             Object.class);
    
    assertNotNull(obj);
    assertEquals(RuntimeTestClass.class, obj.getClass());
    final RuntimeTestClass r = (RuntimeTestClass) obj;
    assertTrue(r.map.isEmpty());
  }
  
  @Test
  public void testCustomTypeAttribute() {
    final Object obj = new MappingContext()
        .withRuntimeMapper(new RuntimeMapper().withTypeAttribute("_type"))
        .map(Collections.singletonMap("_type", RuntimeTestClass.class.getName()), 
             Object.class);
    assertNotNull(obj);
    assertEquals(RuntimeTestClass.class, obj.getClass());
    final RuntimeTestClass r = (RuntimeTestClass) obj;
    assertTrue(r.map.isEmpty());
  }
  
  @Test
  public void testMap() {
    final Map<?, ?> dom = new FluentMap<>()
        .with("type", RuntimeTestClass.class.getName())
        .with("a", "A")
        .with("b", "B")
        .with("c", "C");
    
    final Object obj = new MappingContext()
        .map(dom, Object.class);
    
    assertNotNull(obj);
    assertEquals(RuntimeTestClass.class, obj.getClass());
    final RuntimeTestClass r = (RuntimeTestClass) obj;
    assertEquals(3, r.map.size());
  }  
  
  public interface RuntimeMapped {}
  
  public static final class Unmapped implements RuntimeMapped {}
  
  @Test(expected=NoMapperException.class)
  public void testUnmapped() {
    new MappingContext()
    .map(Collections.singletonMap("type", Unmapped.class.getName()), RuntimeMapped.class);
  }
}
