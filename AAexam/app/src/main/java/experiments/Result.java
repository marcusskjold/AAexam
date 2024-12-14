package experiments;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Represents the results of a single experiment.
 * A result is a collection of key-value pairs.
 * A key is a member of enum Result.Key.
 * Results of parameterized keys are represented as a list of results.
 *
 * Some methods return the object itself for a stream-like workflow. E.g.:
 * {@code exampleResult.removeKey(Key.RUNS).put(Key.MEANTIME, 0.0).toCSV()}
 */
public abstract sealed class Result {
    public static final String PRINTFORMAT = "%-20s%10s%7s%13s%20s%13s%13s%13s";
    private String title;

    public enum Key { MEANTIME, SDEVTIME, MEANRESULT, SDEVRESULT, PARAMETER, REPETITIONS, RUNS; }

    public abstract Result removeKeys(Collection<Key> ks);
    public abstract Result removeKey(Key ks);
    public abstract Result put(Key k, Double d);
    public abstract List<String> toCSV();

    public void setTitle(String title) { this.title = title; }
    public String getTitle()            { return title; }
    public void print()                { System.out.println(this); }

    public Result saveAsCSV() {
        File f = new File("data/results/" + getTitle() + ".csv");
        try (BufferedWriter fw = new BufferedWriter(new FileWriter(f))) {
            for (String s : toCSV()) { fw.write(s); fw.newLine(); }
        } catch (Exception e) { e.printStackTrace(); return null; }
        return this;
    }

    public static String resultHeaders() {
        return String.format(PRINTFORMAT,
            "# title", "param", "runs", "reps", "meanTime", "sdevTime", "meanResult", "sdevResult");
    }
}

final class SingleResult extends Result {
    private Map<Key, Double> r;

    public SingleResult(String withTitle)        { r = new HashMap<>();      setTitle(withTitle); }
    public Result put(Key k, Double d)           { r.put(k, d);                      return this; }
    public Result removeKeys(Collection<Key> ks) { for (Key key : ks) r.remove(key); return this; }
    public Result removeKey(Key k)               { r.remove(k);                      return this; }
    public double get(Key k)                     { return r.containsKey(k) ? r.get(k) : -1.0;     }

    public String toString() {
        return String.format(
            PRINTFORMAT, 
            getTitle(),
            r.containsKey(Key.PARAMETER)   ? String.format("%,9.0f",  r.get(Key.PARAMETER))   : "",
            r.containsKey(Key.RUNS)        ? String.format("%,4.0f",  r.get(Key.RUNS))        : "",
            r.containsKey(Key.REPETITIONS) ? String.format("%,9.0f",  r.get(Key.REPETITIONS)) : "",
            r.containsKey(Key.MEANTIME)    ? String.format("%,10.0f", r.get(Key.MEANTIME))    : "",
            r.containsKey(Key.SDEVTIME)    ? String.format("%,6.1f",  r.get(Key.SDEVTIME))    : "",
            r.containsKey(Key.MEANRESULT)  ? String.format("%,10.0f", r.get(Key.MEANRESULT))  : "",
            r.containsKey(Key.SDEVRESULT)  ? String.format("%,6.1f",  r.get(Key.SDEVRESULT))  : ""
        ).trim();
    }

    public List<String> toCSV() {
        
        StringBuilder sbC = new StringBuilder();
        StringBuilder sbH = new StringBuilder();
        sbH.append("TITLE");
        sbC.append(getTitle());
        for (Key k : r.keySet()) {
            sbH.append(", ");
            sbH.append(k.toString());
            sbC.append(", ");
            sbC.append(r.get(k));
        }
        return List.of(sbH.toString(),sbC.toString());
    }
}

/** Represents the results of a parameterized experiments as a list of single results.*/
final class ParameterizedResult extends Result {
    public final List<SingleResult> result;

    public ParameterizedResult(String title, List<SingleResult> results) { setTitle(title);   result = results; }
    public Result removeKeys(Collection<Key> ks) { for (SingleResult r : result) r.removeKeys(ks); return this; }
    public Result removeKey(Key k)               { for (SingleResult r : result) r.removeKey(k);   return this; }
    public Result put(Key k, Double d)           { for (SingleResult r : result) r.put(k, d);      return this; }

    public String toString() {
        return result.stream()
                     .map(r -> r.toString())
                     .reduce((s, t) -> String.format("%s%n%s", s, t))
                     .orElse("");
    }

    public List<String> toCSV() {
        List<List<String>> ls = result.stream()
                                      .map(r -> r.toCSV())
                                      .toList();
        List<String> l = new ArrayList<String>(ls.size()+1);
        l.add(ls.getFirst().getFirst());
        ls.forEach(e -> l.add(e.getLast()));
        return l;

    }
}
