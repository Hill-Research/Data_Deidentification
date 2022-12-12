package com.dupreytherapeutics.Generator;

import java.util.Random;

public class CodeGen {

  public static String gen(int numLetters, int numNumbers) {
    Random rand = new Random();
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < numLetters; i++) {
      sb.append(String.valueOf((char) (rand.nextInt(26) + 'A')));
    }
    for (int i = 0; i < numNumbers; i++) {
      sb.append(rand.nextInt(10));
    }
    return sb.toString();
  }
}
