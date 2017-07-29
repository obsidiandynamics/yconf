package com.obsidiandynamics.yconf;

import java.io.*;

@FunctionalInterface
public interface Parser {
  Object load(MappingContext context, Reader reader) throws IOException;
}