package com.obsidiandynamics.yconf.util;

import java.util.*;

import com.obsidiandynamics.yconf.*;

@Y(PropsBuilder.Mapper.class)
public final class PropsBuilder {
  public static final class Mapper implements TypeMapper {
    @Override public Object map(YObject y, Class<?> type) {
      final PropsBuilder builder = new PropsBuilder();
      for (Map.Entry<String, YObject> entry : y.asMap().entrySet()) {
        builder.with(entry.getKey(), entry.getValue().map(Object.class));
      }
      return builder;
    }
  }
  
  private final Properties properties = new Properties();
  
  public PropsBuilder with(String key, Object value) {
    if (value != null) properties.put(key, value.toString());
    return this;
  }
  
  /**
   *  Assigns the property, sourcing initially from the system properties, and falling back
   *  to {@code defaultValue} if the entry wasn't found in {@code System#getProperties()}.
   *  
   *  @param key The key.
   *  @param defaultValue The default value, if the entry isn't in {@code System#getProperties()}.
   *  @return This builder, for fluent chaining.
   */
  public PropsBuilder withSystemDefault(String key, Object defaultValue) {
    return withDefault(key, System.getProperties(), defaultValue);
  }
  
  /**
   *  Assigns the property, sourcing initially from a set of default properties, and falling back
   *  to {@code defaultValue} if the entry wasn't found in {@code defaultProperties}.
   *  
   *  @param key The key.
   *  @param defaultProperties The defaults to source from.
   *  @param defaultValue The default value, if the entry isn't in {@code defaultProperties}.
   *  @return This builder, for fluent chaining.
   */
  public PropsBuilder withDefault(String key, Properties defaultProperties, Object defaultValue) {
    return with(key, defaultProperties.getProperty(key, defaultValue != null ? defaultValue.toString() : null));
  }
  
  public Properties build() {
    final Properties copy = new Properties();
    copy.putAll(properties);
    return copy;
  }
  
  @Override
  public String toString() {
    return properties.toString();
  }
}