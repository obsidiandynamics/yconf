package com.obsidiandynamics.yconf;

import java.io.*;
import java.lang.ref.*;

public final class JuelSession {
  final ThreadLocal<WeakReference<MappingContext>> localContext = new ThreadLocal<>();
  
  boolean setLocalContext(MappingContext context) {
    final boolean assigned = localContext.get() == null;
    if (assigned) localContext.set(new WeakReference<>(context));
    return assigned;
  }
  
  void clearLocalContext() {
    localContext.remove();
  }
  
  public Object link(String file) {
    try (Reader reader = new FileReader(file)) {
      final YObject y = localContext.get().get().fromReader(reader);
      return y.value();
    } catch (IOException e) {
      throw new MappingException("Error reading from " + file, e);
    }
  }
}