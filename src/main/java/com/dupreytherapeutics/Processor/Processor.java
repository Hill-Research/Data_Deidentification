package com.dupreytherapeutics.Processor;

import com.dupreytherapeutics.Algorithm.Blur.BlurNumber;
import com.dupreytherapeutics.Algorithm.Blur.BlurTime;
import com.dupreytherapeutics.Algorithm.Hash.Hash;
import com.dupreytherapeutics.Algorithm.Mask.MaskLocation;
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
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/** This class does de-identification processing for each data file */
public class Processor {

  private static final Logger logger = LogManager.getLogger(Processor.class);
  private final String result_directory;
  private final Properties prop;
  private BufferedWriter bw;
  private String algo_directory;
  private String data_directory;

  DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

  /**
   * Constructor. It reads in the config file, and extracts the result directory location which will
   * be used to store the deid table files. If the result directory already exists, it will
   *
   * <pre>remove the existing directory and create a new one</pre>
   *
   * .
   *
   * @throws IOException when it fails to manipulate with the result directory
   */
  public Processor() throws IOException {
    try {
      dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
    } catch (ParserConfigurationException e) {
      logger.error("Parser configuratio error!\n");
      e.printStackTrace();
      System.exit(-1);
    }
    URL configURL = Processor.class.getClassLoader().getResource("config.properties");
    assert configURL != null;
    String configFilePath = configURL.getPath();
    FileInputStream propsInput = new FileInputStream(configFilePath);
    prop = new Properties();
    prop.load(propsInput);
    String classpath =
        Processor.class.getProtectionDomain().getCodeSource().getLocation().getPath();
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

  /**
   * Check if the data file is valid, which includes: 1) the data file name should start with "tb_"
   * 2) the data file name should end with ".txt"
   *
   * @param dataFileName input data file name
   */
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

  /**
   * Check if the algo file is valid, which includes: 1) the algo file name should start with "tb_"
   * 2) the algo file name should end with "_algo.xml"
   *
   * @param algoFileName input algo file name
   */
  private void verifyAlgoFile(final String algoFileName) {
    if (algoFileName.indexOf("tb_") != 0) {
      logger.error("algo file " + algoFileName + " does not have the right prefix!");
      System.exit(-1);
    }
    if (!algoFileName.endsWith("_algo.xml")) {
      logger.error("algo file " + algoFileName + " does not have the right suffix!");
      System.exit(-1);
    }
  }

  /**
   * Extract the base name (i.e., the table name) from input data file name. For example, if the
   * input file name is "tb_patient_info.txt", then the base name is "tb_patient_info"
   *
   * @param dataFileName input data file name
   * @return the base name
   */
  private String extractBaseName(final String dataFileName) {
    int N = dataFileName.length();
    if (N <= 4) {
      logger.error("data file " + dataFileName + " does not have the right length!");
      System.exit(-1);
    }
    return dataFileName.substring(0, dataFileName.length() - 4);
  }

  /**
   * generate the de-identification file for the input data file
   *
   * @param dataFileName input data file name
   */
  private void genDeIdOutputFile(final String dataFileName) {
    String outputFile = result_directory + dataFileName;
    logger.info("Start to generate outputFile: " + outputFile);
    File deIdFile = new File(outputFile);
    logger.info("deid path: " + deIdFile.getAbsolutePath());
    if (deIdFile.exists()) {
      if (!deIdFile.delete()) {
        logger.error("Fail to delete the history result file for " + dataFileName);
        System.exit(-1);
      }
    }
    try {
      if (!deIdFile.createNewFile()) {
        logger.error("Fail to create the result file for " + dataFileName);
        System.exit(-1);
      }
    } catch (Exception e) {
      logger.error("Have exception when creating the result file for " + dataFileName);
      e.printStackTrace();
      System.exit(-1);
    }
    try {
      FileOutputStream fileOutput = new FileOutputStream(deIdFile);
      bw = new BufferedWriter(new OutputStreamWriter(fileOutput));
    } catch (Exception e) {
      logger.error("Have exception when creating BufferWriter for " + dataFileName);
      e.printStackTrace();
      System.exit(-1);
    }
  }

  /**
   * Write one de-identified element to the de-identification file
   *
   * @param element the de-identified element to be written
   */
  private void writeToDeIdFile(String element) {
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

  /**
   * Read in the algo file, and store the de-identification algorithm for each element
   *
   * @param algoFileName input algo file name
   * @param algoMap the map which stores the de-identification algorithm for each element
   * @throws URISyntaxException if error happens for getting the URI of the algo file
   */
  private void readAlgoFile(final String algoFileName, Map<String, Integer> algoMap)
      throws URISyntaxException {
    verifyAlgoFile(algoFileName);
    logger.info("Read algo file " + algoFileName);
    URL algoURL = Processor.class.getClassLoader().getResource(algo_directory + algoFileName);
    assert algoURL != null;
    File algoFile = new File(algoURL.toURI());
    try {
      System.out.println(algoFile.getName());
      DocumentBuilder db = dbf.newDocumentBuilder();
      Document doc = db.parse(algoFile);
      doc.getDocumentElement().normalize();
      System.out.println("Root element: " + doc.getDocumentElement().getNodeName());

      NodeList nodeList = doc.getElementsByTagName("item");
      System.out.printf("len: %d\n", nodeList.getLength());
      for (int itr = 0; itr < nodeList.getLength(); itr++) {
        Node node = nodeList.item(itr);
        if (node.getNodeType() == Node.ELEMENT_NODE) {
          Element eElement = (Element) node;
          String code = eElement.getElementsByTagName("Code").item(0).getTextContent();
          int algoVal =
              Integer.parseInt(
                  eElement.getElementsByTagName("AlgoNumber").item(0).getTextContent());
          algoMap.put(code, algoVal);
        } else {
          logger.error("Unknown record in " + algoFileName + ": " + node.getBaseURI());
          System.exit(-1);
        }
      }
    } catch (Exception e) {
      logger.error("Fail to read algo file " + algoFileName);
      e.printStackTrace();
      System.exit(-1);
    }
  }

  /**
   * Run de-identification operations on each record from input data file
   *
   * @param dataFileName input data file name
   * @param algoMap the map which stores the de-identification algorithm for each record in the data
   *     file
   * @throws URISyntaxException if error happens for getting the URI of the data file
   */
  private void deIdentifyDataFile(final String dataFileName, Map<String, Integer> algoMap)
      throws URISyntaxException {
    verifyDataFile(dataFileName);
    logger.info("Read data file " + dataFileName);
    URL dataURL = Processor.class.getClassLoader().getResource(data_directory + dataFileName);
    assert dataURL != null;
    File dataFile = new File(dataURL.toURI());
    try {
      BufferedReader dataReader = new BufferedReader(new FileReader(dataFile));
      String record = dataReader.readLine();
      BlurTime blurTimeEngine = new BlurTime();
      BlurNumber blurNumberEngine = new BlurNumber();
      MaskLocation maskLocationEngine = new MaskLocation();
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
          logger.error(
              dataFileName
                  + " ("
                  + numKeywords
                  + ") and its algo file ("
                  + algoSize
                  + ") have mismatched number of records!");
          logger.error("record: " + record);
          for (int i = 0; i < numKeywords; i++) {
            logger.error((i + 3) + ": " + keywords[i]);
          }
          System.exit(-1);
        }
        int index = 0;
        for (Map.Entry<String, Integer> algoMapIt : algoMap.entrySet()) {
          String code = algoMapIt.getKey(); // this code might be useful when we access the database
          int algo = algoMapIt.getValue();
          String keyword = keywords[index];
          switch (algo) {
            case 0:
              writeToDeIdFile(keyword);
              break;
            case 1:
              break;
            case 2:
              try {
                String blurTime = blurTimeEngine.blur(keyword, "month");
                writeToDeIdFile(blurTime);
              } catch (NoSuchAlgorithmException e) {
                logger.error("No algorithm for BlurTime!\n");
                e.printStackTrace();
                System.exit(-1);
              }
              break;
            case 3:
              try {
                String blurAge = blurNumberEngine.blur(keyword);
                writeToDeIdFile(blurAge);
              } catch (NoSuchAlgorithmException e) {
                logger.error("No algorithm for BlurAge!\n");
                e.printStackTrace();
                System.exit(-1);
              }
              break;
            case 4:
              try {
                String maskAddr = maskLocationEngine.mask(keyword);
                writeToDeIdFile(maskAddr);
              } catch (NoSuchAlgorithmException e) {
                logger.error("No algorithm for MaskLocation!\n");
                e.printStackTrace();
                System.exit(-1);
              }
              break;
            case 5:
              try {
                String hash_keyword = Hash.hash(keyword);
                writeToDeIdFile(hash_keyword);
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
        writeToDeIdFile("\n");
        record = dataReader.readLine();
      }
    } catch (IOException e) {
      logger.error("Have exception when reading data file " + dataFileName);
      e.printStackTrace();
      System.exit(-1);
    }
  }

  /**
   * Process the input data file, including: 1) read in its corresponding de-identification algo
   * file 2) generate the de-identification file for the input data file 3) apply the
   * de-identification algorithm on each record in the data file, and write the result to the de-id
   * file
   *
   * @param dataFileName input data file name
   */
  public void process(final String dataFileName) {
    verifyDataFile(dataFileName);
    try {
      algo_directory = prop.getProperty("algo_directory");
      data_directory = prop.getProperty("data_directory");
      logger.info("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%\n");
      logger.info("Start to process data table " + dataFileName);
      String baseName = extractBaseName(dataFileName);
      logger.info("baseName: " + baseName);
      genDeIdOutputFile(baseName + "_deid.txt");
      Map<String, Integer> algoMap = new LinkedHashMap<>();
      readAlgoFile(baseName + "_algo.xml", algoMap);
      deIdentifyDataFile(baseName + ".txt", algoMap);
      bw.close();
      logger.info("Done processing data table " + dataFileName);
      logger.info("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%\n\n\n");

    } catch (Exception e) {
      logger.error("Fail to process data table " + dataFileName);
      e.printStackTrace();
      System.exit(-1);
    }
  }
}
