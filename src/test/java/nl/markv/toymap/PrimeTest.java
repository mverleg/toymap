package nl.markv.toymap;

import org.junit.jupiter.api.Test;

public class PrimeTest {

    @Test
    public void hardcodedPrimesMatchSieve() {
        int primesFound = 1;
        int[] computedPrimes = new int[2<<20];
        computedPrimes[0] = 2;
        outer: for (int i = 3; i < Integer.MAX_VALUE - 2; i += 2) {
            //System.out.println("trying " + i);
            for (int divisor : computedPrimes) {
                //System.out.println("try " + i + " / " + divisor);
                if (divisor == 0) {
                    break;
                }
                if (i % divisor == 0) {
                    continue outer;
                }
            }
            computedPrimes[primesFound++] = i;
        }
        System.out.println("found " + primesFound + " primes");
    }
}
