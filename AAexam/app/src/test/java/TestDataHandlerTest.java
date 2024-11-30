import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * DataGeneratorTest
 */
public class TestDataHandlerTest {
    TestData[] actual, expected, dummy;

    @BeforeEach void reset() {
        actual   = null;
        expected = null;
        dummy    = null;
    }

    @Test void seqGen_generatesCorrectAmountOfTestData() {
        actual = TestDataHandler.seqGen(0, 10_000_000);
        assertEquals(10_000_000, actual.length);
    }

    @Test void seqGen_generatesSortedTestData() {
        actual = TestDataHandler.seqGen(-10, 1_000_000);
        expected = actual.clone();
        Arrays.sort(expected);
        assertArrayEquals(expected, actual);
    }

    @Test void randomize_randomizes() {
        expected = TestDataHandler.seqGen(-1000, 1_000_000);
        actual =  TestDataHandler.randomizeData(expected);
        assertFalse(Arrays.equals(expected,actual));
    }

    @Test void randomize_throws() {
        dummy = TestDataHandler.seqGen(0, 100);
        assertThrows(IllegalArgumentException.class, () ->
            TestDataHandler.randomizeData(dummy, -1));

        assertThrows(IllegalArgumentException.class, () ->
            TestDataHandler.randomizeData(dummy, 101));
    }

    // We expect no change unless at least two elements can be swapped.
    @Test void randomize_lowresolution_respectsminimum() {
        expected = TestDataHandler.seqGen(0, 10);
        actual   = TestDataHandler.randomizeData(expected, 19);
        assertArrayEquals(expected, actual);

        expected = TestDataHandler.seqGen(0, 10);
        actual   = TestDataHandler.randomizeData(expected, 1);
        assertArrayEquals(expected, actual);

        // 7 * 0.28 < 2
        expected = TestDataHandler.seqGen(0, 7);
        actual   = TestDataHandler.randomizeData(expected, 28);
        assertArrayEquals(expected, actual);

        // 199 * 0.01 < 2
        expected = TestDataHandler.seqGen(0, 199);
        actual   = TestDataHandler.randomizeData(expected, 1);
        assertArrayEquals(expected, actual);
    }

    /** This also implicitly tests all other methods */
    @Test void fileIO_works() {
        expected = TestDataHandler.seqGen(-2, 0);
        TestDataHandler.writeToFile("test.data", expected);
        actual = TestDataHandler.readFile("test.data");
        assertArrayEquals(expected, actual);
    }

}
