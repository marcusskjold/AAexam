package experiments;

import java.util.function.IntFunction;
import java.util.function.ToIntFunction;
import java.util.function.UnaryOperator;

/** <p>An experiment is a collection of input, a run function, and a setup function.</p>
 *
 * <p>Experiments should either have some randomness in the setup function or have an input generation function
 * that makes use of the integer input, to avoid the compiler caching the result of the experiment.
 * Input is by default a generator, such that it takes the repetition counter as input (from 0 .. repetitions)</p>
 *
 * <p>Overloaded constructors create identity functions to simplify experiment definition code.</p>
 */
public record Experiment<T>(IntFunction<T> inputGen, ToIntFunction<T> runFun, UnaryOperator<T> setupFun) { 
    public T input(int i)   { return inputGen.apply(i); }
    public T setup(T input) { return setupFun.apply(input);}
    public int run(T input) { return runFun.applyAsInt(input); }

    public Experiment(T input, ToIntFunction<T> runFun, UnaryOperator<T> setupFun) { this(i -> input, runFun, setupFun); }
    public Experiment(T input, ToIntFunction<T> runFun)                            { this(i -> input, runFun, t -> t); }
    public Experiment(IntFunction<T> inputGen, ToIntFunction<T> runFun)            { this(inputGen, runFun, t -> t); }

    }
