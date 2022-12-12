package com.dupreytherapeutics.Algorithm;

import com.dupreytherapeutics.Demo.Main;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
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

public class Hash {

  private static final Map<String, byte[]> saltMap;

  private static File saltFile;
  private static final Logger logger = LogManager.getLogger(Hash.class);

  static {
    saltMap = new HashMap<>();
    URL configURL = Main.class.getClassLoader().getResource("config.properties");
    assert configURL != null;
    String configFilePath = configURL.getPath();
    try {
      FileInputStream propsInput = new FileInputStream(configFilePath);
      Properties prop = new Properties();
      prop.load(propsInput);
      String hash_salt_cache_directory = prop.getProperty("hash_salt_cache_directory");
      createDirIfNotExist(hash_salt_cache_directory);
      String salt_file = prop.getProperty("salt_file");
      saltFile = new File(hash_salt_cache_directory + salt_file);
      readSaltFile(saltFile);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private static void createDirIfNotExist(String dirName) {
    File cacheDir = new File(dirName);
    if (!cacheDir.exists()) {
      logger.info("Create hash dir " + dirName);
      if (!cacheDir.mkdirs()) {
        logger.error("Fail to create the cache dir for hash_salt!\n");
        System.exit(-1);
      }
    } else {
      logger.info("Already exist hash dir "+ dirName);
    }
  }

  private static byte[] getSalt(String input) {
    if (saltMap.containsKey(input)) {
      return saltMap.get(input);
    }
    SecureRandom random = new SecureRandom();
    byte[] salt = new byte[16];
    random.nextBytes(salt);
    saltMap.put(input, salt);
    print(input, salt);
    return salt;
  }

  private static void print(String element, byte[] salt) {
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
      e.printStackTrace();
    }
  }

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
          logger.error("We found invalid hash salt record with len %d!\n", keywords.length);
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
