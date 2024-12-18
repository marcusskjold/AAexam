Data produced with the following function calls:

t12e1(   512_000, 2.0, 6, 8.0, "t12e1_1");
t12e1( 1_024_000, 2.0, 6, 8.0, "t12e1_2");
t12e1( 2_000_000, 2.0, 6, 8.0, "t12e1_3");
t12e1( 4_000_000, 2.0, 6, 8.0, "t12e1_4");
t12e1( 8_000_000, 2.0, 6, 8.0, "t12e1_5");
t12e1(16_000_000, 2.0, 6, 8.0, "t12e1_6");
t12e1(32_000_000, 2.0, 6, 8.0, "t12e1_7");

```
t12e1(int n, double s, int e, double time, String title) {
    IntFunction<Experiment<Integer[]>> ex = parameterValue -> new Experiment<>(
        generate(n, i -> i),
        d -> RecursiveMergeSortParallel.sort(d, parameterValue),
        Handler::randomize
    );

    int lowerBound = (int) (n / (Math.pow(s, e)));
    measure(ex, time, lowerBound, n+1, s).analyze(title).saveAsCSV().print();
}
```
