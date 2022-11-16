package Processor;

import Algorithm.Blur.BlurNumber;
import Algorithm.Blur.BlurTime;
import Algorithm.Hash;
import Algorithm.Mask.MaskLocation;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedHashMap;
import java.util.Map;

public class Processor {

  private BufferedWriter bw;

  public Processor() {
    File resultFold = new File("../data/result");
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

  private void generateOutput(final String fileName) throws IOException {
    File deIdFile = new File("../data/result/" + fileName);
    if (deIdFile.exists()) {
      if (!deIdFile.delete()) {
        System.out.printf("Fail to delete the history result file for %s!\n", fileName);
        System.exit(-1);
      }
    }
    if (!deIdFile.createNewFile()) {
      System.out.printf("Fail to create the result file for %s!\n", fileName);
      System.exit(-1);
    }
    FileOutputStream fileOutput = new FileOutputStream(deIdFile);
    bw = new BufferedWriter(new OutputStreamWriter(fileOutput));
  }

  private void print(String element) throws IOException {
    bw.write(element);
    bw.write(" ");
  }

  private void readAlgo(final String fileName, Map<String, Integer> algoMap) throws IOException {
    File algoFile = new File("../data/algo/" + fileName);
    BufferedReader algoReader = new BufferedReader(new FileReader(algoFile));
    String record;
    while ((record = algoReader.readLine()) != null) {
      String[] keywords = record.split("\\s+");
      algoMap.put(keywords[0], Integer.valueOf(keywords[1]));
    }
  }

  private void readData(final String fileName, Map<String, Integer> algoMap)
      throws IOException, NoSuchAlgorithmException {
    File dataFile = new File("../data/sample/" + fileName);
    BufferedReader dataReader = new BufferedReader(new FileReader(dataFile));

    MaskLocation.setLocationsName();
    String record = dataReader.readLine();
    while ((record != null) && !record.isEmpty()) {
      String[] keywords = record.split("\\s+");
      int numKeywords = keywords.length;
      int algoSize = algoMap.size();
      if (numKeywords != algoSize) {
        System.out.printf("%s (%d) and its algo file (%d) have mismatched number of records!\n",
            fileName, numKeywords, algoSize);
        System.out.printf("record: %s\n", record);
        for (int i = 0; i < numKeywords; i++) {
          System.out.printf("%d: %s\n", i+3, keywords[i]);
        }
        return;
//        System.exit(-1);
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
            String blurTime = BlurTime.blur(keyword, "month");
            print(blurTime);
            break;
          case 3:
            String blurAge = BlurNumber.blur(keyword);
            print(blurAge);
            break;
          case 4:
            String maskAddr = MaskLocation.mask(keyword);
            print(maskAddr);
            break;
          case 5:
            String hash_keyword = Hash.hash(keyword);
            print(hash_keyword);
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
  }

  public void process(final String fileName) throws IOException, NoSuchAlgorithmException {
    String baseName = fileName.substring(0, fileName.length() - 4);
    System.out.printf("fileName: %s, base: %s\n", fileName, baseName);
    generateOutput(baseName + "_deid.txt");
    Map<String, Integer> algoMap = new LinkedHashMap<>();
    readAlgo(baseName + "_algo.txt", algoMap);
    readData(baseName + ".txt", algoMap);
    bw.close();
  }
}
