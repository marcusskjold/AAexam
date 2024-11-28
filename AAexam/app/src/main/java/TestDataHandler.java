import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

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
    public static List<TestData> seqGen(int from, int to) {
        ArrayList<TestData> x = new ArrayList<>(to-from);
        for (int i = from; i < to; i++) {
            x.add(new TestData(r.nextInt(), i));
        }
        return x;
    }

    // ============================= HANDLING ====================================

    /** Returns a new, shuffled copy of the input list. */
    public static List<TestData> randomizeData(List<TestData> t) {
        List<TestData> x = new ArrayList<>(t);
        Collections.shuffle(x, r);
        return x;
    }

    // ============================= I/O ======================================


    /** Writes the testdata to a file as simple plain text.
     * The data is stored in a data folder.
     * @param name the desired file name.
     * @param data the list of {@code TestData} to write.
     * @return if there was no errors writing the data to file.
     */
    public static boolean writeToFile(String filename, List<TestData> data) {
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
    public static List<TestData> readFile(String filename) {
        try {
            InputStream i = new FileInputStream(new File("data/" + filename));
            System.out.println("File found!");
            return readData(i);
        } catch(FileNotFoundException e) {
            System.out.println("File not found");
            return null;
        }
    }

    // ==================================== Helper methods ===========================

    /** Reads TestData from an input stream. */
    public static List<TestData> readData(InputStream stream) {
        return new BufferedReader(new InputStreamReader(stream))
            .lines()
            .map(s -> testDataFrom(s))
            .toList();
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
        System.out.println("Writing testdata to file");
        List<TestData> genData = seqGen(-2, 4);
        System.out.println(genData);
        writeToFile("test.data", genData);
        System.out.println("Reading testdata to file");
        List<TestData> readData = readFile("test.data");
        System.out.println(readData);
        assert(genData.equals(readData));
        List<TestData> randomized = randomizeData(readData);
        System.out.println(randomized);
        assert(!randomized.equals(readData));
        seqGen(2, 2);
    }
}
