package com.obsidiandynamics.yconf;

import java.lang.reflect.*;
import java.util.*;

/**
 *  A {@link TypeMapper} implementation that will attempt to reflectively instantiate a given
 *  class, in addition to setting fields directly post construction.
 */
public final class ReflectiveMapper implements TypeMapper {
  static final class ObjectInstantiationException extends MappingException {
    private static final long serialVersionUID = 1L;
    ObjectInstantiationException(String m, Throwable cause) { super(m, cause); }
  }
  
  static final class NoSuitableConstructorException extends MappingException {
    private static final long serialVersionUID = 1L;
    NoSuitableConstructorException(String m, Throwable cause) { super(m, cause); }
  }

  static final class NoNameSpecifiedException extends MappingException {
    private static final long serialVersionUID = 1L;
    NoNameSpecifiedException(String m) { super(m); }
  }
  
  @Override
  public Object map(YObject y, Class<?> type) {
    if (y.is(type)) {
      return y.value();
    }
    
    final Constructor<?> constr;
    try {
      constr = getConstructor(type);
    } catch (NoSuchMethodException | SecurityException e) {
      throw new NoSuitableConstructorException("Class " + type.getName() + " does not have a suitable constructor", e);
    }
    
    final Object[] args = new Object[constr.getParameterCount()];
    final Parameter[] params = constr.getParameters();
    for (int i = 0; i < params.length; i++) {
      final YInject inj = params[i].getAnnotation(YInject.class);
      final Class<?> t = inj.type() != Void.class ? inj.type() : params[i].getType();
      if (inj.name().isEmpty()) {
        throw new NoNameSpecifiedException("No name specified for attribute of type " + t.getName());
      }
      final Class<? extends TypeMapper> mapperType = inj.mapper() != NullMapper.class ? inj.mapper() : null;
      args[i] = y.getAttribute(inj.name()).map(t, mapperType);
    }

    final Object target;
    try {
      target = constr.newInstance(args);
    } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
      throw new ObjectInstantiationException("Error instantiating " + type.getName(), e);
    }
    
    return y.mapReflectively(target);
  }
  
  private static Constructor<?> getConstructor(Class<?> type) throws NoSuchMethodException, SecurityException {
    for (Constructor<?> c : type.getDeclaredConstructors()) {
      final long count = Arrays.stream(c.getParameters()).filter(p -> p.isAnnotationPresent(YInject.class)).count();
      if (count > 0 && count == c.getParameterCount()) {
        c.setAccessible(true);
        return c;
      }
    }
    
    return type.getConstructor();
  }
}
