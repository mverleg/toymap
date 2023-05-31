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

public class Bench {

    private final static int n = 10_000_000;

    @State(Scope.Benchmark)
    public static class BenchState {
        private List<Integer> integerSequence;
        private List<Integer> negativeIntegerDuplicates;
        private List<Double> encodedDateDoubles;
        private List<Double> constantNumber;
        private List<String> sentences;

        @Param({"builtin", "toyset"})
        public String hashImpl;
        @Param({"integerSequence", "integerDuplicates", "encodedDateDoubles", "constantNumber", "sentences"})
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
                    int val = -Math.abs(i << 16);
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

            String[] colors = new String[]{"Red", "Green", "Blue", "Yellow", "Purple", "Cyan", "White", "Black",};
            sentences = new ArrayList<>();
            for (int i = 0; i < n; i++) {
                StringBuilder text = new StringBuilder();
                int n = i;
                for (int j = 0; j < 16; j++) {
                    text.append(colors[n % colors.length]);
                    n = n >> 3;
                }
                sentences.add(text.toString());
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
    public void bench(BenchState state, Blackhole blackhole) {
        List<?> list;
        BiConsumer<Set<?>, BenchState> test;
        if ("integerSequence".equals(state.listType)) {
            list = state.integerSequence;
            test = this::integerSequenceCheck;
        } else if ("integerDuplicates".equals(state.listType)) {
            list = state.negativeIntegerDuplicates;
            test = this::integerDuplicatesCheck;
        } else if ("encodedDateDoubles".equals(state.listType)) {
            list = state.encodedDateDoubles;
            test = this::encodedDateDoublesCheck;
        } else if ("constantNumber".equals(state.listType)) {
            list = state.constantNumber;
            test = this::constantNumberCheck;
        } else if ("sentences".equals(state.listType)) {
            list = state.sentences;
            test = this::sentencesCheck;
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
        test.accept(set, state);
        blackhole.consume(set);
    }

    void integerSequenceCheck(Set<?> set, BenchState state) {
        for (int i = 0; i < 2 * n; i += 2) {
            check(set.contains(i));
            check(!set.contains(i + 1));
        }
    }

    void integerDuplicatesCheck(Set<?> set, BenchState state) {
        for (int i : state.negativeIntegerDuplicates) {
            check(set.contains(i));
        }
        for (int i = 1; i < n; i++) {
            check(!set.contains(i));
        }
    }

    private void encodedDateDoublesCheck(Set<?> set, BenchState state) {
        for (int i = 0; i <= 9_999_999; i++) {
            check(!set.contains(i));
            if (i < state.encodedDateDoubles.size()) {
                Double val = state.encodedDateDoubles.get(i);
                check(set.contains(val));
            }
        }
    }

    private void constantNumberCheck(Set<?> set, BenchState state) {
        for (int i = 0; i < n; i++) {
            check(!set.contains((double) i));
        }
        check(set.contains(Math.PI));
    }

    private void sentencesCheck(Set<?> set, BenchState state) {
        for (String sentence : state.sentences) {
            check(set.contains(sentence));
        }
    }

    void check(boolean isCheckTrue) {
        if (!isCheckTrue) {
            throw new IllegalStateException();
        }
    }
}
