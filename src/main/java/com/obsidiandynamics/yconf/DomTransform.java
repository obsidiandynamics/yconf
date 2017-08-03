package com.obsidiandynamics.yconf;

@FunctionalInterface
public interface DomTransform {
  Object transform(Object dom, MappingContext context);
}
