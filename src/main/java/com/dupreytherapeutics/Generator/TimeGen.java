package com.dupreytherapeutics.Generator;

import java.util.Random;

public class TimeGen {

  public static String gen() {
    Random rand = new Random();
    StringBuilder sb = new StringBuilder();
    sb.append(rand.nextInt(82) + 1940);
    sb.append("年");
    sb.append(rand.nextInt(13));
    sb.append("月");
    sb.append(rand.nextInt(29));
    sb.append("日");
    return sb.toString();
  }
}
