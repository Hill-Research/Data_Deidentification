package Generator;

import java.util.List;
import java.util.Random;

public class RandGen {
  public static String gen(List<String> list) {
    int N = list.size();
    return list.get(new Random().nextInt(N));
  }
}
