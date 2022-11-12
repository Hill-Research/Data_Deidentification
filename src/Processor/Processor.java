package Processor;

import Algorithm.Blur;
import Algorithm.Hash;
import Algorithm.Mask;
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
  private final Map<String, Integer> algoMap = new LinkedHashMap<>();

  private void generateOutput(final String fileName) throws IOException {
    File resultFold = new File("../data/result/");
    if (resultFold.exists()) {
      if (!resultFold.delete()) {
        System.out.printf("Fail to delete the result directory for %s!\n", fileName);
        System.exit(-1);
      }
    }
    if (!resultFold.mkdirs()) {
      System.out.printf("Fail to create the result directory for %s!\n", fileName);
      System.exit(-1);
    }

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

  private void readAlgo(final String fileName) throws IOException {
    File algoFile = new File("../data/algo/" + fileName);
    BufferedReader algoReader = new BufferedReader(new FileReader(algoFile));
    String record;
    while ((record = algoReader.readLine()) != null) {
      String[] keywords = record.split("\\s+");
      algoMap.put(keywords[0], Integer.valueOf(keywords[1]));
    }
  }

  private void readData(final String fileName) throws IOException, NoSuchAlgorithmException {
    File dataFile = new File("../data/sample/" + fileName);
    BufferedReader dataReader = new BufferedReader(new FileReader(dataFile));
    String record;
    while ((record = dataReader.readLine()) != null) {
      String[] keywords = record.split("\\s+");
      int numKeywords = keywords.length;
      if (numKeywords != algoMap.size()) {
        System.out.print("Data file and algo file have mismatched number of records!\n");
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
            String blurTime = Blur.BlurTime.blur(keyword, "month");
            print(blurTime);
            break;
          case 3:
            String blurAge = Blur.BlurAge.run(keyword);
            print(blurAge);
            break;
          case 4:
            String maskAddr = Mask.MaskAddr.run(keyword);
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
    }
  }

  public void process(final String fileName) throws IOException, NoSuchAlgorithmException {
    generateOutput(fileName + "_deid.txt");
    readAlgo(fileName + "_algo.txt");
    readData(fileName + ".txt");
    bw.close();
  }
}
