package nl.markv.toymap;

import org.junit.jupiter.api.Test;

import java.util.BitSet;

public class PrimeTest {

    @Test
    public void hardcodedPrimesMatchGenerated() {
        int[] primes = calculatePrimes();
        System.out.println("count = " + primes.length);
        System.out.println("first = " + primes[0]);
        System.out.println("second = " + primes[2]);
        System.out.println("second to last = " + primes[primes.length - 2]);
        System.out.println("last = " + primes[primes.length - 1]);
        int prev = primes[0];
        System.out.print(prev);
        for (int i = 1; i < primes.length; i++) {
            if (primes[i] < 0.95 * prev) {
                prev = primes[i];
                System.out.print(", " + prev);
            }
        }
    }

    public int[] calculatePrimes() {
        int max = Integer.MAX_VALUE;
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
        int primeCnt = max / 2 - isComposite.cardinality() + 2;
        int[] primes = new int[primeCnt];
        int primeIx = primeCnt - 1;
        primes[0] = (1<<31) - 1;
        primes[primeIx--] = 2;
        for (int ix = 0; ix < max / 2; ix++) {
            if (!isComposite.get(ix)) {
                primes[primeIx--] = 1 + 2 * ix;
            }
        }
        return primes;
    }
}
