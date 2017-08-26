package com.obsidiandynamics.yconf;

import static org.junit.Assert.*;

import java.util.*;

import org.junit.*;

public final class ListMapperTest {
  @Y
  public static final class ListMapperTestClass {}

  @Test
  public void test() {
    final List<?> list = new MappingContext()
        .map(Arrays.asList(Collections.singletonMap("type", ListMapperTestClass.class.getName()),
                           Collections.singletonMap("type", ListMapperTestClass.class.getName()),
                           Collections.singletonMap("type", ListMapperTestClass.class.getName())), List.class);
    assertEquals(3, list.size());
    assertEquals(ListMapperTestClass.class, list.get(0).getClass());
    assertEquals(ListMapperTestClass.class, list.get(1).getClass());
    assertEquals(ListMapperTestClass.class, list.get(2).getClass());
  }
}
