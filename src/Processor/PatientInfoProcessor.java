package Processor;

import Algorithm.Blur;
import Algorithm.Hash;
import Algorithm.Mask;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.security.NoSuchAlgorithmException;

public class PatientInfoProcessor {

  private static BufferedWriter bw;

  private static void print(String element) throws IOException {
    System.out.printf("Start to print %s\n", element);
    bw.write(element);
    bw.write(" ");
  }

  private static void processOneRecord(String[] keywords)
      throws NoSuchAlgorithmException, IOException {
    if (keywords.length != 32) {
      System.out.print("Illegal keyword length!\n");
      System.exit(-1);
    }
    String BRID = keywords[3];
    print(BRID);
    String XM = keywords[4];
    String hash_name = Hash.hash(XM);
    print(hash_name);
    String XBMC = keywords[6];
    print(XBMC);
    String CSRQ = keywords[9];
    String blurBirthDate = Blur.BlurTime.blur(CSRQ, "month");
    print(blurBirthDate);
    String NL = keywords[10];
    String blurAge = Blur.BlurAge.run(NL);
    print(blurAge);
    String BRLXDM = keywords[11];
    print(BRLXDM);
    String BRLXMC = keywords[12];
    print(BRLXMC);
    String HYMC = keywords[14];
    print(HYMC);
    String MZMC = keywords[16];
    print(MZMC);
    String GJMC = keywords[18];
    print(GJMC);
    String ZY = keywords[19];
    print(ZY);
    String XZZ = keywords[22];
    String maskAddr = Mask.MaskAddr.run(XZZ);
    print(maskAddr);
  }

  public static void process(final File file) throws IOException, NoSuchAlgorithmException {
    File deIdFile = new File("../data/tb_patientinfo_deidentification.txt");
    if (deIdFile.exists()) {
      deIdFile.delete();
    }
    deIdFile.createNewFile();
    FileOutputStream fileOutput = new FileOutputStream(deIdFile);
    bw = new BufferedWriter(new OutputStreamWriter(fileOutput));
    BufferedReader reader = new BufferedReader(new FileReader(file));
    String record;
    while ((record = reader.readLine()) != null) {
      String[] keywords = record.split("\\s+");
      processOneRecord(keywords);
    }
    bw.close();
    fileOutput.close();
  }
}
