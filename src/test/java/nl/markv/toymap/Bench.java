package nl.markv.toymap;

import org.openjdk.jmh.Main;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@State(Scope.Benchmark)
public class Bench {

    private final static int n = 10_000_000;
    private List<Integer> integers;

    public static void main(String[] args) throws IOException {
        Main.main(args);
    }

    @Setup(Level.Invocation)
    public void setup() {
        integers = new ArrayList<>();
        for (int i = 0; i < n; i += 2) {
            integers.add(i);
        }
        for (int i = 0; i < n; i += 4) {
            integers.add(i);
        }
    }

    @Benchmark
    @Fork(value = 3, warmups = 1)
    @BenchmarkMode(Mode.AverageTime)
    public void integerSequence() {
        ToySet.from(integers);
        for (int i = 0; i < n; i += 2) {
            assert integers.contains(i);
            assert ! integers.contains(i + 1);
        }
    }
}
