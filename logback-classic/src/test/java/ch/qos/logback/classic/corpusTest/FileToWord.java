package ch.qos.logback.classic.corpusTest;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.List;

import org.junit.Test;

import ch.qos.logback.classic.corpus.TextFileUtil;

public class FileToWord {

  @Test
  public void smoke() throws IOException {
    String s = "When on board H.M.S. 'Beagle,' as naturalist, I was much struck with\r\n"
        + "certain facts in the distribution of the inhabitants of South America,\r\n"
        + "and in the geological relations of the present to the past inhabitants\r\n"
        + "of that continent.";
    
    StringReader sr = new StringReader(s);
    BufferedReader br = new BufferedReader(sr);
    List<String> wordList = TextFileUtil.toWords(br);
    assertEquals(38, wordList.size());
    assertEquals("When", wordList.get(0));
    assertEquals("'Beagle,'", wordList.get(4));
    assertEquals("of", wordList.get(17));
    
  }
}
