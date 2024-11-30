import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Random;
import java.util.stream.IntStream;

/**
 * DataGenerator
 * Generates test data for sorting algorithms.
 */
public class TestDataHandler {
    private static final long DEFAULT_SEED = 298092841098572l;
    private static final Random r = new Random(DEFAULT_SEED);

    // ============================== GENERATORS ===================================

    /** Generate a list of {@code TestData} with sequentially increasing values and random ID's.
     * The data returned is sorted by value. There is no guarantee that ID's are not reused.
     * In any case, there will be no duplicate elements.
     * @param from The lowest value.
     * @param to the highest value.
     * @throws IllegalArgumentException if {@code from} is larger than {@code to}.
     */
    public static TestData[] seqGen(int from, int to) {
        int n = to-from;
        TestData[] x = new TestData[n];
        for (int i = 0; i < n; i++) {
            x[i] = new TestData(r.nextInt(), i+from);
        }
        return x;
    }

    // ============================= HANDLING ====================================

    /** Returns a new, shuffled copy of the input list. */
    public static <T extends Comparable<? super T>> T[] randomizeData(T[] data) {
        T[] x = data.clone();
        int n = data.length;
        for (int i = 0; i < n; i++) {
            T t    = x[i];
            int j  = r.nextInt(n);
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
     * {@code 1 < n < percentage / data.length}
     * @param data The array to produce a shuffled copy of.
     * @param percentage The percentage expressed as a number between 0 and 100.
     * @return An array containing the input data with the given percentage of elements randomly swapped.
     */
    public static <T extends Comparable<? super T>> T[] randomizeData(T[] data, int percentage) {
        if (percentage < 0 || percentage > 100) 
            throw new IllegalArgumentException("Percentage must be between 0-100");
        int n        = data.length;
        int m        = (n * percentage) / 100;
        T[] x = data.clone();
        int[] seq    = IntStream.range(0, n).toArray(); // Generate a random sequence of indices
        
        for (int i = n-1; i > 0; i--) {
            int k  = r.nextInt(i+1);
            int j  = seq[i];
            seq[i] = seq[k];
            seq[k] = j;
        }

        int a = seq[0];
        for (int i = 1; i < m; i++) {      // Randomize the output array
            int b  = seq[i];
            T t    = x[a]; // Swap
            x[a]   = x[b];
            x[b]   = t;
            a      = b;
        }
        return x; 
    }

    // ============================= I/O ======================================


    /** Writes the testdata to a file as simple plain text.
     * The data is stored in a data folder.
     * @param name the desired file name.
     * @param data the list of {@code TestData} to write.
     * @return if there was no errors writing the data to file.
     */
    public static boolean writeToFile(String filename, TestData[] data) {
        File f = new File("data/" + filename);
        try {
            BufferedWriter fw = new BufferedWriter( new FileWriter(f));
            for (TestData td : data) {
                fw.write(td.id() + " " + td.value());
                fw.newLine();
            }
            fw.close();
        } catch (IOException e) { e.printStackTrace(); return false;}
        return true;
    }

    /** Reads a file specifying TestData as plain text.
     * Reads from the data folder.
     * @return The resulting TestData
     * @param filename the file to be read from the data folder.
     */
    public static TestData[] readFile(String filename) {
        try {
            InputStream i = new FileInputStream(new File("data/" + filename));
            return readData(i);
        } catch(FileNotFoundException e) {
            return null;
        }
    }

    // ==================================== Helper methods ===========================
    
    /** Counts the number of different index values in the two arrays */
    public static <T extends Comparable<? super T>> int arrayDifference(T[] either, T[] other) {
        if (either.length != other.length)
            throw new IllegalArgumentException("The arrays must be of same size.");
        int n = either.length;
        int m = 0;
        for (int i = 0; i < n; i++) {
            if (either[i] != other[i]) m++;
        }
        return m;

    }

    /** Reads TestData from an input stream. */
    public static TestData[] readData(InputStream stream) {
        return (TestData[]) new BufferedReader(new InputStreamReader(stream))
            .lines()
            .map(s -> testDataFrom(s))
            .toArray(TestData[]::new);
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

    // ========================== MAIN ===========================

    /** Manual sanity check tests*/
    public static void main(String[] args) {
        //System.out.println("Writing testdata to file");
        TestData[] genData = seqGen(-2, 4);
        //System.out.println(genData);
        writeToFile("test.data", genData);
        //System.out.println("Reading testdata to file");
        TestData[] readData = readFile("test.data");
        //System.out.println(readData);
        assert(genData.equals(readData));
        TestData[] randomized = randomizeData(readData);
        //System.out.println(randomized);
        assert(!randomized.equals(readData));
        seqGen(2, 2);
        genData = seqGen(0, 100);
        int n = genData.length;
        randomized = randomizeData(genData, 45);
        int m = 0;
        for (int i = 0; i < n; i++) {
            if (genData[i] != randomized[i]) m++;
        }
        System.out.println(n + " " + m + " ~ " + ((double) m / (double) n));

    }
}
