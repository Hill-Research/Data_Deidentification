package Algorithm;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class Hash {

  private static final Map<String, byte[]> saltMap;

  private static File saltFile;
  private static boolean verbose;

  static {
    saltMap = new HashMap<>();
    String configFilePath = "Common/config.properties";
    try {
      FileInputStream propsInput = new FileInputStream(configFilePath);
      Properties prop = new Properties();
      prop.load(propsInput);
      verbose = Boolean.parseBoolean(prop.getProperty("verbose"));
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
      System.out.printf("Create hash dir %s\n", dirName);
      if (!cacheDir.mkdirs()) {
        System.out.print("Fail to create the cache dir for hash_salt!\n");
        System.exit(-1);
      }
    } else {
      System.out.printf("Already exist hash dir %s\n", dirName);
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
      System.out.print("Try to insert null hash key!\n");
      System.exit(-1);
    }
    if (salt.length == 0) {
      System.out.print("Try to insert null hash salt!\n");
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
    if (verbose) {
      System.out.printf("Read1 hash salt file %s\n", saltFile.getAbsolutePath());
    }
    try {
      if (!saltFile.exists()) {
        if (!saltFile.createNewFile()) {
          System.out.printf("Fail to create the hash salt file %s!\n", saltFile.getAbsolutePath());
          System.exit(-1);
        }
      }
    } catch (Exception e) {
      System.out.print("Have exception when creating the hash salt file!\n");
      e.printStackTrace();
      System.exit(-1);
    }
    try {
      BufferedReader saltReader = new BufferedReader(new FileReader(saltFile));
      String record;
      while ((record = saltReader.readLine()) != null) {
        String[] keywords = record.split(":");
        if (keywords.length != 2) {
          System.out.printf("We found invalid hash salt record with len %d!\n", keywords.length);
          System.out.print(record);
          System.exit(-1);
        }
        saltMap.put(keywords[0], keywords[1].getBytes());
      }
      saltReader.close();
    } catch (IOException e) {
      System.out.print("Fail to read hash salt file!\n");
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
