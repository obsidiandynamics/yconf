package com.obsidiandynamics.yconf;

import static org.junit.Assert.*;

import java.io.*;
import java.util.*;

import javax.el.*;

import org.junit.*;

public final class LinkTest {
  private static final class TopLineParser implements Parser {
    @Override
    public Object load(Reader reader) throws IOException {
      final BufferedReader br = new BufferedReader(reader);
      return br.readLine();
    }
  }
  
  private static final class CorruptParser implements Parser {
    @Override
    public Object load(Reader reader) throws IOException {
      throw new IOException("Stream corrupt");
    }
  }
  
  @Test
  public void testValidFile() throws IOException {
    final MappingContext context = new MappingContext()
        .withParser(new TopLineParser())
        .withDomTransform(new JuelTransform());
    
    final YObject y = new YObject(Collections.singletonMap("key", "${session.link('src/test/resources/link-test.txt')}"), context);
    final String out = y.mapAttribute("key", String.class);
    assertEquals("Linked content", out);
  }
  
  @Test(expected=ELException.class)
  public void testCorruptFile() throws IOException {
    final MappingContext context = new MappingContext()
        .withParser(new CorruptParser())
        .withDomTransform(new JuelTransform());
    
    final YObject y = new YObject(Collections.singletonMap("key", "${session.link('src/test/resources/link-test.txt')}"), context);
    final String out = y.mapAttribute("key", String.class);
    assertEquals("Linked content", out);
  }
}
