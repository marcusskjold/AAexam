package experiments;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

import experiments.Result.Key;

public class ResultTest {
    @Test void
    givenNoKeys_whenPrint_thenCorrectFormat() {
        SingleResult r = new SingleResult("test");
        assertEquals("test", r.toString());
    }

    @Test void
    givenSomeKeys_whenPrint_thenCorrectFormat() {
        SingleResult r = new SingleResult("test");
        r.put(Key.RUNS, 10.0);
        assertEquals("test                               10", r.toString());
    }

    @Test void
    givenAllKeys_whentoString_thenCorrectFormat() {
        SingleResult r = new SingleResult("test");
        r.put(Key.REPETITIONS, 8000.0);
        r.put(Key.MEANTIME, 8000.407414);
        r.put(Key.SDEVTIME, 0.403414);
        r.put(Key.MEANRESULT, 1000.0);
        r.put(Key.SDEVRESULT, 99.003414);
        r.put(Key.RUNS, 10.0);
        r.put(Key.PARAMETER, 79.0);
        assertEquals(
            "test                        79     10        8,000               8,000          0.4        1,000         99.0",
            r.toString());
    }

    @Test void
    givenAllKeys_whenCSV_thenCorrectFormat() {
        SingleResult r = new SingleResult("test");
        r.put(Key.REPETITIONS, 8000.0);
        r.put(Key.MEANTIME, 8000.407414);
        r.put(Key.SDEVTIME, 0.403414);
        r.put(Key.MEANRESULT, 1000.0);
        r.put(Key.SDEVRESULT, 99.003414);
        r.put(Key.RUNS, 10.0);
        r.put(Key.PARAMETER, 79.0);
        List<String> csv = r.toCSV();
        assertEquals(2, csv.size());
        assertEquals(
            "TITLE,PARAMETER,RUNS,REPETITIONS,MEANTIME,SDEVTIME,MEANRESULT,SDEVRESULT", csv.getFirst());
        assertEquals(
            "test,79.0,10.0,8000.0,8000.407414,0.403414,1000.0,99.003414", csv.getLast());
    }
}
