package nl.markv.toymap;

import org.openjdk.jmh.Main;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Bench {

    private final static int n = 10_000_000;

    @State(Scope.Benchmark)
    public static class BenchState {
        private List<Integer> integerSequence;
        @Param({"builtin", "toyset"})
        public String hashImpl;
        @Param({"integerSequence"})
        public String listType;

        @Setup(Level.Invocation)
        public void setup(){
            integerSequence = new ArrayList<>();
            for (int i = 0; i < 2 * n; i += 2) {
                integerSequence.add(i);
            }
            for (int i = 0; i < 2 * n; i += 4) {
                integerSequence.add(i);
            }
        }
    }

    public static void main(String[] args) throws IOException {
        Main.main(args);
    }

    @Benchmark
    @Fork(value = 1, warmups = 1)
    @Warmup(iterations = 1)
    @Measurement(iterations = 3)
    @BenchmarkMode(Mode.AverageTime)
    public void integerSequence(BenchState state, Blackhole blackhole) {
        List<?> list;
        if ("integerSequence".equals(state.listType)) {
            list = state.integerSequence;
        } else {
            throw new IllegalStateException("listType = " + state.listType);
        }
        Set<?> set;
        if ("builtin".equals(state.hashImpl)) {
            set = new HashSet<>(list);
        } else if ("toyset".equals(state.hashImpl)) {
            set = ToySet.from(list);
        } else {
            throw new IllegalStateException("hashImpl = " + state.hashImpl);
        }
        for (int i = 0; i < 2 * n; i += 2) {
            assert set.contains(i);
            assert ! set.contains(i + 1);
        }
    }
}
