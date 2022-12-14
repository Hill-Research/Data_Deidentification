package com.dupreytherapeutics.Algorithm;

import com.dupreytherapeutics.Processor.Processor;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/** This class implements the hashing algorithm for input string. */
public class Hash {

  private static final Map<String, byte[]> saltMap;

  /** the salt file used by the {@link Hash} block */
  private static File saltFile;

  private static final Logger logger = LogManager.getLogger(Hash.class);

  static {
    saltMap = new HashMap<>();
    URL configURL = Hash.class.getClassLoader().getResource("config.properties");
    assert configURL != null;
    String configFilePath = configURL.getPath();
    try {
      FileInputStream propsInput = new FileInputStream(configFilePath);
      Properties prop = new Properties();
      prop.load(propsInput);
      String classpath =
          Processor.class.getProtectionDomain().getCodeSource().getLocation().getPath();
      String hash_salt_cache_directory = classpath + prop.getProperty("hash_salt_cache_directory");
      createDirIfNotExist(hash_salt_cache_directory);
      String salt_file = prop.getProperty("salt_file");
      saltFile = new File(hash_salt_cache_directory + salt_file);
      readSaltFile(saltFile);
    } catch (FileNotFoundException e) {
      logger.error("We did not find the config/salt files for the hash block!");
      e.printStackTrace();
      System.exit(-1);
    } catch (IOException e) {
      logger.error("We fail to create cache directory for the hash salt file!\n");
      e.printStackTrace();
      System.exit(-1);
    }
  }

  /**
   * Create the directory if it does not exist
   *
   * @param dirName the name of the directory which needs to exist
   */
  private static void createDirIfNotExist(String dirName) {
    File cacheDir = new File(dirName);
    if (!cacheDir.exists()) {
      logger.info("Create hash dir " + dirName);
      if (!cacheDir.mkdirs()) {
        logger.error("Fail to create the cache dir for hash_salt!\n");
        System.exit(-1);
      }
    } else {
      logger.info("Already exist hash dir " + dirName);
    }
  }

  /**
   * Get the hash salt for the input string. Note that for the same input string, it always needs to
   * use the same salt when doing the hashing, so we need to cache the salt if it is newly generated
   *
   * @param input input string
   * @return the hash salt
   */
  private static byte[] getSalt(String input) {
    if (saltMap.containsKey(input)) {
      return saltMap.get(input);
    }
    SecureRandom random = new SecureRandom();
    byte[] salt = new byte[16];
    random.nextBytes(salt);
    saltMap.put(input, salt);
    writeToSaltFile(input, salt);
    return salt;
  }

  /**
   * Write the <input string, salt> pair to the cache file
   *
   * @param element input string
   * @param salt the newly generated salt for the input string
   */
  private static void writeToSaltFile(String element, byte[] salt) {
    if (element.isEmpty()) {
      logger.error("Try to insert null hash key!\n");
      System.exit(-1);
    }
    if (salt.length == 0) {
      logger.error("Try to insert null hash salt!\n");
      System.exit(-1);
    }
    try {
      FileWriter saltFileWriter = new FileWriter(saltFile, true);
      BufferedWriter bw = new BufferedWriter(saltFileWriter);
      PrintWriter out = new PrintWriter(bw);
      out.print(element);
      out.print(":");
      out.print(Arrays.toString(salt));
      out.print("\n");
      out.close();
      bw.close();
    } catch (IOException e) {
      logger.error("Fail with salt cache file!");
      e.printStackTrace();
      System.exit(-1);
    }
  }

  /**
   * read the salt cache file and extract the existing <input string, salt> pairs
   *
   * @param saltFile the salt cache file
   */
  public static void readSaltFile(final File saltFile) {

    logger.info("Read1 hash salt file " + saltFile.getAbsolutePath());

    try {
      if (!saltFile.exists()) {
        if (!saltFile.createNewFile()) {
          logger.error("Fail to create the hash salt file " + saltFile.getAbsolutePath());
          System.exit(-1);
        }
      }
    } catch (Exception e) {
      logger.error("Have exception when creating the hash salt file!\n");
      e.printStackTrace();
      System.exit(-1);
    }
    try {
      BufferedReader saltReader = new BufferedReader(new FileReader(saltFile));
      String record;
      while ((record = saltReader.readLine()) != null) {
        String[] keywords = record.split(":");
        if (keywords.length != 2) {
          logger.error("We found invalid hash salt record with len " + keywords.length);
          logger.error(record);
          System.exit(-1);
        }
        saltMap.put(keywords[0], keywords[1].getBytes());
      }
      saltReader.close();
    } catch (IOException e) {
      logger.error("Fail to read hash salt file!\n");
      e.printStackTrace();
      System.exit(-1);
    }
  }

  /**
   * Run the hashing algorithm on the input string. It is using SHA-512 by default.
   *
   * @param input input string
   * @return the hash value
   * @throws NoSuchAlgorithmException if the specified hash algorithm does not exist
   */
  public static String hash(String input) throws NoSuchAlgorithmException {
    String generatedPassword;
    byte[] salt = getSalt(input);
    MessageDigest md = MessageDigest.getInstance("SHA-512");
    md.update(salt);
    byte[] bytes = md.digest(input.getBytes(StandardCharsets.UTF_8));
    StringBuilder sb = new StringBuilder();
    for (byte aByte : bytes) {
      sb.append(Integer.toString((aByte & 0xff) + 0x100, 16).substring(1));
    }
    generatedPassword = sb.toString();
    return generatedPassword;
  }
}
