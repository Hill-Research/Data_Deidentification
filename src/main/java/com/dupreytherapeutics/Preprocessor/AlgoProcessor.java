package com.dupreytherapeutics.Preprocessor;

import com.dupreytherapeutics.Demo.Main;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.Properties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * This class runs corresponding de-identification algorithms on each data record. For each table,
 * we specify an algorithm file which specifies the deid algo for each record within the table.
 */
public class AlgoProcessor {

  private static final int maxAlgoCodeLen = 20;

  private static final Logger logger = LogManager.getLogger(AlgoProcessor.class);

  /**
   * Verify if the algo record has the valid format, which checks: 1) each algo should consist of
   * two parts 2) the first part is algoCode, which is the code for each data record, and its length
   * should not exceed the predefined threshold 3) the second part is the algorithm id, which ranges
   * from 0 to 5
   *
   * @param records one algo record from the algo file
   */
  private static void verifyAlgo(final String[] records) {
    if (records.length != 2) {
      logger.error("We have invalid algo records!\n");
      logger.error(Arrays.toString(records));
      System.exit(-1);
    }
    String algoCode = records[0];
    if (algoCode.length() > maxAlgoCodeLen) {
      logger.error("Invalid algoCode length!\n");
      logger.error(Arrays.toString(records));
      System.exit(-1);
    }
    int algoVal = Integer.parseInt(records[1]);
    if ((algoVal < 0) || (algoVal > 5)) {
      logger.error("Invalid algo val for ");
      logger.error(Arrays.toString(records));
      System.exit(-1);
    }
  }

  /**
   * read in the algo file, and reformat each record to standard format。
   *
   * @param algoFile file handler to algo file
   */
  private static void normalizeAlgoFile(final File algoFile) {
    try {
      BufferedReader file = new BufferedReader(new FileReader(algoFile));
      StringBuilder algoBuilder = new StringBuilder();

      String line;
      while ((line = file.readLine()) != null) {
        String[] keywords = line.split("\\s+");
        verifyAlgo(keywords);
        algoBuilder.append(String.format("%-" + maxAlgoCodeLen + "s", keywords[0]));
        algoBuilder.append(keywords[1]);
        algoBuilder.append("\n");
      }
      file.close();
      String inputStr = algoBuilder.toString();
      FileOutputStream fileOut = new FileOutputStream(algoFile, false);
      fileOut.write(inputStr.getBytes());
      fileOut.close();
    } catch (Exception e) {
      logger.error("Problem reading algoFile " + algoFile.getName());
      e.printStackTrace();
    }
  }

  public static void main(String[] args) throws IOException, URISyntaxException {
    logger.info("Start to run AlgoProcessor!\n");
    URL algoURL = AlgoProcessor.class.getClassLoader().getResource("data/algo");
    assert algoURL != null;
    final File folder = new File(algoURL.toURI());
    logger.info("Preprocess the algo files in directory: " + folder.getAbsolutePath());
    File[] listOfFiles = folder.listFiles();
    if (listOfFiles == null) {
      logger.error("We could not find any algo files in " + folder.getName());
      System.exit(-1);
    }
    for (File file : listOfFiles) {
      if (!file.isFile()) {
        logger.error("We found abnormal file " + file.getName());
        System.exit(-1);
      }
      if (file.getName().contains("_algo.txt")) {
        logger.info("Run check on algo file " + file.getName());
        normalizeAlgoFile(file);
      }
    }
  }
}
