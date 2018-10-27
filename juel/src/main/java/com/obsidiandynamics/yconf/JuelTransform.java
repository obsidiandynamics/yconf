package com.obsidiandynamics.yconf;

import static com.obsidiandynamics.func.Functions.*;

import java.lang.reflect.*;

import javax.el.*;

import com.obsidiandynamics.func.*;

import de.odysseus.el.*;
import de.odysseus.el.util.*;

public final class JuelTransform implements DomTransform {
  private final ExpressionFactory factory = new ExpressionFactoryImpl();
  private final SimpleContext elContext = new SimpleContext();
  private final JuelSession session = new JuelSession();
  
  public JuelTransform() {
    configure(this::registerDefaults);
  }
  
  @FunctionalInterface
  public interface Configurator {
    void apply(JuelTransform transform) throws Throwable;
  }
  
  public void configure(Configurator c) {
    Exceptions.wrap(() -> c.apply(this), RuntimeException::new);
  }
  
  public static String getEnv(String key, String defaultValue) {
    final String existingValue = nullCoerce(System.getenv(key));
    return ifAbsent(existingValue, give(defaultValue));
  }
  
  static String nullCoerce(String str) {
    return str != null && ! str.isEmpty() ? str : null;
  }
  
  private void registerDefaults(JuelTransform transform) throws NoSuchMethodException, SecurityException {
    transform
    .withVariable("env", System.getenv())
    .withVariable("session", session)
    .withFunction("env", JuelTransform.class.getMethod("getEnv", String.class, String.class))
    .withFunction("mandatory", Mandatory.class.getMethod("of", Object.class, String.class))
    .withFunction("secret", Secret.class.getMethod("of", String.class));
  }
  
  public JuelTransform withFunction(String name, Method method) {
    return withFunction("", name,  method);
  }
  
  public JuelTransform withFunction(String namespace, String name, Method method) {
    elContext.setFunction(namespace, name, method);
    return this;
  }
  
  public JuelTransform withVariable(String name, Object val) {
    elContext.setVariable(name, factory.createValueExpression(val, Object.class));
    return this;
  }
  
  @Override
  public Object transform(Object dom, MappingContext context) {
    if (dom instanceof String) {
      final String str = (String) dom;
      if (str.contains("${")) {
        final boolean assigned = session.setLocalContext(context);
        try {
          final ValueExpression expr = factory.createValueExpression(elContext, str, Object.class);
          return expr.getValue(elContext);
        } finally {
          if (assigned) session.clearLocalContext();
        }
      } else {
        return dom;
      }
    } else {
      return dom;
    }
  }
}
