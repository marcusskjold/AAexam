import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * DataGeneratorTest
 */
public class TestDataTest{
    TestData[] actual, expected, dummy;

    @BeforeEach void reset() {
        actual   = null;
        expected = null;
        dummy    = null;
    }

    @Test void seqGen_generatesCorrectAmountOfTestData() {
        actual = TestData.seqGen(0, 10_000_000);
        assertEquals(10_000_000, actual.length);
    }

    @Test void seqGen_generatesSortedTestData() {
        actual   = TestData.seqGen(-10, 1_000_000);
        expected = actual.clone();
        Arrays.sort(expected);
        assertArrayEquals(expected, actual);
    }

    @Test void randomize_randomizes() {
        expected = TestData.seqGen(-1000, 1_000_000);
        actual   = TestData.randomize(expected);
        assertFalse(Arrays.equals(expected,actual));
    }

    @Test void randomize_throwsCorrectly() {
        dummy = TestData.seqGen(0, 100);
        TestData.randomize(dummy, 0);
        TestData.randomize(dummy, 100);

        assertThrows(IllegalArgumentException.class, () ->
            TestData.randomize(dummy, -1));

        assertThrows(IllegalArgumentException.class, () ->
            TestData.randomize(dummy, 101));

    }

    // We expect no change unless at least two elements can be swapped.
    @Test void randomize_lowresolution_respectsminimum() {
        expected = TestData.seqGen(0, 10);
        actual   = TestData.randomize(expected, 19);
        assertArrayEquals(expected, actual);

        expected = TestData.seqGen(0, 10);
        actual   = TestData.randomize(expected, 1);
        assertArrayEquals(expected, actual);

        // 7 * 0.28 < 2
        expected = TestData.seqGen(0, 7);
        actual   = TestData.randomize(expected, 28);
        assertArrayEquals(expected, actual);

        // 199 * 0.01 < 2
        expected = TestData.seqGen(0, 199);
        actual   = TestData.randomize(expected, 1);
        assertArrayEquals(expected, actual);
    }

    /** This also implicitly tests all other methods */
    @Test void fileIO_works() {
        expected = TestData.seqGen(-2, 0);
        TestData
.writeToFile("test.data", expected);
        actual = TestData.readFile("test.data");
        assertArrayEquals(expected, actual);
    }

    // TODO: Write a parameterized test that check the invariant holds for many sizes of lists and / or percentages

}
