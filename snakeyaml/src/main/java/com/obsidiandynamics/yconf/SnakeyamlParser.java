package com.obsidiandynamics.yconf;

import java.io.*;

import org.yaml.snakeyaml.*;

public final class SnakeyamlParser implements Parser {
  private final Yaml yaml;
  
  public SnakeyamlParser() {
    this(new Yaml());
  }
  
  public SnakeyamlParser(Yaml yaml) {
    this.yaml = yaml;
  }

  @Override
  public Object load(Reader reader) throws IOException {
    return yaml.load(reader);
  }
}
