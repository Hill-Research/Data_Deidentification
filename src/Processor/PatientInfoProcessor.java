package Processor;

import Algorithm.Hash;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public class PatientInfoProcessor {
  public static void process(final File file) throws IOException, NoSuchAlgorithmException {
    BufferedReader reader = new BufferedReader(new FileReader(file));
        String record;
        while ((record = reader.readLine()) != null) {
            String[] keywords = record.split("\\s+");
            String name = keywords[4];
            String hash_name = Hash.hash(name);
            System.out.printf("Name: %s, hash_name: %s\n", name, hash_name);
        }
  }
}
