package com.dupreytherapeutics.Algorithm.Blur;

import java.security.NoSuchAlgorithmException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/** This class implements the interface {@link Blur} for Number item. */
public class BlurNumber implements Blur {
  private static final Logger logger = LogManager.getLogger(BlurNumber.class);

  /**
   * This method removes any non-digit character from the input string
   *
   * @param input input number of string format. Note that the input could have abnormal symbols due
   *     to typos, and we need to remove them.
   * @return the standard number after stripping the abnormal characters from input string.
   */
  private int getStandardNumber(String input) {
    String Number = input.replaceAll("[^0-9]", "");
    return Integer.parseInt(Number);
  }

  /**
   * Blur the input number using default level 10.
   *
   * @param input number to be blurred
   * @return blurred number
   * @throws NoSuchAlgorithmException when the corresponding blur block is not found
   */
  public String blur(String input) throws NoSuchAlgorithmException {
    String StandardAge = null;
    try {
      StandardAge = blur(input, "10");
    } catch (NoSuchAlgorithmException e) {
      logger.error("We could not find algorithm for blur number!");
      e.printStackTrace();
      System.exit(-1);
    }
    return StandardAge;
  }

  /**
   * Blur the input number with explicitly-specified level. Suppose the input number is "1234", then
   * if the level is "10", the blurred value is "1230"; if the level is "100", then the blurred
   * value is "1200".
   *
   * @param input item to be blurred
   * @param level at which the number to be blurred
   * @return blurred number
   * @throws NoSuchAlgorithmException when the corresponding blur block is not found.
   */
  public String blur(String input, String level) throws NoSuchAlgorithmException {
    // TODO: need to validate "level"
    int count = Integer.parseInt(level);
    int standardNumberValue = getStandardNumber(input);
    int period = standardNumberValue / count;
    return String.format("%d-%d", period * count, (period + 1) * count);
  }
}
