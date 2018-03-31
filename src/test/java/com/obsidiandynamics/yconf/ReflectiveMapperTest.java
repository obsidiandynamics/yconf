package com.obsidiandynamics.yconf;

import static org.junit.Assert.*;

import java.util.*;

import org.junit.*;

import com.obsidiandynamics.yconf.util.*;

public final class ReflectiveMapperTest {
  @Y
  public static class FieldTest {
    @YInject
    private int numberDefaultName;

    @YInject(name="specialNumber")
    private int numberCustomName;

    @YInject
    private int unset;

    private int outOfScope;
  }

  @Test
  public void testFieldSuccess() {
    final Map<?, ?> dom = new FluentMap<>()
        .with("numberDefaultName", 100)
        .with("specialNumber", 200)
        .with("extraneous", 300);

    final FieldTest o = new MappingContext()
        .map(dom, FieldTest.class);

    assertEquals(100, o.numberDefaultName);
    assertEquals(200, o.numberCustomName);
    assertEquals(0, o.unset);
    assertEquals(0, o.outOfScope);
  }

  @Y
  public static class ConstructorTest {
    private final int alpha;
    private final int bravo;

    public ConstructorTest(@YInject(name="alpha") int alpha, 
                           @YInject(name="bravo") int bravo) {
      this.alpha = alpha;
      this.bravo = bravo;
    }
  }

  @Test
  public void testConstructorSuccess() {
    final Map<?, ?> dom = new FluentMap<>()
        .with("alpha", 100)
        .with("bravo", 200)
        .with("extraneous", 300);

    final ConstructorTest o = new MappingContext()
        .map(dom, ConstructorTest.class);

    assertEquals(100, o.alpha);
    assertEquals(200, o.bravo);
  }

  private static class SealedType {
    private int value;
  }

  public static class SealedTypeMapper implements TypeMapper {
    @Override
    public Object map(YObject y, Class<?> type) {
      final SealedType obj = new SealedType();
      obj.value = y.getAttribute("value").map(int.class);
      return obj;
    }
  }

  @Y
  public static final class FieldWithInlineMapper {
    @YInject(mapper=SealedTypeMapper.class)
    private SealedType sealed;
  }

  @Test
  public void testSealedField() {
    final Map<?, ?> dom = new FluentMap<>()
        .with("sealed", new FluentMap<>().with("value", 100).with("extraneous", 300));

    final FieldWithInlineMapper o = new MappingContext()
        .map(dom, FieldWithInlineMapper.class);

    assertNotNull(o.sealed);
    assertEquals(100, o.sealed.value);
  }

  @Y
  public static final class ConstructorWithInlineMapper {
    private final SealedType sealed;

    public ConstructorWithInlineMapper(@YInject(name="sealed", mapper=SealedTypeMapper.class) SealedType sealed) {
      this.sealed = sealed;
    }
  }

  @Test
  public void testSealedConstructor() {
    final Map<?, ?> dom = new FluentMap<>()
        .with("sealed", new FluentMap<>().with("value", 100).with("extraneous", 300));

    final ConstructorWithInlineMapper o = new MappingContext()
        .map(dom, ConstructorWithInlineMapper.class);

    assertNotNull(o.sealed);
    assertEquals(100, o.sealed.value);
  }
}
