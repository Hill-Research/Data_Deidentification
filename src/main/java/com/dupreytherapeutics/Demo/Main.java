package com.dupreytherapeutics.Demo;

import com.dupreytherapeutics.Processor.Processor;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Main {

  static Processor processor;
  private static final Logger logger = LogManager.getLogger(Main.class);

  private static void callProcessor(final File file) throws IOException, NoSuchAlgorithmException {
    String fileName = file.getName();
    processor.process(fileName);
  }

  public static void main(String[] args)
      throws IOException, NoSuchAlgorithmException, URISyntaxException {
    logger.info("This is the data de-identification demo.\n");
    URL sampleURL = Main.class.getClassLoader().getResource("data/sample");
    assert sampleURL != null;
    final File folder = new File(sampleURL.toURI());
    logger.info("sample dir: " + folder.getName());
    File[] listOfFiles = folder.listFiles();
    if (listOfFiles == null) {
      logger.error("We could not find any data files in " + folder.getName());
      System.exit(-1);
    }
    processor = new Processor();
    for (File file : listOfFiles) {
      if (!file.isFile()) {
        logger.error("We found abnormal file " + file.getName());
        System.exit(-1);
      }
      logger.info("file: " + file.getName());
      callProcessor(file);
    }
  }
}
