package data;
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
public class HandlerTest{

    @Test void generate_matchesLength() {
        Integer[] actual = Handler.generate(10_000_000, i -> i);
        assertEquals(10_000_000, actual.length);
    }

    @Test void generate_isSorted() {
        Integer[] actual = Handler.generate(10_000_000, i -> i);
        Integer[] expected = actual.clone();
        Arrays.sort(expected);
        assertArrayEquals(expected, actual);
    }

    @Test void randomize_randomizes() {
        Integer[] expected = Handler.generate(10_000_000, i -> i);
        Integer[] actual   = Handler.randomize(expected);
        assertFalse(Arrays.equals(expected,actual));
    }

    @Test void randomize_throwsCorrectly() {
        Integer[] dummy = Handler.generate(10_000_000, i -> i);
        Handler.randomize(dummy, 0);
        Handler.randomize(dummy, 100);

        assertThrows(IllegalArgumentException.class, () ->
            Handler.randomize(dummy, -1));

        assertThrows(IllegalArgumentException.class, () ->
            Handler.randomize(dummy, 101));

    }

    // We expect no change unless at least two elements can be swapped.
    @Test void randomize_atBoundaryValues_roundsCorrectly() {
        Integer[] expected = Handler.generate(10, i -> i);
        Integer[] actual   = Handler.randomize(expected, 19);
        assertArrayEquals(expected, actual);

        expected = Handler.generate(10, i -> i);
        actual   = Handler.randomize(expected, 1);
        assertArrayEquals(expected, actual);

        // 7 * 0.28 < 2
        expected = Handler.generate(7, i -> i);
        actual   = Handler.randomize(expected, 28);
        assertArrayEquals(expected, actual);

        // 199 * 0.01 < 2
        expected = Handler.generate(199, i -> i);
        actual   = Handler.randomize(expected, 1);
        assertArrayEquals(expected, actual);
    }

    /** This also implicitly tests all other methods */
    @Test void fileIO_works() {
        Integer[] expected = Handler.generate(1_000_000, i -> i);
        Handler.writeToFile("test", expected, Handler.Category.UNITTEST, Handler.Ext.OUT);
        Integer[] actual = Handler.readData(Handler.streamFile("unittest/test.Integer.out"), s -> Integer.valueOf(s));
        assertArrayEquals(expected, actual);
    }

    // TODO: Write a parameterized test that check the invariant holds for many sizes of lists and / or percentages

}
