package data;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Handler
 */
public class Handler {
    private static final long DEFAULT_SEED = 298092841098572l;
    private static final Random r          = new Random(DEFAULT_SEED);

    // ============================== Data generation ===================================

    @SuppressWarnings("unchecked")
    public static <T> T[] generate(int n, Function<Integer, T> generator) {
        @SuppressWarnings("rawtypes") Class c = generator.apply(0).getClass();
        return (T[]) IntStream.range(0, n).boxed()
            .map(i -> generator.apply(i))
            .toArray(i -> (T[]) Array.newInstance(c, i));

    }

    @SuppressWarnings("unchecked")
    public static <T extends Comparable<? super T>> T[] generate(int n, Supplier<T> generator) {
        @SuppressWarnings("rawtypes") Class c = generator.get().getClass();
        return (T[]) Stream
            .generate(generator)
            .limit(n)
            .sorted()
            .toArray(i -> (T[]) Array.newInstance(c, i));

    }

    public static TestData testDataGen(int start, int i, int repeat, int increment) {
        return new TestData(r.nextInt(), start + ((i / repeat) * increment));
    }

    public static String randomString(int length) {
        final char[] chars = "0123456789abcdefghijklmnopqrstuvxyzABCDEFGHIJKLMNOPQRSTUVXYZ ".toCharArray();
        final StringBuilder b = new StringBuilder(length);;
        for (int i = 0; i < length; i++) b.append(chars[r.nextInt(chars.length)]);
        return b.toString();
    }

    /** Generates a TestData object from a plain text description.
     * @param string must contain first the ID as an integer, then an integer value.
     * @throws IllegalArgumentException if the string does not contain exactly two tokens.
     * @throws NumberFormatException if either of the tokens are not readable as integers.
     */
    public static TestData testDataFrom(String string) {
        String[] split = string.split(" ");
        if (!(split.length == 2)) throw new IllegalArgumentException("Wrong string content");
        return new TestData(Integer.parseInt(split[0]), Integer.parseInt(split[1])); }


    // ============================= Data array manipulation ====================================

    /** Returns a new, shuffled copy of the input array. */
    public static <T> T[] randomize(T[] data) {
        T[] x = data.clone();
        int n = data.length;
        for (int i = 0; i < n; i++) {
            int j  = i + r.nextInt(n-i);
            T t    = x[i];
            x[i]   = x[j];
            x[j]   = t;
        }
        
        return x;
    }

    /** Returns a new copy of the input with the specified percentage of elements shuffled.
     * The randomizer ensures that there are no duplicate swaps, such that the percentage
     * of shuffled elements as closely matches the percentage given as possible.
     * The integer rounding is always downwards to prevent indexing out of bounds,
     * this also means that no swaps will happen if the percentage translates to less than two elements
     * swapped.
     * In other words the number of elements swapped is the nearest integer n where
     * {@code 1 < n <= percentage / data.length}
     * @param data The array to produce a shuffled copy of.
     * @param percentage The percentage expressed as a number between 0 and 100.
     * @return An array containing the input data with the given percentage of elements randomly swapped.
     */
    public static <T> T[] randomize(T[] data, int percent) {
        if (percent < 0 || percent > 100)      // Precondition
            throw new IllegalArgumentException("Percentage must be between 0-100");

        int n        = data.length;            // Setup
        int m        = (n * percent) / 100;
        T[] x        = data.clone();
        int[] seq    = IntStream.range(0, n).toArray(); 

        for (int i = 0; i < n; i++) {          // Generate a random sequence of indices
            int k  = i + r.nextInt(n-i);
            int j  = seq[i];
            seq[i] = seq[k];
            seq[k] = j;
        }

        int a = seq[0];
        for (int i = 1; i < m; i++) {          // Randomize the output array
            int b  = seq[i];
            T t    = x[a]; 
            x[a]   = x[b];
            x[b]   = t;
            a      = b;
        }

        assert(diff(data, x) == m || m < 2);   // Postcondition
        return x;
    }

    // ============================= I/O ======================================

    public enum Category { UNITTEST, SMALL, MEDIUM, LARGE, OTHER }
    public enum Ext { IN, OUT }

    public static <T> boolean writeToFile(String filename, T[] data, Category category, Ext ext) {
        String c = data[0].getClass().getSimpleName(); 
        String fullname = 
            category.toString().toLowerCase() + 
            "/" + filename + 
            "." + c + 
            "." + ext.toString().toLowerCase();
        return writeToFile(fullname, data, e -> e.toString());
    }

    /** Writes the testdata to a file as simple plain text.
     * Each element is in one line by calling {@code toString}.
     *
     * Note that calling this with a {@code toString} method that produces multiple
     * lines will result in weird behavior.
     * The data is stored in directory named "data".
     * @param filename the desired file name.
     * @param data the list of data to write.
     * @return if there was no errors writing the data to file.
     */
    public static <T> boolean writeToFile(String filename, T[] data) {
        return writeToFile(filename, data, e -> e.toString());
    }

    /** Writes the testdata to a file as simple plain text.
     * Each element is in one line by calling the provided {@code writer} function.
     *
     * Note that calling this with a {@code writer} function that produces multiple
     * lines will result in weird behavior.
     * The data is stored in directory named "data".
     * @param filename the desired file name.
     * @param data the list of data to write.
     * @return if there was no errors writing the data to file.
     */
    public static <T> boolean writeToFile(String filename, T[] data, Function<T,String> writer) {
        File f = new File("data/" + filename);
        try {
            BufferedWriter fw = new BufferedWriter(new FileWriter(f));
            for (T td : data) {
                fw.write(writer.apply(td));
                fw.newLine();
            }   fw.close();
        } catch (IOException e) { e.printStackTrace(); return false;}
        return true;

    }

    /** Returns an {@code InputStream} of the specified data file.
     * Searches for a file in the "data" folder with the provided name.
     * If none is found, returns null and prints an error message.
     * Reads from the data folder.
     * This method is for convenient writing and testing.
     * @return The resulting TestData.
     * @param filename the file to be read from the data folder.
     */
    public static <T> InputStream streamFile(String filename) {
        try {
            return new FileInputStream(new File("data/" + filename));
        } catch(FileNotFoundException e) {
            System.out.println("No file called " + filename + " found in the data folder!");
            System.err.println("No file called " + filename + " found in the data folder!");
            return null;
        }
    }

    /** Reads data from an input stream.
     * Makes no attempt at input verification.
     * @param stream a stream of correctly formatted strings.
     * @param reader a function that creates data from a string.
     * @return an array of data objects.
     */
    @SuppressWarnings("unchecked")
    public static <T> T[] readData(InputStream stream, Function<String, T> reader) {
        List<T> l = new BufferedReader(new InputStreamReader(stream))
            .lines()
            .map(s -> reader.apply(s))
            .toList();
        return l.toArray((T[]) Array.newInstance(l.getFirst().getClass(), l.size()));
    }

    // ======================= Analysis ===========================

    /** Counts the number of different index values in the two arrays */
    public static <T> int diff(T[] either, T[] other) {
        if (either.length != other.length)
            throw new IllegalArgumentException("The arrays must be of same size.");
        int n = either.length;
        int m = 0;
        for (int i = 0; i < n; i++) {
            if (either[i] != other[i]) m++;
        }
        return m;
    }

    // ===================Manual sanity check tests===============

    public static <T> boolean readWriteTest(String filename, T[] testData, Function<String, T> reader) {
        System.out.println("Writing testdata to file");
        System.out.println(Arrays.toString(testData));
        writeToFile(filename, testData);
        System.out.println("success");

        System.out.println("Reading testdata from file");
        T[] readData = readData(streamFile(filename), reader);
        if (Arrays.equals(testData, readData)) {
            System.out.println("success, data matches");
            return true;
        } else { 
            System.out.println("fail, data does not match");
            return false;
        }
    }

    // ============== MAIN ======================
    /** Generate all desired test data to the data folder */
    public static void main(String[] args) {

        // Sanity check
        assert(readWriteTest(
            "other/sanity.TestData",
            generate(10, i -> testDataGen(0, i, 1, 1)),
            s -> testDataFrom(s)
        ));
        
        // ------------------- Data generation

        Ext in = Ext.IN;
        Ext out = Ext.OUT;

        // Unit tests

        Category ut = Category.UNITTEST;
        
        Integer[] basicseq = generate(10, i -> i);
        String a = "basicshuffle";
        writeToFile(a, randomize(basicseq), ut, in);
        writeToFile(a, basicseq, ut, out);

        TestData[] copies = generate(20, i -> new TestData(i, i/2));
        String b = "stable";
        writeToFile(b, randomize(copies), ut, in);
        writeToFile(b, copies, ut, out);

        String[] strings = generate(10, () -> randomString(10));
        String c = "strings";
        writeToFile(c, randomize(strings), ut, in);
        writeToFile(c, strings, ut, out);

        String[] stringsprefix = generate(10, () -> "prefix" + randomString(10));
        String d = "stringsprefix";
        writeToFile(d, randomize(stringsprefix), ut, in);
        writeToFile(d, stringsprefix, ut, out);

    }

}
