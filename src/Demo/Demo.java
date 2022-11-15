package Demo;

import Processor.Processor;
import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

class Demo {

  static Processor processor = new Processor();

  private static void callProcessor(final File file) throws IOException, NoSuchAlgorithmException {
    String fileName = file.getName();
    processor.process(fileName);
  }

  public static void main(String[] args) throws IOException, NoSuchAlgorithmException {
    System.out.println("This is the data de-identification demo.\n");
    final File folder = new File("../data/sample");
    File[] listOfFiles = folder.listFiles();
    if (listOfFiles == null) {
      System.out.format("We could not find any data files in %s!", folder.getName());
      System.exit(-1);
    }
    for (File file : listOfFiles) {
      if (!file.isFile()) {
        System.out.format("We found abnormal file %s!", file.getName());
        System.exit(-1);
      }
      callProcessor(file);
    }
  }
}
