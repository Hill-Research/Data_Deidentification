package com.dupreytherapeutics.Demo;

import com.dupreytherapeutics.Processor.Processor;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.NoSuchAlgorithmException;

public class Main {

  static Processor processor;

  private static void callProcessor(final File file) throws IOException, NoSuchAlgorithmException {
    String fileName = file.getName();
    processor.process(fileName);
  }

  public static void main(String[] args)
      throws IOException, NoSuchAlgorithmException, URISyntaxException {
    System.out.println("This is the data de-identification demo.\n");
    URL sampleURL = Main.class.getClassLoader().getResource("data/sample");
    assert sampleURL != null;
    final File folder = new File(sampleURL.toURI());
    System.out.printf("sample dir: %s\n", folder.getName());
    File[] listOfFiles = folder.listFiles();
    if (listOfFiles == null) {
      System.out.format("We could not find any data files in %s!", folder.getName());
      System.exit(-1);
    }
    processor = new Processor();
    for (File file : listOfFiles) {
      if (!file.isFile()) {
        System.out.format("We found abnormal file %s!", file.getName());
        System.exit(-1);
      }
      System.out.printf("file: %s\n", file.getName());
      callProcessor(file);
    }
  }
}
