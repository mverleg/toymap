package nl.markv.toymap;

import org.junit.jupiter.api.Test;
import org.openjdk.jmh.infra.Blackhole;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

//TODO @mark: TEMPORARY! REMOVE THIS!
public class Bench {

    private final static int n = 10_000_000;

    private List<Integer> integerSequence = new ArrayList<>();

    @Test
    public void integerSequence(Blackhole blackhole) {
        List<?> list = integerSequence;
        Set<?> set;
        if ("builtin".equals(state.hashImpl)) {
            set = new HashSet<>(list);
        } else if ("toyset".equals(state.hashImpl)) {
            set = ToySet.from(list);
        } else {
            throw new IllegalStateException("hashImpl = " + state.hashImpl);
        }
        integerSequenceCheck(set);
        blackhole.consume(set);
    }

    void integerDuplicatesCheck(Set<?> set) {
        for (int i = 0; i < 2 * n; i += 2) {
            check(set.contains(i));
            check(!set.contains(i + 1));
        }
    }

    void integerSequenceCheck(Set<?> set) {
        for (int i : state.negativeIntegerDuplicates) {
            if (!set.contains(i)) {
                System.out.println("i=" + i);  //TODO @mark: TEMPORARY! REMOVE THIS!
            }
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
