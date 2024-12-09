package benchmarking;
// Simple microbenchmark setups
// sestoft@itu.dk * 2013-06-02, 2015-09-15
// jst@itu.dk * 2021-08-31 Moved code to contructor to follow Raul style
// raup@itu.dk * 05/10/2022

import java.util.function.IntToDoubleFunction;
public class Benchmark {
  public static void main(String[] args) { new Benchmark(); }
  
  public Benchmark() {
    SystemInfo();
  }

  // ========== Example functions and benchmarks ==========

  private static double multiply(int i) {
    double x = 1.1 * (double)(i & 0xFF);
     return x * x * x * x * x * x * x * x * x * x 
          * x * x * x * x * x * x * x * x * x * x;
  }
  private static double multiplyL(long i) {
    double x = 1.1 * (double)(i & 0xFFL);
     return x * x * x * x * x * x * x * x * x * x 
          * x * x * x * x * x * x * x * x * x * x;
  }

  // ========== Infrastructure code ==========

  public static void SystemInfo() {
    System.out.printf("# OS:   %s; %s; %s%n", 
                      System.getProperty("os.name"), 
                      System.getProperty("os.version"), 
                      System.getProperty("os.arch"));
    System.out.printf("# JVM:  %s; %s%n", 
                      System.getProperty("java.vendor"), 
                      System.getProperty("java.version"));
    // The processor identifier works only on MS Windows:
    System.out.printf("# CPU:  %s; %d \"cores\"%n", 
                      System.getenv("PROCESSOR_IDENTIFIER"),
                      Runtime.getRuntime().availableProcessors());
    java.util.Date now = new java.util.Date();
    System.out.printf("# Date: %s%n", 
      new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").format(now));
  }

  public static void Mark0() {         // USELESS
    Timer t = new Timer();
    double dummy = multiply(10);
    double time = t.check() * 1e9;
    System.out.printf("%6.1f ns%n", time);
  }

  public static void Mark1() {         // NEARLY USELESS
    Timer t = new Timer();
    int count = 20000_000;
    for (int i=0; i<count; i++) {
        double dummy = multiply( i);
    }
    double time = t.check()  ; /// count;
    System.out.printf("%6.3f s  %6.1fns %n", time, time*1e9/count);
  }

  public static double Mark2() {
    Timer t = new Timer();
    int count = 100_000_000;
    double dummy = 0.0;
    for (int i=0; i<count; i++) 
      dummy += multiply(i);
    double time = t.check() * 1e9 / count;
    System.out.printf("%6.1f ns%n", time);
    return dummy;
  }

  public static double Mark3() {
    int n = 10;
    int count = 100_000_000;
    double dummy = 0.0;
    for (int j=0; j<n; j++) {
      Timer t = new Timer();
      for (int i=0; i<count; i++) 
        dummy += multiply(i);
      double time = t.check() * 1e9 / count;
      System.out.printf("%6.1f ns%n", time);
    }
    return dummy / n;
  }

  public static double Mark4() {
    int n = 10;
    int count = 100_000_000;
    double dummy = 0.0;
    double st = 0.0, sst = 0.0;
    for (int j=0; j<n; j++) {
      Timer t = new Timer();
      for (int i=0; i<count; i++) 
        dummy += multiply(i);
      double time = t.check() * 1e9 / count;
      st += time; 
      sst += time * time;
    }
    double mean = st/n, sdev = Math.sqrt((sst - mean*mean*n)/(n-1));
    System.out.printf("%6.1f ns +/- %6.3f%n", mean, sdev);
    return dummy / n;
  }

  public static double Mark5() {
    int n = 10, count = 1, totalCount = 0;
    double dummy = 0.0, runningTime = 0.0, st = 0.0, sst = 0.0;
    do {
      count *= 2;
      st = sst = 0.0;
      for (int j=0; j<n; j++) {
        Timer t = new Timer();
        for (int i=0; i<count; i++) 
          dummy += multiply(i);
        runningTime = t.check();
        double time = runningTime * 1e9 / count;
        st += time; 
        sst += time * time;
        totalCount += count;
      }
      double mean = st/n, sdev = Math.sqrt((sst - mean*mean*n)/(n-1));
      System.out.printf("%6.1f ns +/- %8.2f %10d%n", mean, sdev, count);
    } while (runningTime < 0.25 && count < Integer.MAX_VALUE/2);
    return dummy / totalCount;
  }

  public static double Mark6(String msg, IntToDoubleFunction f) {
    int n = 10, count = 1, totalCount = 0;
    double dummy = 0.0, runningTime = 0.0, st = 0.0, sst = 0.0;
    do { 
      count *= 2;
      st = sst = 0.0;
      for (int j=0; j<n; j++) {
        Timer t = new Timer();
        for (int i=0; i<count; i++) 
          dummy += f.applyAsDouble(i);
        runningTime = t.check();
        double time = runningTime * 1e9 / count;
        st += time; 
        sst += time * time;
        totalCount += count;
      }
      double mean = st/n, sdev = Math.sqrt((sst - mean*mean*n)/(n-1));
      System.out.printf("%-25s %15.1f ns %10.2f %10d%n", msg, mean, sdev, count);
    } while (runningTime < 0.25 && count < Integer.MAX_VALUE/2);
    return dummy / totalCount;
  }

  public static double Mark7(String msg, IntToDoubleFunction f) {
    int n = 10, count = 1, totalCount = 0;
    double dummy = 0.0, runningTime = 0.0, st = 0.0, sst = 0.0;
    do { 
      count *= 2;
      st = sst = 0.0;
      for (int j=0; j<n; j++) {
        Timer t = new Timer();
        for (int i=0; i<count; i++) 
          dummy += f.applyAsDouble(i);
        runningTime = t.check();
        double time = runningTime * 1e9 / count;
        st += time; 
        sst += time * time;
        totalCount += count;
      }
    } while (runningTime < 0.25 && count < Integer.MAX_VALUE/2);
    double mean = st/n, sdev = Math.sqrt((sst - mean*mean*n)/(n-1));
    System.out.printf("%-25s %15.1f ns %10.2f %10d%n", msg, mean, sdev, count);
    return dummy / totalCount;
  }

  public static double Mark8(String msg, String info, IntToDoubleFunction f, 
                             int n, double minTime) {
    int count = 1, totalCount = 0;
    double dummy = 0.0, runningTime = 0.0, st = 0.0, sst = 0.0;
    do { 
      count *= 2;
      st = sst = 0.0;
      for (int j=0; j<n; j++) {
        Timer t = new Timer();
        for (int i=0; i<count; i++) 
          dummy += f.applyAsDouble(i);
        runningTime = t.check();
        double time = runningTime * 1e9 / count;
        st += time; 
        sst += time * time;
        totalCount += count;
      }
    } while (runningTime < minTime && count < Integer.MAX_VALUE/2);
    double mean = st/n, sdev = Math.sqrt((sst - mean*mean*n)/(n-1));
    System.out.printf("%-25s %s%15.1f ns %10.2f %10d%n", msg, info, mean, sdev, count);
    return dummy / totalCount;
  }

  public static double Mark8(String msg, IntToDoubleFunction f) {
    return Mark8(msg, "", f, 10, 0.25);
  }

  public static double Mark8(String msg, String info, IntToDoubleFunction f) {
    return Mark8(msg, info, f, 10, 0.25);
  }

  public static double Mark8Setup(String msg, String info, Benchmarkable f, 
                                  int n, double minTime) {
    int count = 1, totalCount = 0;
    double dummy = 0.0, runningTime = 0.0, st = 0.0, sst = 0.0;
    do { 
      count *= 2;
      st = sst = 0.0;
      for (int j=0; j<n; j++) {
        Timer t = new Timer();
        for (int i=0; i<count; i++) {
          t.pause();
          f.setup();
          t.play();
          dummy += f.applyAsDouble(i);
        }
        runningTime = t.check();
        double time = runningTime * 1e9 / count;
        st += time; 
        sst += time * time;
        totalCount += count;
      }
    } while (runningTime < minTime && count < Integer.MAX_VALUE/2);
    double mean = st/n, sdev = Math.sqrt((sst - mean*mean*n)/(n-1));
    System.out.printf("%-25s %s%15.1f ns %10.2f %10d%n", msg, info, mean, sdev, count);
    return dummy / totalCount;
  }

  public static double Mark8Setup(String msg, Benchmarkable f) {
    return Mark8Setup(msg, "", f, 10, 0.25);
  }

  public static double Mark8Setup(String msg, String info, Benchmarkable f) {
    return Mark8Setup(msg, info, f, 10, 0.25);
  }
}
