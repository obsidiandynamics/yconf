package com.obsidiandynamics.yconf;

import java.lang.reflect.*;

import javax.el.*;

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
    void apply(JuelTransform transform) throws Exception;
  }
  
  public void configure(Configurator c) {
    try {
      c.apply(this);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
  
  private void registerDefaults(JuelTransform transform) throws NoSuchMethodException, SecurityException {
    transform
    .withVariable("env", System.getenv())
    .withVariable("session", session)
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
  }
}
