package com.dupreytherapeutics.Preprocessor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.util.Arrays;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AlgoProcessor {

  private static final int maxAlgoCodeLen = 20;

  private static final Logger logger = LogManager.getLogger(AlgoProcessor.class);

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

  private static void checkAlgoFile(final File algoFile) {
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
      FileOutputStream fileOut = new FileOutputStream(algoFile);
      fileOut.write(inputStr.getBytes());
      fileOut.close();
    } catch (Exception e) {
      logger.error("Problem reading algoFile %s!\n", algoFile.getName());
      e.printStackTrace();
    }
  }

  public static void main(String[] args) {
    final File folder = new File("../data/algo");
    logger.info("Preprocess the algo files in directory: %s\n", folder.getAbsolutePath());
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
        checkAlgoFile(file);
      }
    }
  }
}
