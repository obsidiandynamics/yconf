package com.obsidiandynamics.yconf;

import java.io.*;
import java.lang.reflect.*;
import java.net.*;
import java.util.*;

import com.obsidiandynamics.yconf.CoercingMapper.*;

public final class MappingContext {
  private final Map<Class<?>, TypeMapper> mappers = new HashMap<>();

  private DomTransform domTransform = (dom, context) -> dom;

  private Parser parser;

  private RuntimeMapper runtimeMapper = new RuntimeMapper();

  public MappingContext() {
    withMappers(defaultMappers());
  }

  private static TypeMapper getCharMapper() {
    return new CoercingMapper(Character.class, s -> {
      if (s.length() != 1) {
        throw new MappingException("Invalid character '" + s + "'", null) {
          private static final long serialVersionUID = 1L;
        };
      }
      return s.charAt(0);
    });
  }

  private Map<Class<?>, TypeMapper> defaultMappers() {
    final Map<Class<?>, TypeMapper> mappers = new HashMap<>();
    mappers.put(boolean.class, new CoercingMapper(Boolean.class, Boolean::parseBoolean));
    mappers.put(Boolean.class, new CoercingMapper(Boolean.class, Boolean::parseBoolean));
    mappers.put(byte.class, new CoercingMapper(Byte.class, stripTrailingDecimalFunc().then(Byte::parseByte)));
    mappers.put(Byte.class, new CoercingMapper(Byte.class, stripTrailingDecimalFunc().then(Byte::parseByte)));
    mappers.put(char.class, getCharMapper());
    mappers.put(Character.class, getCharMapper());
    mappers.put(Class.class, new CoercingMapper(Class.class, Class::forName));
    mappers.put(double.class, new CoercingMapper(Double.class, Double::parseDouble));
    mappers.put(Double.class, new CoercingMapper(Double.class, Double::parseDouble));
    mappers.put(float.class, new CoercingMapper(Float.class, Float::parseFloat));
    mappers.put(Float.class, new CoercingMapper(Float.class, Float::parseFloat));
    mappers.put(int.class, new CoercingMapper(Integer.class, stripTrailingDecimalFunc().then(Integer::parseInt)));
    mappers.put(Integer.class, new CoercingMapper(Integer.class, stripTrailingDecimalFunc().then(Integer::parseInt)));
    mappers.put(List.class, new ListMapper());
    mappers.put(long.class, new CoercingMapper(Long.class, stripTrailingDecimalFunc().then(Long::parseLong)));
    mappers.put(Long.class, new CoercingMapper(Long.class, stripTrailingDecimalFunc().then(Long::parseLong)));
    mappers.put(Map.class, new MapMapper());
    mappers.put(Object.class, (y, type) -> runtimeMapper.map(y, type));
    mappers.put(Properties.class, new PropertiesMapper());
    mappers.put(short.class, new CoercingMapper(Short.class, stripTrailingDecimalFunc().then(Short::parseShort)));
    mappers.put(Short.class, new CoercingMapper(Short.class, stripTrailingDecimalFunc().then(Short::parseShort)));
    mappers.put(String.class, new CoercingMapper(String.class, s -> s));
    mappers.put(URI.class, new CoercingMapper(URI.class, URI::new));
    mappers.put(URL.class, new CoercingMapper(URL.class, URL::new));
    return mappers;
  }

  @FunctionalInterface
  private interface StringProcessor {
    String process(String source);

    default <T> StringConverter<T> then(StringConverter<T> converter) {
      return str -> converter.convert(process(str));
    }
  }

  private static StringProcessor stripTrailingDecimalFunc() {
    return MappingContext::stripTrailingDecimal;
  }

  private static String stripTrailingDecimal(String str) {
    if (str.endsWith(".0")) {
      return str.substring(0, str.length() - 2);
    } else {
      return str;
    }
  }

  private TypeMapper getMapper(Class<?> type, Class<? extends TypeMapper> mapperType) {
    final TypeMapper existing = mappers.get(type);
    if (existing != null) {
      return cast(existing);
    } else {
      final TypeMapper newMapper;
      if (mapperType != null) {
        newMapper = instantiateMapper(type, mapperType);
      } else {
        final Y y = type.getAnnotation(Y.class);
        if (y != null) {
          newMapper = instantiateMapper(type, y.value());
        } else {
          newMapper = mappers.get(Object.class);
        }
      }
      mappers.put(type, newMapper);
      return newMapper;
    }
  }

  private TypeMapper instantiateMapper(Class<?> type, Class<? extends TypeMapper> mapperType) {
    try {
      return mapperType.getDeclaredConstructor().newInstance();
    } catch (Exception e) {
      throw new MapperInstantiationException("Error instantiating mapper " + mapperType.getName() + " for type " +
          type.getName(), e);
    }
  }

  Object transformDom(Object dom) {
    return domTransform.transform(dom, this);
  }

  RuntimeMapper getRuntimeMapper() {
    return runtimeMapper;
  }

  public MappingContext withRuntimeMapper(RuntimeMapper runtimeMapper) {
    this.runtimeMapper = runtimeMapper;
    return this;
  }

  public MappingContext withParser(Parser parser) {
    this.parser = parser;
    return this;
  }

  public MappingContext withDomTransform(DomTransform domTransform) {
    this.domTransform = domTransform;
    return this;
  }

  public MappingContext withMapper(Class<?> type, TypeMapper mapper) {
    mappers.put(type, mapper);
    return this;
  }

  public MappingContext withMappers(Map<Class<?>, TypeMapper> mappers) {
    this.mappers.putAll(mappers);
    return this;
  }

  @SuppressWarnings("unchecked")
  static <T> T cast(Object obj) {
    return (T) obj;
  }

  static final class MapperInstantiationException extends MappingException {
    private static final long serialVersionUID = 1L;
    MapperInstantiationException(String m, Exception cause) { super(m, cause); }
  }

  public <T> T map(Object dom, Class<? extends T> type) {
    return map(dom, type, null);
  }

  public <T> T map(Object dom, Class<? extends T> type, Class<? extends TypeMapper> mapperType) {
    if (dom instanceof YObject) {
      throw new IllegalArgumentException("Cannot map an instance of " + YObject.class.getSimpleName());
    } else if (dom == null) {
      return null;
    } else if (type.isArray()) {
      final Class<?> componentType = type.getComponentType();
      final List<?> items = (List<?>) dom;
      final List<Object> list = new ArrayList<>(items.size());
      for (Object i : items) {
        list.add(new YObject(i, this).map(componentType));
      }
      final Object[] array = MappingContext.cast(Array.newInstance(componentType, list.size()));
      return cast(list.toArray(array));
    } else if (type.isEnum()) {
      return cast(Enum.valueOf(cast(type), String.valueOf(dom)));
    } else {
      final TypeMapper mapper = getMapper(type, mapperType);
      final YObject y = new YObject(dom, this);
      return cast(mapper.map(y, type));
    }
  }

  static final class NoParserException extends MappingException {
    private static final long serialVersionUID = 1L;
    NoParserException(String m) { super(m); }
  }

  private void ensureParser() {
    if (parser == null) throw new NoParserException("Parser not assigned");
  }

  public YObject fromStream(InputStream stream) throws IOException {
    try (InputStream s = stream) {
      ensureParser();
      final Object root = parser.load(new InputStreamReader(stream));
      return new YObject(root, this);
    }
  }

  public YObject fromReader(Reader reader) throws IOException {
    try (Reader r = reader) {
      ensureParser();
      final Object root = parser.load(reader);
      return new YObject(root, this);
    }
  }

  public YObject fromString(String str) throws IOException {
    ensureParser();
    final Object root = parser.load(new StringReader(str));
    return new YObject(root, this);
  }
}