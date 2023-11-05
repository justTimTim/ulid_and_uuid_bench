package be.easy.bench;

import java.io.IOException;


public class BenchIdApplication {

  public static void main(String[] args) {
    try {
      org.openjdk.jmh.Main.main(args);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }


}
