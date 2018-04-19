package com.obsidiandynamics.yconf;

import static org.mockito.Mockito.*;

import java.io.*;

import org.junit.Test;

import com.obsidiandynamics.yconf.MappingContext.*;

import junit.framework.*;

public final class MappingContextTest {
  @Test(expected=NoParserException.class)
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
  
  @Test
  public void testInputStreamClose() throws IOException {
    final InputStream stream = mock(InputStream.class);
    new MappingContext()
    .withParser(__ -> null)
    .fromStream(stream);
    
    verify(stream).close();
  }
  
  @Test
  public void testReaderClose() throws IOException {
    final Reader reader = mock(Reader.class);
    new MappingContext()
    .withParser(__ -> null)
    .fromReader(reader);
    
    verify(reader).close();
  }
}
