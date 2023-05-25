package nl.markv.toymap;

import org.junit.jupiter.api.Test;

import java.util.BitSet;

public class PrimeTest {

    @Test
    public void hardcodedPrimesMatchGenerated() {
        int max = Integer.MAX_VALUE;
        BitSet isComposite = new BitSet(max / 2);
        isComposite.set(0);
        int sqrtMax = (int) Math.ceil(Math.sqrt(max) / 2.);
        for (int outer = 0; outer < sqrtMax; outer++) {
            if (!isComposite.get(outer)) {
                for (int inner = 3 * outer + 1; inner <= max / 2 + 1; inner += 2 * outer + 1) {
                    isComposite.set(inner);
                }
            }
        }
        int primeCnt = 1;
        for (int ix = 0; ix < max / 2; ix++) {
            if (!isComposite.get(ix)) {
                primeCnt++;
            }
        }
        System.out.println("found " + primeCnt + " primes");
    }
}
