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
        int max = Integer.MAX_VALUE;
        BitSet isComposite = new BitSet(max / 2);
        isComposite.set(0);
        int sqrtMax = (int) Math.ceil(Math.sqrt(max) / 2.);
        for (int outer = 0; outer < sqrtMax; outer++) {
            if (!isComposite.get(outer)) {
                for (int inner = 3 * outer + 1; inner <= max / 2 + 1; inner += 2 * outer + 1) {
                    if (!isComposite.get(inner)) {
                    }
                    //assert (2 * inner + 1) % (2 * outer + 1) == 0;  //TODO @mark:
                    isComposite.set(inner);
                }
            } else {
            }
        }
        //System.out.println("built-in prime: " + 2);
        int primeCnt = 1;
        for (int ix = 0; ix < max / 2; ix++) {
            if (!isComposite.get(ix)) {
                //System.out.println("prime: " + (1 + 2 * ix));
                primeCnt++;
            }
        }
        System.out.println("found " + primeCnt + " primes");
    }
}
