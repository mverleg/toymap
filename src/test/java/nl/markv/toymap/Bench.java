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
        private List<Integer> integers;
        @Param({"builtin", "toyset"})
        public String hashImpl;

        @Setup(Level.Invocation)
        public void setup(){
            integers = new ArrayList<>();
            for (int i = 0; i < 2 * n; i += 2) {
                integers.add(i);
            }
            for (int i = 0; i < 2 * n; i += 4) {
                integers.add(i);
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
        Set<Integer> set;
        if ("builtin".equals(state.hashImpl)) {
            set = new HashSet<>(state.integers);
        } else if ("toyset".equals(state.hashImpl)) {
            set = ToySet.from(state.integers);
        } else {
            throw new IllegalStateException("hashImpl = " + state.hashImpl);
        }
        for (int i = 0; i < 2 * n; i += 2) {
            assert set.contains(i);
            assert ! set.contains(i + 1);
        }
    }
}
