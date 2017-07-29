package com.obsidiandynamics.yconf;

import java.io.*;

@FunctionalInterface
public interface Parser {
  Object load(Reader reader) throws IOException;
}