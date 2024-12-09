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

/** A collection of functions to handle data objects
 *
 *  All methods depending on randomness use a final static Random objecy by default,
 *  however, another instance can be given as a parameter.
 *
 *  Functions are written to be as generic as possible.
 *
 *  The main categories of functions are:
 *  - Data generation functions.
 *  - Data array manipulation functions.
 *  - I/O functions.
 *  - Helper / analysis functions.
 *
 *  Further, there is a file I/O read-write test function.
 *
 *  Finally, the main methods is used to specify all the desired data files for the current project.
 *
 *  When using this class for writing data to disk, it is recommended to use the local enums
 *  Cat and Ext, to specify the category of data (corresponding to an existing directory),
 *  and the extension (currently only "in" or "out" to match kattis/codegrade logic).
 *  It is easier to use if they are statically imported (e.g. {@code import static data.Handler.Cat}).
 *
 */
public class Handler {
    private static final long DEFAULT_SEED = 298092841098572l;
    public static final Random RANDOM     = new Random(DEFAULT_SEED);

    public enum Cat { UNITTEST, SMALL, MEDIUM, LARGE, OTHER }
    public enum Ext { IN, OUT }

    // ============================== Data generation ===================================

    /** Generates a array of elements from the generator function.
     * The sorted output if the generator function intrinsically generates output of higher
     * value relative to the integer input.
     * @param n the number of elements.
     * @param generator a supplier function.
     * @return an array of elements.
     */
    @SuppressWarnings("unchecked")
    public static <T> T[] generate(int n, Function<Integer, T> generator) {
        @SuppressWarnings("rawtypes") Class c = generator.apply(0).getClass();
        return (T[]) IntStream.range(0, n).boxed()
            .map(i -> generator.apply(i))
            .toArray(i -> (T[]) Array.newInstance(c, i));

    }

    /** Generates a sorted array of elements from the supplier function
     * Useful for making lists of random or constant elements.
     * @param n the number of elements.
     * @param supplier a supplier function.
     * @param <T> Must be comparable, as the array is sorted before being returned.
     * @return an array of comparable elements.
     */
    @SuppressWarnings("unchecked")
    public static <T extends Comparable<? super T>> T[] generate(int n, Supplier<T> supplier) {
        @SuppressWarnings("rawtypes") Class c = supplier.get().getClass();
        return (T[]) Stream
            .generate(supplier)
            .limit(n)
            .sorted()
            .toArray(i -> (T[]) Array.newInstance(c, i));

    }

    /** Creates a random alphanumeric string of the specified length */
    public static String randomString(int length, Random r) {
        final char[] chars = "0123456789abcdefghijklmnopqrstuvxyzABCDEFGHIJKLMNOPQRSTUVXYZ".toCharArray();
        final StringBuilder b = new StringBuilder(length);
        for (int i = 0; i < length; i++) b.append(chars[r.nextInt(chars.length)]);
        return b.toString();
    }

    /** Creates a random alphanumeric string of the specified length */
    public static String randomString(int length) { return randomString(length, RANDOM); }


    // ============================= Data array manipulation ====================================

    /** Returns a new, shuffled copy of the input array. */
    public static <T> T[] randomize(T[] data, Random r) {
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

    /** Returns a new, shuffled copy of the input array. */
    public static <T> void inPlaceRandomize(T[] x) {
        int n = x.length;
        for (int i = 0; i < n; i++) {
            int j  = i + RANDOM.nextInt(n-i);
            T t    = x[i];
            x[i]   = x[j];
            x[j]   = t;
        }
        
    }


    /** Returns a new, shuffled copy of the input array. */
    public static <T> T[] randomize(T[] data) { return randomize(data, RANDOM); }

    /** Returns a new copy of the input with the specified percentage of elements shuffled.
     * The randomizer ensures that there are no duplicate swaps, such that the percentage
     * of shuffled elements as closely matches the percentage given as possible.
     * The integer rounding is always downwards to prevent indexing out of bounds,
     * this also means that no swaps will happen if the percentage translates to less than two 
     * elements swapped.
     * In other words the number of elements swapped is the nearest integer n where
     * {@code 1 < n <= percentage / data.length}
     * @param data The array to produce a shuffled copy of.
     * @param percentage The percentage expressed as a number between 0 and 100.
     * @param the source of randomness.
     * @return An array containing the input data with the given percentage of elements randomly swapped.
     */
    public static <T> T[] randomize(T[] data, int percent, Random r) {
        if (percent < 0 || percent > 100)      // Precondition
            throw new IllegalArgumentException("Percentage must be between 0-100");

        int n      = data.length;              // Setup
        int m      = (n * percent) / 100;
        T[] x      = data.clone();
        int[] seq  = IntStream.range(0, n).toArray(); 

        for (int i = 0; i < n; i++) {          // Generate a random sequence of indices
            int k  = i + r.nextInt(n - i);
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
    public static <T> T[] randomize(T[] data, int percent) { return randomize(data, percent, RANDOM); }

    // ============================= I/O ======================================


    /** Writes the testdata to a file as simple plain text.
     * Each element is in one line by calling {@code toString}.
     *
     * Note that calling this with a {@code toString} method that produces multiple
     * lines will result in weird behavior.
     * The data is stored in directory named "data".
     * @param filename the desired file name.
     * @param data the list of data to write.
     * @param category the directory where this data should be stored.
     * @param ext the relevant extension.
     * @return if there was no errors writing the data to file.
     */
    public static <T> boolean writeToFile(String filename, T[] data, Cat category, Ext ext) {
        return writeToFile(filename, data, category, ext, e -> e.toString());
    }

    /** Writes the testdata to a file as simple plain text.
     * Each element is in one line by applying the writer function.
     *
     * Note that calling this with a writer function that produces multiple
     * lines will result in weird behavior.
     * The data is stored in directory named "data".
     * @param filename the desired file name.
     * @param data the list of data to write.
     * @param category the directory where this data should be stored.
     * @param ext the relevant extension.
     * @return if there was no errors writing the data to file.
     */
    public static <T> boolean writeToFile(String filename, T[] data, Cat category, Ext ext, Function<T,String> writer) {
        String c = data[0].getClass().getSimpleName(); 
        String fullname = 
            category.toString().toLowerCase() + 
            "/" + filename + 
            "." + c + 
            "." + ext.toString().toLowerCase();
        return writeToFile(fullname, data, writer);
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
     * @param reader a function that creates a new data object from a string.
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

    /** Checks if data can be written and read from disk. */
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
            generate(10, i -> new TestData(RANDOM.nextInt(), i)),
            TestData::from
        ));
        
        // ------------------- Data generation

        Ext in = Ext.IN;
        Ext out = Ext.OUT;

        // Unit tests data specification exempler

        Cat ut = Cat.UNITTEST;
        
        String a = "basicshuffle";
        Integer[] basicseq = generate(10, i -> i);
        writeToFile(a, randomize(basicseq), ut, in);
        writeToFile(a, basicseq, ut, out);

        String b = "stable";
        TestData[] copies = generate(20, i -> new TestData(i, i/2));
        writeToFile(b, randomize(copies), ut, in);
        writeToFile(b, copies, ut, out);

        String c = "strings";
        String[] strings = generate(10, () -> randomString(10));
        writeToFile(c, randomize(strings), ut, in);
        writeToFile(c, strings, ut, out);

        String d = "stringsprefix";
        String[] stringsprefix = generate(10, () -> "prefix" + randomString(10));
        writeToFile(d, randomize(stringsprefix), ut, in);
        writeToFile(d, stringsprefix, ut, out);

    }

}
