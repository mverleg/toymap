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
        private List<Integer> negativeIntegerDuplicates;
        private List<Double> encodedDateDoubles;
        private List<Double> constantNumber;

        @Param({"builtin", "toyset"})
        public String hashImpl;
        @Param({"integerSequence", "integerDuplicates", "encodedDateDoubles", "constantNumber"})
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

            negativeIntegerDuplicates = new ArrayList<>();
            for (int j = 0; j < 8; j++) {
                for (int i = 0; i < n; i += 2) {
                    int val = -(i << 16);
                    negativeIntegerDuplicates.add(val);
                    negativeIntegerDuplicates.add(val);
                    negativeIntegerDuplicates.add(val);
                }
            }

            encodedDateDoubles = new ArrayList<>();
            for (int yr = 1970; yr <= 2023; yr++) {
                int mnth = 1;
                for (int mnth_len : new int[]{31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31}) {
                    mnth++;
                    for (int d = 0; d < mnth_len; d++) {
                        encodedDateDoubles.add((double) (1000 * yr + 100 * mnth + d));
                    }
                }
            }

            constantNumber = new ArrayList<>();
            for (int i = 0; i < n; i++) {
                constantNumber.add(Math.PI);
            }
        }
    }

    public static void main(String[] args) throws IOException {
        Main.main(args);
    }

    @Benchmark
    @Fork(value = 1, warmups = 1)
    @Warmup(iterations = 1, time = 1)
    @Measurement(iterations = 1, time = 1)
    //TODO @mark: time ^
    @BenchmarkMode(Mode.AverageTime)
    public void integerSequence(BenchState state, Blackhole blackhole) {
        List<?> list;
        if ("integerSequence".equals(state.listType)) {
            list = state.integerSequence;
        } else if ("integerDuplicates".equals(state.listType)) {
            list = state.negativeIntegerDuplicates;
        } else if ("encodedDateDoubles".equals(state.listType)) {
            list = state.encodedDateDoubles;
        } else if ("constantNumber".equals(state.listType)) {
            list = state.constantNumber;
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
