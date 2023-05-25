package nl.markv.toymap;

import org.junit.jupiter.api.Test;

import java.util.BitSet;

public class PrimeTest {

    @Test
    public void hardcodedPrimesMatchGenerated() {
        int[] primes = calculatePrimes();
        System.out.println("count = " + primes.length);
        System.out.println("first = " + primes[0]);
        System.out.println("last = " + primes[primes.length - 1]);
    }

    public int[] calculatePrimes() {
        int max = Integer.MAX_VALUE;
//        int max = 100;
        BitSet isComposite = new BitSet(max / 2);
        isComposite.set(0);
        int sqrtMax = (int) Math.ceil(Math.sqrt(max) / 2.);
        for (int outer = 0; outer < sqrtMax; outer++) {
            if (!isComposite.get(outer)) {
                for (int inner = 3 * outer + 1; inner < max / 2; inner += 2 * outer + 1) {
                    isComposite.set(inner);
                }
            }
        }
        int primeCnt = max / 2 - isComposite.cardinality() + 1;
        int[] primes = new int[primeCnt];
        int primeIx = 0;
        primes[primeIx++] = 2;
        for (int ix = 0; ix < max / 2; ix++) {
            if (!isComposite.get(ix)) {
                primes[primeIx++] = 1 + 2 * ix;
            }
        }
        return primes;
    }
}
