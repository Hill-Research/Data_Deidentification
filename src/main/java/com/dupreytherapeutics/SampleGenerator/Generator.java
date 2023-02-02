package com.dupreytherapeutics.SampleGenerator;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

class Generator {

  private static void print(FileWriter fw, String element) throws IOException {
    try {
      fw.write(element);
      fw.write(" ");
    } catch (IOException ioe) {
      System.err.println("IOException: " + ioe.getMessage());
    }
  }

  private static void print(FileWriter fw, String element, String suffix) throws IOException {
    try {
      fw.write(element);
      fw.write(suffix);
      fw.write(" ");
    } catch (IOException ioe) {
      System.err.println("IOException: " + ioe.getMessage());
    }
  }

  private static void gen_tb_inp_order() {
    try {
      String filename = "../data/db/tb_inp_order.txt";
      FileWriter fw = new FileWriter(filename, true);
      List<String> order_names = new ArrayList<>();
      order_names.add("儿童处方");
      order_names.add("成年处方");
      order_names.add("中医处方");
      List<String> keshi_names = new ArrayList<>();
      keshi_names.add("儿童科");
      keshi_names.add("泌尿科");
      keshi_names.add("耳鼻喉科");
      keshi_names.add("内科");
      keshi_names.add("外科");
      keshi_names.add("神经科");
      keshi_names.add("妇科");
      List<String> ops = new ArrayList<>();
      ops.add("0");
      ops.add("1");
      ops.add("2");

      fw.write("\n");
      for (int i = 11; i <= 1000; i++) {
        print(fw, CodeGen.gen(4, 0));
        print(fw, TimeGen.gen());
        print(fw, CodeGen.gen(4, 0));
        print(fw, CodeGen.gen(4, 2));
        print(fw, CodeGen.gen(0, 6));
        print(fw, CodeGen.gen(2, 2));
        print(fw, CodeGen.gen(1, 1));
        print(fw, RandGen.gen(order_names));
        print(fw, TimeGen.gen());
        print(fw, CodeGen.gen(3, 0));
        print(fw, RandGen.gen(keshi_names));
        print(fw, CodeGen.gen(0, 3), "元");
        print(fw, NameGen.gen());
        print(fw, TimeGen.gen());
        print(fw, NameGen.gen());
        print(fw, TimeGen.gen());
        print(fw, RandGen.gen(ops));
        fw.write("\n");
      }
      fw.close();
    } catch (IOException ioe) {
      System.err.println("IOException: " + ioe.getMessage());
    }
  }

  public static void main(String[] args) throws IOException {
    gen_tb_inp_order();
  }
}
