package com.obsidiandynamics.yconf;

import java.io.*;

public final class JuelSession {
  private final ThreadLocal<MappingContext> localContext = new ThreadLocal<>();
  
  boolean setLocalContext(MappingContext context) {
    final boolean assigned = localContext.get() == null;
    if (assigned) localContext.set(context);
    return assigned;
  }
  
  void clearLocalContext() {
    localContext.remove();
  }
  
  static final class LinkingException extends MappingException {
    private static final long serialVersionUID = 1L;
    LinkingException(String m, Exception cause) { super(m, cause); }
  }
  
  public Object link(String file) {
    try (Reader reader = new FileReader(file)) {
      final YObject y = localContext.get().fromReader(reader);
      return y.value();
    } catch (IOException e) {
      throw new LinkingException("Error reading from " + file, e);
    }
  }
}