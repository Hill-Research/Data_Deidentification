package com.dupreytherapeutics.Algorithm.Blur;

import java.security.NoSuchAlgorithmException;

public class BlurNumber implements Blur {

  private int getStandardNumber(String input) {
    String Number = input.replaceAll("[^0-9]", "");
    int standardNumberValue = Integer.parseInt(Number);
    return standardNumberValue;
  }

  public String blur(String input) throws NoSuchAlgorithmException {
    String StandardAge = null;
    try {
      StandardAge = blur(input, "10");
    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
    }
    return StandardAge;
  }

  public String blur(String input, String level) throws NoSuchAlgorithmException {
    int count = Integer.parseInt(level);
    int standardNumberValue = getStandardNumber(input);
    int period = standardNumberValue / count;
    String standardNumber = String.format("%d-%d", period * count, (period + 1) * count);
    return standardNumber;
  }
}
