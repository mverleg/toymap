package nl.markv.toymap;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.BitSet;

public class PrimeTest {

    @Disabled
    @Test
    public void hardcodedPrimesMatchGenerated0() {
        int primesFound = 1;
        int[] computedPrimes = new int[2<<24];
        computedPrimes[0] = 2;
        outer: for (int nr = 3; nr < Integer.MAX_VALUE - 2; nr += 2) {
            //System.out.println("trying " + i);
            for (int divisor : computedPrimes) {
                //System.out.println("try " + i + " / " + divisor);
                if (divisor == 0 || divisor * divisor > nr) {
                    break;
                }
                if (nr % divisor == 0) {
                    continue outer;
                }
            }
            computedPrimes[primesFound++] = nr;
        }
        System.out.println("found " + primesFound + " primes");
    }

    @Test
    public void hardcodedPrimesMatchGenerated() {
        // Use Sieve but skip indices for numbers divisible by 2, 3 and 5,
        // so iterate in steps of 30 starting from 7.
        int max = 1000;
        BitSet isComposite = new BitSet(max / 2);
        isComposite.set(0);
        int sqrtMax = (int) Math.ceil(Math.sqrt(max) / 2.);
        System.out.println("sqrtMax = " + sqrtMax);
        for (int outer = 0; outer < sqrtMax; outer++) {
            if (!isComposite.get(outer)) {
                System.out.println("found a prime: " + (1 + 2 * outer));
                for (int inner = 2 * outer; inner < max; inner += 2) {
                    if (!isComposite.get(inner)) {
                        System.out.println("marking " + (1 + 2 * inner) + " as composite because multiple of " + (1 + 2 * outer));
                    }
                    isComposite.set(inner);
                }
            } else {
                System.out.println("skipping, not a prime: " + (1 + 2 * outer));
            }
        }
        System.out.println("built-in prime: " + 2);
        int primeCnt = 1;
        for (int ix = 0; ix < max; ix++) {
            if (!isComposite.get(ix)) {
                System.out.println("prime: " + (1 + 2 * ix));
                primeCnt++;
            }
        }
        System.out.println("found " + primeCnt + " primes");
        assert primeCnt == 168;
//        int primeCnt = max - isComponsite.cardinality();
//        System.out.println("found " + primeCnt + " primes");
//        assert primeCnt == 2147483647;
    }
}
