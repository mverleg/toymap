package nl.markv.toymap;

import org.openjdk.jmh.Main;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;

public class IntegerSeqBench {

    private final static int n = 10_000_000;

    public static void main(String[] args) throws IOException {
        Main.main(args);
    }

    @State(Scope.Benchmark)
    public static class BenchState {
        private List<Integer> integerSequence;

        @Setup(Level.Invocation)
        public void setup() {
            integerSequence = new ArrayList<>();
            for (int i = 0; i < 2 * n; i += 2) {
                integerSequence.add(i);
            }
            for (int i = 0; i < 2 * n; i += 4) {
                integerSequence.add(i);
            }
        }
    }

    @Benchmark
    @Fork(value = 1, warmups = 1, jvmArgsAppend = "-Xmx8g")
    @Warmup(iterations = 1, time = 1)
    @Measurement(iterations = 1, time = 1)
    @BenchmarkMode(Mode.AverageTime)
//    @BenchmarkMode(Mode.AverageTime)
    public void bench(BenchState state, Blackhole blackhole) {
        List<?> list = state.integerSequence;
        Set<?> set = new HashSet<>(list);
        integerSequenceCheck(set, state);
        blackhole.consume(set);
    }

    void integerSequenceCheck(Set<?> set, BenchState state) {
        for (int i = 0; i < 2 * n; i += 2) {
            check(set.contains(i));
            check(!set.contains(i + 1));
        }
    }

    void check(boolean isCheckTrue) {
        if (!isCheckTrue) {
            throw new IllegalStateException();
        }
    }
}
