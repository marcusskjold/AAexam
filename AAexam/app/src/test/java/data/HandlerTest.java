package data;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import static data.Handler.Ext;
import static data.Handler.Cat;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;

import sorting.Util;

/**
 * DataGeneratorTest
 * TODO: Write a parameterized test that check the invariant holds for many sizes of lists and / or percentages
 * TODO: Think through exactly what tests are needed to be confident of correctness.
 * Currently, only very basic tests are done to confirm that *something* is done.
 * Mainly, there is no test that ensures that the randomization is of high quality.
 * Also, only Integers are tested.
 */
public class HandlerTest{

    /** Make sure the generator generates the specified number of elements */
    @Test void generate_matchesLength() {
        Integer[] actual = Handler.generate(10_000, i -> i);
        assertEquals(10_000, actual.length);
    }

    @Test void
    givenSameLength_whenGenerateRandomString_generatesUniqueStrings() {
        int n = 100;
        String[] ss = Handler.generate(n, i -> Handler.randomString(20));
        Set<String> uniqueStrings = new HashSet<>(100);
        for (int i = 0; i < n; i++) uniqueStrings.add(ss[i]); 
        assertEquals(n, uniqueStrings.size());
    }

    @Test void
    givenSameLength_whenGenerateRandomString_generatesUnsortedArray() {
        int n = 100;
        String[] ss = Handler.generate(n, i -> Handler.randomString(20));
        assertFalse(Util.isSorted(ss));
    }

    /** Make sure the generator output array is sorted */
    @Test void generate_isSorted() {
        // This generator function takes the index, and so the generator assumes the output is automatically sorted.
        Integer[] actual = Handler.generate(10_000, i -> i);
        Integer[] expected = actual.clone();
        Arrays.sort(expected);
        assertArrayEquals(expected, actual);

        // The string generator function is a supplier, and so the generator manually sorts the output.
        String[] sActual = Handler.generate(1_000, () -> Handler.randomString(10));
        String[] sExpected = sActual.clone();
        assertArrayEquals(sExpected, sActual);
    }

    /** Basic check that *something* happens when randomize is called */
    @Test void randomize_randomizes() {
        Integer[] expected = Handler.generate(10_000, i -> i);
        Integer[] actual   = Handler.randomize(expected);
        assertFalse(Arrays.equals(expected,actual));
    }

    /** Randomize should accept boundary values but reject thos outside the boundary */
    @Test void randomize_throwsCorrectly() {
        Integer[] dummy = Handler.generate(10_000, i -> i);
        Handler.randomize(dummy, 0);
        Handler.randomize(dummy, 100);

        assertThrows(IllegalArgumentException.class, () ->
            Handler.randomize(dummy, -1));

        assertThrows(IllegalArgumentException.class, () ->
            Handler.randomize(dummy, 101));

    }

    /** We expect no change unless at least two elements can be swapped. */
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

    /** This implicitly tests multiple methods */
    @Test void fileIO_works() {
        Integer[] expected = Handler.generate(100, i -> i);
        Handler.writeToFile("test", expected, Cat.UNITTEST, Ext.OUT);
        Integer[] actual = Handler.readData(Handler.streamFile("unittest/test.Integer.out"), Integer::valueOf);
        assertArrayEquals(expected, actual);
    }
}
