package com.obsidiandynamics.yconf;

import java.io.*;

import com.google.gson.*;

public final class GsonParser implements Parser {
  private final Gson gson;

  public GsonParser() {
    this(new GsonBuilder().disableHtmlEscaping().create());
  }

  public GsonParser(Gson gson) {
    this.gson = gson;
  }

  @Override
  public Object load(Reader reader) throws IOException {
    final Object orig = gson.fromJson(reader, Object.class);
    return FixDoubles.fix(orig);
  }
}