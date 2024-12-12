package experiments;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Result
 */
public abstract sealed class Result {
    public static final String PRINTFORMAT = "%-20s %10s %7s %13s %20s %13s %13s %13s";
    private String title = "N/A";
    public enum Key { MEANTIME, SDEVTIME, MEANRESULT, SDEVRESULT, PARAMETER, REPETITIONS, RUNS; }

    public abstract Result removeKeys(Collection<Key> ks);
    public abstract Result put(Key k, Double d);

    public void setTitle(String title) { this.title = title; }
    public String getName()            { return title; }
    public void print()                { System.out.println(this); }

    public static String resultHeaders() {
        return String.format( PRINTFORMAT,
            "# title", "param", "runs", "reps", "meanTime", "sdevTime", "meanResult", "sdevResult");
    }
}

final class SingleResult extends Result {
    private Map<Key, Double> r;

    public SingleResult(String withTitle)        { r = new HashMap<>();      setTitle(withTitle); }
    public Result put(Key k, Double d)           { r.put(k, d);                      return this; }
    public Result removeKeys(Collection<Key> ks) { for (Key key : ks) r.remove(key); return this; }

    public String toString() {
        return String.format(
            PRINTFORMAT, 
            getName(),
            r.containsKey(Key.PARAMETER)   ? String.format("%,9.0f",  r.get(Key.PARAMETER))   : "",
            r.containsKey(Key.RUNS)        ? String.format("%,4.0f",  r.get(Key.RUNS))        : "",
            r.containsKey(Key.REPETITIONS) ? String.format("%,9.0f",  r.get(Key.REPETITIONS)) : "",
            r.containsKey(Key.MEANTIME)    ? String.format("%,10.0f", r.get(Key.MEANTIME))    : "",
            r.containsKey(Key.SDEVTIME)    ? String.format("%,6.1f",  r.get(Key.SDEVTIME))    : "",
            r.containsKey(Key.MEANRESULT)  ? String.format("%,10.0f", r.get(Key.MEANRESULT))  : "",
            r.containsKey(Key.SDEVRESULT)  ? String.format("%,6.1f",  r.get(Key.SDEVRESULT))  : ""
        );
    }
}

final class ParameterizedResult extends Result {
    public final List<SingleResult> result;

    public ParameterizedResult(List<SingleResult> result)                               { this.result = result; }
    public Result removeKeys(Collection<Key> ks) { for (SingleResult r : result) r.removeKeys(ks); return this; }
    public Result put(Key k, Double d)           { for (SingleResult r : result) r.put(k, d);      return this; }

    public String toString() {
        return result.stream()
                     .map(r -> r.toString())
                     .reduce((s, t) -> String.format("%s%n%s", s, t))
                     .orElse("");
    }
}
