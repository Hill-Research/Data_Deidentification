package com.dupreytherapeutics.Algorithm.Mask;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
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
import org.xml.sax.SAXException;

/** This class implements the interface {@link Mask} for location info. TODO: make it singleton */
public class MaskLocation implements Mask {

  /* hierarchical location info for each country */
  @SuppressWarnings("rawtypes")
  HashMap<String, HashMap> base;

  DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
  private static final Logger logger = LogManager.getLogger(Mask.class);

  public MaskLocation() {
    try {
      initLocationHierarchy();
      dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
    } catch (Exception e) {
      logger.error("Could not find the right MaskLocation block!\n");
      e.printStackTrace();
      System.exit(-1);
    }
  }

  /**
   * Read in location file and initialize the location hierarchy for the given country. Note that
   * the location has hierarchical structure, such as: 国->省->市->县, Country->State->City->Town
   *
   * @throws NoSuchAlgorithmException if we cannot handle the location file.
   */
  @SuppressWarnings({"rawtypes", "unchecked"})
  public void initLocationHierarchy() throws NoSuchAlgorithmException {
    try {
      URL locationURL = MaskLocation.class.getClassLoader().getResource("data/location.xml");
      assert locationURL != null;
      final File locFile = new File(locationURL.toURI());
      if (!locFile.setReadable(true)) {
        logger.error("Fail to set the location.csv file to be readable!");
        System.exit(-1);
      }
      String provinceName;
      String cityName;
      String countryName;
      HashMap<String, HashMap> province = new HashMap<>();
      HashMap<String, HashMap> city = new HashMap<>();
      HashMap<String, HashMap> china = new HashMap<>();
      DocumentBuilder db = dbf.newDocumentBuilder();
      Document doc = db.parse(locFile);
      doc.getDocumentElement().normalize();
      NodeList nodeList = doc.getElementsByTagName("item");
      int len = nodeList.getLength();

      for (int itr = 0; itr < len; itr++) {
        Node node = nodeList.item(itr);
        if (node.getNodeType() == Node.ELEMENT_NODE) {
          Element eElement = (Element) node;
          int level = Integer.parseInt(
              eElement.getElementsByTagName("level").item(0).getTextContent());
          String name = eElement.getElementsByTagName("name").item(0).getTextContent();
          String mergeName = eElement.getElementsByTagName("merger_name").item(0).getTextContent();
          String[] simpliedNameLinkWord = mergeName.split(",");
          String simpliedName = simpliedNameLinkWord[simpliedNameLinkWord.length - 1];
          String linkWord = name.replace(simpliedName, "");
          if (name.equals("市辖区") || name.equals("直辖区") || name.equals("直辖县")) {
            continue;
          }
          switch (level) {
            case 0:
              provinceName = simpliedName + "," + linkWord;
              china.put(provinceName, new HashMap<String, HashMap>());
              province = china.get(provinceName);
              break;
            case 1:
              cityName = simpliedName + "," + linkWord;
              province.put(cityName, new HashMap<String, HashMap>());
              city = province.get(cityName);
              break;
            case 2:
              countryName = simpliedName + "," + linkWord;
              city.put(countryName, null);
              break;
            case 3:
              cityName = simpliedName + "," + linkWord;
              province.put(cityName, null);
              break;
          }
          base = china;
        } else {
          logger.error("Unknown record in " + locFile + ": " + node.getBaseURI());
          System.exit(-1);
        }
      }
    } catch (FileNotFoundException e) {
      logger.error("Could not find the location file!");
      e.printStackTrace();
      System.exit(-1);
    } catch (IOException e) {
      logger.error("File IO error!");
      e.printStackTrace();
      System.exit(-1);
      e.printStackTrace();
    } catch (URISyntaxException e) {
      logger.error("Fail with extracting the URI of the location.csv!");
      e.printStackTrace();
      System.exit(-1);
    } catch (ParserConfigurationException e) {
      logger.error("Parser configuration error!");
      e.printStackTrace();
      System.exit(-1);
    } catch (SAXException e) {
      logger.error("SAX Exception!");
      e.printStackTrace();
      System.exit(-1);
    }
  }

  /**
   * Check if the given location is of standard format or not. TODO: add more explanations
   *
   * @param location location string
   * @param map the hierarchical location map of a given country
   * @return TODO: explain the return value
   * @throws NoSuchAlgorithmException if the given location string cannot be processed
   */
  @SuppressWarnings("rawtypes")
  private String checkStandardLocation(String location, HashMap<String, HashMap> map)
      throws NoSuchAlgorithmException {
    Iterator<Entry<String, HashMap>> iterator = map.entrySet().iterator();
    String selectedLocation = null;
    int minimumLoc = location.length() + 1;
    while (iterator.hasNext()) {
      Map.Entry<String, HashMap> entry = iterator.next();
      String key = entry.getKey();
      String subLocation = key.split(",")[0];
      int firstLoc = location.indexOf(subLocation);
      if (firstLoc != -1 && minimumLoc > firstLoc) {
        minimumLoc = firstLoc;
        selectedLocation = key;
      }
    }
    if (selectedLocation == null) {
      throw new NoSuchAlgorithmException();
    } else {
      return selectedLocation;
    }
  }

  /**
   * Given the input location, convert it into standard form: [province, city, country]
   *
   * @param input location string
   * @param fathermap the hierarchical location map of a given country
   * @return the standard form of input location
   * @throws NoSuchAlgorithmException if the given location string cannot be processed
   */
  @SuppressWarnings({"rawtypes", "unchecked"})
  private String[] getStandardLocation(String input, HashMap<String, HashMap> fathermap)
      throws NoSuchAlgorithmException {
    String[] StandardLocationSeries = new String[3];
    String Location = input.replaceAll("([^\\u4e00-\\u9fa5])", "");
    Location.replaceAll("中国|天朝|中华人民共和国|大陆", "");
    HashMap<String, HashMap> map = fathermap;
    for (int k = 0; k < 3 && map != null; k++) {
      try {
        String subLocationLinkWord = checkStandardLocation(Location, map);
        String[] subLocationLinkWordList = subLocationLinkWord.split(",");
        String subLocation = subLocationLinkWordList[0];
        String subLinkWord =
            (subLocationLinkWordList.length == 2) ? subLocationLinkWordList[1] : "";
        Location = Location.replaceFirst(subLocation, "");
        if (k < 2) {
          map = map.get(subLocationLinkWord);
        }
        StandardLocationSeries[k] = subLocation + subLinkWord;
      } catch (NoSuchAlgorithmException e) {
        break;
      }
    }
    return StandardLocationSeries;
  }

  /**
   * Mask the location info from input string with default level "province"
   *
   * @param input input string with sensitive location info
   * @return masked string
   * @throws NoSuchAlgorithmException if the given location string cannot be processed
   */
  public String mask(String input) throws NoSuchAlgorithmException {
    String standardQuickLocation = "";
    try {
      standardQuickLocation = mask(input, "province");
    } catch (NoSuchAlgorithmException e) {
      logger.error("We don't have algorithm to process " + input);
      e.printStackTrace();
      System.exit(-1);
    }
    return standardQuickLocation;
  }

  /**
   * Mask the location info from input string with explicitly specified level from [province, city,
   * country]
   *
   * @param input input string with sensitive location info
   * @return masked string
   * @throws NoSuchAlgorithmException if the given location string cannot be processed
   */
  public String mask(String input, String level) throws NoSuchAlgorithmException {
    String StandardQuickLocation = "";
    try {
      int count = 0;
      switch (level) {
        case "province":
          count = 1;
          break;
        case "city":
          count = 2;
          break;
        case "country":
          count = 3;
          break;
        default:
          logger.error("Unknown mask level " + level);
          System.exit(-1);
      }

      String[] StandardLocationSeries = getStandardLocation(input, base);
      StringBuilder StandardLocationBuffer = new StringBuilder();
      for (int k = 0; k < StandardLocationSeries.length; k++) {
        String LocationElement = StandardLocationSeries[k];
        if (LocationElement == null) {
          continue;
        } else {
          if (k < count) {
            StandardLocationBuffer.append(LocationElement);
          } else {
            StringBuilder stars = new StringBuilder();
            for (int i = 0; i < LocationElement.length(); i++) {
              stars.append("*");
            }
            StandardLocationBuffer.append(stars.toString());
          }
        }
      }
      StandardQuickLocation = StandardLocationBuffer.toString();
      StandardQuickLocation.replace("高开区", "高新技术产业开发区");
      StandardQuickLocation.replace("经开区", "经济开发区");
    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
    } catch (IllegalArgumentException e) {
      logger.error("The value of level should be chosen in province/city/country.");
      e.printStackTrace();
    }
    return StandardQuickLocation;
  }
}
