package experiments;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import experiments.Result.Key;

public class MeasurementTest {
    static SingleResult r;
    static SingleRunMeasurement m;

    @BeforeAll static void simpleSetup() {
        r = new SingleRunMeasurement(
            new Experiment<Integer>(i -> i, i -> i), 10)
            .analyze("test");
        m = new SingleRunMeasurement(
            new Experiment<Integer>(i -> i, i -> i), 10);
    }

    @Test void
    SRM__givenRepetition_whenInitialization_fieldsAreCorrect() {
        assertEquals(10, m.observations());
        assertEquals(4.5, m.averageResult());
        assertTrue(0 < m.runningTime());
        assertTrue(0 < m.averageTime());
    }

    @Test void
    SRM__givenMeasurement_whenAnalyze_generatesCorrectStdDev() {
        assertEquals(3.0276503540974917, r.get(Key.SDEVRESULT));
    }

    @Test void
    SRM__givenMeasurement_whenAnalyze_generatesCorrectResult() {
        assertEquals(3.0276503540974917, r.get(Key.SDEVRESULT));
        assertEquals(4.5, r.get(Key.MEANRESULT));
        assertEquals(10, r.get(Key.REPETITIONS));
        assertEquals(-1.0, r.get(Key.RUNS));
        assertEquals(-1.0, r.get(Key.PARAMETER));
        assertNotNull(r.get(Key.SDEVTIME));
        assertNotNull(r.get(Key.SDEVTIME));
    }
}
