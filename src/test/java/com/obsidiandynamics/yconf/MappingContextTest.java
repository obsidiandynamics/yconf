package com.obsidiandynamics.yconf;

import java.io.*;

import org.junit.Test;

import junit.framework.*;

public final class MappingContextTest {
  @Test(expected=MappingException.class)
  public void testNoParser() throws IOException {
    new MappingContext().fromString("").map(Object.class);
  }

  @Test
  public void testFromString() throws IOException {
    final String doc = "some text";
    new MappingContext()
    .withParser(reader -> {
      final BufferedReader br = new BufferedReader(reader);
      final String line = br.readLine();
      TestCase.assertEquals(doc, line);
      return line;
    })
    .fromString(doc)
    .map(Object.class);
  }

  @Test
  public void testFromReader() throws IOException {
    final String doc = "some text";
    new MappingContext()
    .withParser(reader -> {
      final BufferedReader br = new BufferedReader(reader);
      final String line = br.readLine();
      TestCase.assertEquals(doc, line);
      return line;
    })
    .fromReader(new StringReader(doc))
    .map(Object.class);
  }
}
