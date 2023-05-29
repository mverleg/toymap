package nl.markv.toymap;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.VisibleForTesting;

import java.util.Collection;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;

//@ThreadSafe
public class ToySet<K> implements Iterable<K>, Set<K> {

    private final int elemCount;
    private final int bucketCnt;   //TODO @mark: is this useful to store separately?
    //TODO @mark: is this better than recomputing?
    private final int @NotNull [] hashes;
    /** Use Object because we cannot properly create a generic array */
    private final @NotNull Object @NotNull [] keys;
    //@NotNull
    //private final V[] values;
    //TODO @mark: does it make sense to store keys and values in the same array (for cache locality)?
    //TODO @mark: does not work for primitives though, and needs casting

    private ToySet(int elemCount, int[] hashes, @NotNull K @NotNull [] keys) {
        this.bucketCnt = hashes.length;
        this.hashes = hashes;
        assert elemCount <= bucketCnt;
        this.elemCount = elemCount;
        assert keys.length == bucketCnt;
        this.keys = keys;
    }

    public static <K> ToySet<K> from(Collection<@NotNull K> inputs) {
        //TODO @mark: n should be the de-duplicated number?
        int n = determineInitialCapacity(inputs.size());
        int[] hashes = new int[n];
        Object[] keys = new Object[n];
        int valueCnt = 0;
        //TODO @mark: how to deal with changes to the collection during this iteration?
        for (var inp : inputs) {
            Objects.requireNonNull(inp, "cannot contain null values");
            int insertHash = rehash(inp.hashCode());
            int bucket = chooseBucket(insertHash, 0, n);
            if (bucket < 0) {
                System.out.println("insertHash = " + insertHash + " / bucket = " + bucket + " / n = " + n);  //TODO @mark: TEMPORARY! REMOVE THIS!
            }
            if (hashes[bucket] != 0) {
                if (inp.equals(keys[bucket])) {
                    continue;
                }
            }
            assert hashes[bucket] == 0;  //TODO @mark: impl collisions
            valueCnt++;
            hashes[bucket] = insertHash;
            keys[bucket] = inp;
        }
        assert valueCnt <= inputs.size();
        //noinspection unchecked
        return (ToySet<K>) new ToySet<>(valueCnt, hashes, keys);
    }

    public boolean containsKey(@NotNull K lookupKey) {
        if (this.bucketCnt == 0) {
            return false;
        }
        int lookupHash = rehash(lookupKey.hashCode());
        for (int collisionCount = 0; collisionCount < this.bucketCnt; collisionCount++) {
            int bucket = chooseBucket(lookupHash, collisionCount, this.bucketCnt);
            int bucketHash = this.hashes[bucket];
            if (lookupHash != bucketHash || bucketHash == 0) {
                return false;
            }
            if (lookupKey.equals(this.keys[bucket])) {
                return true;
            }
        }
        throw new IllegalStateException("the wholo collection was checked and everything was duplicate, which should not happen since load factor should be < 1");
    }

    public boolean isEmpty() {
        return this.elemCount == 0;
    }

    @Override
    public boolean contains(Object obj) {
        return containsKey((K) obj);
    }

    public int size() {
        return this.elemCount;
    }

    @VisibleForTesting
    public int collisionCount() {
        int collisions = 0;
        for (int i = 0; i < this.bucketCnt; i++) {
            if (this.hashes[i] != 0 && chooseBucket(this.hashes[i], 0, this.bucketCnt) != i) {
                collisions++;
            }
        }
        return collisions;
    }

    @NotNull
    @Override
    public Iterator<K> iterator() {
        throw new UnsupportedOperationException();  //TODO @mark:
    }

    @NotNull
    @Override
    public Object[] toArray() {
        throw new UnsupportedOperationException();
    }

    @NotNull
    @Override
    public <T> T[] toArray(@NotNull T[] a) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean add(K k) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean remove(Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean containsAll(@NotNull Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addAll(@NotNull Collection<? extends K> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainAll(@NotNull Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeAll(@NotNull Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    //TODO @mark: optimize this: balance size (larger wastes memory and also hurts cache locality)
    //TODO @mark: java one uses 75%, but has a special bucketing scheme, while https://www.youtube.com/watch?v=td0h7cv4cc0 says <50% for double hashing
    private static int determineInitialCapacity(int elementCount) {
        // should always be >= elementCount + 1 unless 0
        double sizeWithLoadFactor = elementCount * 1.7;
        if (sizeWithLoadFactor >= Integer.MAX_VALUE) {
            return Prime.largestIntPrime();
        }
        return Prime.primeAbove((int) Math.ceil(elementCount * 1.7));
        //TODO @mark: ^ experiment with different load factors
    }

    private static int rehash(int pureHashCode) {
        // assert !Double.isNaN(nr);
        // long longHash = Double.doubleToRawLongBits(Double.NaN);
        // int hash = (int)(longHash ^ (longHash >>> 32));
        //TODO @mark: hashcode for doubles (don't allow NaN)
        if (pureHashCode == 0) {
            return 1;
        }
        return pureHashCode;
        //TODO @mark: some bit magic for better distribution?
    }

    //TODO @mark: optimize this: there are some benefits to using something smart here, like DOS resistance
    //TODO @mark: but skipping that can be several times faster on simple types with cheap hashcodes
    //TODO @mark: also note that there is now a distinction between rehash and bucket
    private static int chooseBucket(int rehashCode, int collisionCount, int totalBucketCnt) {
        int signedTotal = rehashCode + collisionCount;
        if (signedTotal < 0 || collisionCount < 0 || totalBucketCnt < 0) {
            System.out.println("rehashCode " + rehashCode + " signedTotal is " + signedTotal + " total size " + totalBucketCnt);   //TODO @mark: TEMPORARY! REMOVE THIS!
        }
        return (signedTotal > 0 ? signedTotal : -signedTotal) % totalBucketCnt;
    }

    public int capacity() {
        return this.bucketCnt;
    }

    //TODO @mark: also note there is a bit of tension between having capacity prime vs power of two,
    //TODO @mark: since prime prevents hash collisions, whereas power of two has much faster modulo
}
