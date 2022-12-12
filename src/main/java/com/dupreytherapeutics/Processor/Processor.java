package com.dupreytherapeutics.Processor;

import com.dupreytherapeutics.Algorithm.Blur.BlurNumber;
import com.dupreytherapeutics.Algorithm.Blur.BlurTime;
import com.dupreytherapeutics.Algorithm.Hash;
import com.dupreytherapeutics.Algorithm.Mask.MaskLocation;
import com.dupreytherapeutics.Demo.Main;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Processor {

  private BufferedWriter bw;

  private final String result_directory;
  private String algo_directory;
  private String data_directory;

  private final Properties prop;

  private static final Logger logger = LogManager.getLogger(Processor.class);

  public Processor() throws IOException {
    URL configURL = Main.class.getClassLoader().getResource("config.properties");
    assert configURL != null;
    String configFilePath = configURL.getPath();
    FileInputStream propsInput = new FileInputStream(configFilePath);
    prop = new Properties();
    prop.load(propsInput);
    String classpath = Processor.class.getProtectionDomain().getCodeSource().getLocation()
        .getPath();
    result_directory = classpath + prop.getProperty("result_directory");
    File resultFold = new File(result_directory);
    logger.info("result dir " + resultFold.getAbsolutePath());
    if (resultFold.exists()) {
      String[] files = resultFold.list();
      if (files != null) {
        for (String file : files) {
          File currentFile = new File(resultFold.getPath(), file);
          if (!currentFile.delete()) {
            logger.error("Fail to delete the history result file " + currentFile);
            System.exit(-1);
          }
        }
      }

      if (!resultFold.delete()) {
        logger.error("Fail to delete the result directory!\n");
        System.exit(-1);
      }
    }
    if (!resultFold.mkdirs()) {
      logger.error("Fail to create the result directory!\n");
      System.exit(-1);
    }
  }

  private void verifyDataFile(final String dataFileName) {
    if (dataFileName.indexOf("tb_") != 0) {
      logger.error("data file " + dataFileName + " does not have the right prefix!");
      System.exit(-1);
    }
    if (!dataFileName.endsWith(".txt")) {
      logger.error("data file " + dataFileName + " does not have the right suffix!");
      System.exit(-1);
    }
  }

  private void verifyAlgoFile(final String dataFileName) {
    if (dataFileName.indexOf("tb_") != 0) {
      logger.error("algo file " + dataFileName + " does not have the right prefix!");
      System.exit(-1);
    }
    if (!dataFileName.endsWith("_algo.txt")) {
      logger.error("algo file " + dataFileName + " does not have the right suffix!");
      System.exit(-1);
    }
  }

  private String extractBaseName(final String dataFileName) {
    int N = dataFileName.length();
    if (N <= 4) {
      logger.error("data file " + dataFileName + " does not have the right length!");
      System.exit(-1);
    }
    return dataFileName.substring(0, dataFileName.length() - 4);
  }

  private void generateOutput(final String fileName) {
    String outputFile = result_directory + fileName;
      logger.info("Start to generate outputFile: " + outputFile);
    File deIdFile = new File(outputFile);
    logger.info("deid path: " + deIdFile.getAbsolutePath());
    if (deIdFile.exists()) {
      if (!deIdFile.delete()) {
        logger.error("Fail to delete the history result file for " + fileName);
        System.exit(-1);
      }
    }
    try {
      if (!deIdFile.createNewFile()) {
        logger.error("Fail to create the result file for " + fileName);
        System.exit(-1);
      }
    } catch (Exception e) {
      logger.error("Have exception when creating the result file for " + fileName);
      e.printStackTrace();
      System.exit(-1);
    }
    try {
      FileOutputStream fileOutput = new FileOutputStream(deIdFile);
      bw = new BufferedWriter(new OutputStreamWriter(fileOutput));
    } catch (Exception e) {
      logger.error("Have exception when creating BufferWriter for " + fileName);
      e.printStackTrace();
      System.exit(-1);
    }
  }

  private void print(String element) {
    if (element.isEmpty()) {
      logger.error("Try to insert null element!\n");
      System.exit(-1);
    }
    try {
      bw.write(element);
      bw.write(" ");
    } catch (IOException e) {
      logger.error("Fail to write element " + element);
      e.printStackTrace();
      System.exit(-1);
    }
  }

  private void readAlgo(final String fileName, Map<String, Integer> algoMap)
      throws URISyntaxException {
    verifyAlgoFile(fileName);
      logger.info("Read algo file " + fileName);
    URL algoURL = Main.class.getClassLoader().getResource(algo_directory + fileName);
    assert algoURL != null;
    File algoFile = new File(algoURL.toURI());
    try {
      BufferedReader algoReader = new BufferedReader(new FileReader(algoFile));
      String record;
      while ((record = algoReader.readLine()) != null) {
        String[] keywords = record.split("\\s+");
        algoMap.put(keywords[0], Integer.valueOf(keywords[1]));
      }
    } catch (IOException e) {
      logger.error("Fail to read algo file " + fileName);
      e.printStackTrace();
      System.exit(-1);
    }
  }

  private void readData(final String fileName, Map<String, Integer> algoMap)
      throws URISyntaxException {
    verifyDataFile(fileName);
      logger.info("Read data file " + fileName);
    URL dataURL = Main.class.getClassLoader().getResource(data_directory + fileName);
    assert dataURL != null;
    File dataFile = new File(dataURL.toURI());
    try {
      BufferedReader dataReader = new BufferedReader(new FileReader(dataFile));
      String record = dataReader.readLine();
      BlurTime blurTimeEngine = new BlurTime();
      BlurNumber blurNumberEngine = new BlurNumber();
      while ((record != null)) {
        if (record.isEmpty()) {
          record = dataReader.readLine();
          continue;
        }
        String[] keywords = record.split("\\s+");
        int numKeywords = keywords.length;
        if (numKeywords == 0) {
          record = dataReader.readLine();
          continue;
        }
        int algoSize = algoMap.size();
        if (numKeywords != algoSize) {
          logger.error(fileName + " (" + numKeywords + ") and its algo file (" + algoSize
                  + ") have mismatched number of records!");
          logger.error("record: " + record);
          for (int i = 0; i < numKeywords; i++) {
            logger.error((i + 3) + ": " + keywords[i]);
          }
          System.exit(-1);
        }
        int index = 0;
        for (Map.Entry<String, Integer> algoMapIt : algoMap.entrySet()) {
          //TODO: this code might be useful when we access the database
          String code = algoMapIt.getKey();
          int algo = algoMapIt.getValue();
          String keyword = keywords[index];
          switch (algo) {
            case 0:
              print(keyword);
              break;
            case 1:
              break;
            case 2:
              try {
                String blurTime = blurTimeEngine.blur(keyword, "month");
                print(blurTime);
              } catch (NoSuchAlgorithmException e) {
                logger.error("No algorithm for BlurTime!\n");
                e.printStackTrace();
                System.exit(-1);
              }
              break;
            case 3:
              try {
                String blurAge = blurNumberEngine.blur(keyword);
                print(blurAge);
              } catch (NoSuchAlgorithmException e) {
                logger.error("No algorithm for BlurAge!\n");
                e.printStackTrace();
                System.exit(-1);
              }
              break;
            case 4:
              try {
                String maskAddr = MaskLocation.mask(keyword);
                print(maskAddr);
              } catch (NoSuchAlgorithmException e) {
                logger.error("No algorithm for MaskLocation!\n");
                e.printStackTrace();
                System.exit(-1);
              }
              break;
            case 5:
              try {
                String hash_keyword = Hash.hash(keyword);
                print(hash_keyword);
              } catch (NoSuchAlgorithmException e) {
                logger.error("No algorithm for Hash!\n");
                e.printStackTrace();
                System.exit(-1);
              }
              break;
            default:
              logger.error("Unknown algo id " + algo);
              System.exit(-1);
          }
          index++;
        }
        print("\n");
        record = dataReader.readLine();
      }
    } catch (IOException e) {
      logger.error("Have exception when reading data file " + fileName);
      e.printStackTrace();
      System.exit(-1);
    }
  }

  public void process(final String fileName) {
                    verifyDataFile(fileName);
    try {
      algo_directory = prop.getProperty("algo_directory");
      data_directory = prop.getProperty("data_directory");
        logger.info("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%\n");
        logger.info("Start to process data table " + fileName);
      String baseName = extractBaseName(fileName);
      logger.info("baseName: " + baseName);
      generateOutput(baseName + "_deid.txt");
      Map<String, Integer> algoMap = new LinkedHashMap<>();
      readAlgo(baseName + "_algo.txt", algoMap);
      readData(baseName + ".txt", algoMap);
      bw.close();
        logger.info("Done processing data table " + fileName);
        logger.info("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%\n\n\n");

    } catch (Exception e) {
      logger.error("Fail to process data table " + fileName);
      e.printStackTrace();
      System.exit(-1);
    }
  }
}
