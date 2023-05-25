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
        // Use Sieve but skip indices for numbers divisible by 2, 3 and 5, so iterate in steps of 30.
        BitSet isComponsite = new BitSet();
        int ix = 0;
        isComponsite.set(ix++);  // 2
        isComponsite.set(ix++);  // 3
        isComponsite.set(ix++);  // 5
        for (int realNr = 0; realNr < (2<<16); realNr += 30) {
            isComponsite.set(ix) / realNr + 1;
            isComponsite.set(ix) / realNr + 7;
            isComponsite.set(ix) / realNr + 11;
            isComponsite.set(ix) / realNr + 13;
            isComponsite.set(ix) / realNr + 17;
            isComponsite.set(ix) / realNr + 19;
            isComponsite.set(ix) / realNr + 23;
            isComponsite.set(ix) / realNr + 29;
        }
        System.out.println("found " + isComponsite.cardinality() + " primes");
    }
}
