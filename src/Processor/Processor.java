package Processor;

import Algorithm.Blur.BlurNumber;
import Algorithm.Blur.BlurTime;
import Algorithm.Hash;
import Algorithm.Mask.MaskLocation;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

public class Processor {

  private BufferedWriter bw;
  private boolean verbose;

  private String result_directory;
  private String algo_directory;
  private String data_directory;

  public Processor() {
    File resultFold = new File("../" + result_directory);
    if (resultFold.exists()) {
      String[] files = resultFold.list();
      if (files != null) {
        for (String file : files) {
          File currentFile = new File(resultFold.getPath(), file);
          if (!currentFile.delete()) {
            System.out.printf("Fail to delete the history result file %s!\n", currentFile);
            System.exit(-1);
          }
        }
      }

      if (!resultFold.delete()) {
        System.out.print("Fail to delete the result directory!\n");
        System.exit(-1);
      }
    }
    if (!resultFold.mkdirs()) {
      System.out.print("Fail to create the result directory!\n");
      System.exit(-1);
    }
  }

  private void verifyDataFile(final String dataFileName) {
    if (dataFileName.indexOf("tb_") != 0) {
      System.out.printf("data file %s does not have the right prefix!\n", dataFileName);
      System.exit(-1);
    }
    if (!dataFileName.endsWith(".txt")) {
      System.out.printf("data file %s does not have the right suffix!\n", dataFileName);
      System.exit(-1);
    }
  }

  private void verifyAlgoFile(final String dataFileName) {
    if (dataFileName.indexOf("tb_") != 0) {
      System.out.printf("algo file %s does not have the right prefix!\n", dataFileName);
      System.exit(-1);
    }
    if (!dataFileName.endsWith("_algo.txt")) {
      System.out.printf("algo file %s does not have the right suffix!\n", dataFileName);
      System.exit(-1);
    }
  }

  private String extractBaseName(final String dataFileName) {
    int N = dataFileName.length();
    if (N <= 4) {
      System.out.printf("data file %s does not have the right length!\n", dataFileName);
      System.exit(-1);
    }
    return dataFileName.substring(0, dataFileName.length() - 4);
  }

  private void generateOutput(final String fileName) {
    String outputFile = "../" + result_directory + fileName;
    if (verbose) {
      System.out.printf("Start to generate outputFile: %s\n", outputFile);
    }
    File deIdFile = new File(outputFile);
    if (deIdFile.exists()) {
      if (!deIdFile.delete()) {
        System.out.printf("Fail to delete the history result file for %s!\n", fileName);
        System.exit(-1);
      }
    }
    try {
      if (!deIdFile.createNewFile()) {
        System.out.printf("Fail to create the result file for %s!\n", fileName);
        System.exit(-1);
      }
    } catch (Exception e) {
      System.out.printf("Have exception when creating the result file for %s!\n", fileName);
      e.printStackTrace();
      System.exit(-1);
    }
    try {
      FileOutputStream fileOutput = new FileOutputStream(deIdFile);
      bw = new BufferedWriter(new OutputStreamWriter(fileOutput));
    } catch (Exception e) {
      System.out.printf("Have exception when creating BufferWriter for %s!\n", fileName);
      e.printStackTrace();
      System.exit(-1);
    }
  }

  private void print(String element) {
    if (element.isEmpty()) {
      System.out.print("Try to insert null element!\n");
      System.exit(-1);
    }
    try {
      bw.write(element);
      bw.write(" ");
    } catch (IOException e) {
      System.out.printf("Fail to write element %s!\n", element);
      e.printStackTrace();
      System.exit(-1);
    }
  }

  private void readAlgo(final String fileName, Map<String, Integer> algoMap) {
    verifyAlgoFile(fileName);
    if (verbose) {
      System.out.printf("Read algo file %s.\n", fileName);
    }
    File algoFile = new File("../" + algo_directory + fileName);
    try {
      BufferedReader algoReader = new BufferedReader(new FileReader(algoFile));
      String record;
      while ((record = algoReader.readLine()) != null) {
        String[] keywords = record.split("\\s+");
        algoMap.put(keywords[0], Integer.valueOf(keywords[1]));
      }
    } catch (IOException e) {
      System.out.printf("Fail to read algo file %s!\n", fileName);
      e.printStackTrace();
      System.exit(-1);
    }
  }

  private void readData(final String fileName, Map<String, Integer> algoMap) {
    verifyDataFile(fileName);
    if (verbose) {
      System.out.printf("Read data file %s.\n", fileName);
    }
    File dataFile = new File("../" + data_directory + fileName);
    try {
      BufferedReader dataReader = new BufferedReader(new FileReader(dataFile));
      String record = dataReader.readLine();
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
          System.out.printf("%s (%d) and its algo file (%d) have mismatched number of records!\n",
              fileName, numKeywords, algoSize);
          System.out.printf("record: %s\n", record);
          for (int i = 0; i < numKeywords; i++) {
            System.out.printf("%d: %s\n", i + 3, keywords[i]);
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
                String blurTime = BlurTime.blur(keyword, "month");
                print(blurTime);
              } catch (NoSuchAlgorithmException e) {
                System.out.print("No algorithm for BlurTime!\n");
                e.printStackTrace();
                System.exit(-1);
              }
              break;
            case 3:
              try {
                String blurAge = BlurNumber.blur(keyword);
                print(blurAge);
              } catch (NoSuchAlgorithmException e) {
                System.out.print("No algorithm for BlurAge!\n");
                e.printStackTrace();
                System.exit(-1);
              }
              break;
            case 4:
              try {
                String maskAddr = MaskLocation.mask(keyword);
                print(maskAddr);
              } catch (NoSuchAlgorithmException e) {
                System.out.print("No algorithm for MaskLocation!\n");
                e.printStackTrace();
                System.exit(-1);
              }
              break;
            case 5:
              try {
                String hash_keyword = Hash.hash(keyword);
                print(hash_keyword);
              } catch (NoSuchAlgorithmException e) {
                System.out.print("No algorithm for Hash!\n");
                e.printStackTrace();
                System.exit(-1);
              }
              break;
            default:
              System.out.printf("Unknown algo id %d!\n", algo);
              System.exit(-1);
          }
          index++;
        }
        print("\n");
        record = dataReader.readLine();
      }
    } catch (IOException e) {
      System.out.printf("Have exception when reading data file %s!\n", fileName);
      e.printStackTrace();
      System.exit(-1);
    }
  }

  public void process(final String fileName) {
    verifyDataFile(fileName);
    String configFilePath = "Common/config.properties";
    try {
      FileInputStream propsInput = new FileInputStream(configFilePath);
      Properties prop = new Properties();
      prop.load(propsInput);
      verbose = Boolean.parseBoolean(prop.getProperty("verbose"));
      result_directory = prop.getProperty("result_directory");
      algo_directory = prop.getProperty("algo_directory");
      data_directory = prop.getProperty("data_directory");
      if (verbose) {
        System.out.print("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%\n");
        System.out.printf("Start to process data table %s.\n", fileName);
      }
      String baseName = extractBaseName(fileName);
      generateOutput(baseName + "_deid.txt");
      Map<String, Integer> algoMap = new LinkedHashMap<>();
      readAlgo(baseName + "_algo.txt", algoMap);
      readData(baseName + ".txt", algoMap);
      bw.close();
      if (verbose) {
        System.out.printf("Done processing data table %s.\n", fileName);
        System.out.print("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%\n\n\n");
      }
    } catch (Exception e) {
      System.out.printf("Fail to process data table %s!\n", fileName);
      e.printStackTrace();
      System.exit(-1);
    }
  }
}
