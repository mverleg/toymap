package nl.markv.toymap;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.VisibleForTesting;

import java.util.Collection;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;

//@ThreadSafe
public class ToySet<K> implements Iterable<K>, Set<K> {
    /// Factor (>1) of extra capacity that is initially provided to prevent collisions
    private static final double MIN_OVERCAPACITY_FACTOR = 1.6;
    // Factor (>MIN) of extra capacity beyond which the data is compressed
    private static final double MAX_OVERCAPACITY_FACTOR = 2.2;
    static {
        //noinspection ConstantValue
        assert MIN_OVERCAPACITY_FACTOR > 1.0;
        //noinspection ConstantValue
        assert MAX_OVERCAPACITY_FACTOR >= MIN_OVERCAPACITY_FACTOR;
    }

    private final int elemCount;
    private final int bucketCnt;   //TODO @mark: is this useful to store separately?
    //TODO @mark: is this better than recomputing? test
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

    /**
     * The collection in the argument must not change during this method.
     */
    public static <K> ToySet<K> from(Collection<@NotNull K> inputs) {
        ToySet<@NotNull K> largeSet = fromCollection(inputs);
        if ((((double) largeSet.bucketCnt) / largeSet.elemCount) <= MAX_OVERCAPACITY_FACTOR) {
            return largeSet;
        }
        return decreaseCapacity(largeSet);
    }

    public static <K> ToySet<K> fromCollection(Collection<@NotNull K> inputs) {
        int sizeBeforeDedup = inputs.size();
        int bucketCntBeforeDedup = determineInitialCapacity(sizeBeforeDedup);
        int[] hashes = new int[bucketCntBeforeDedup];
        Object[] keys = new Object[bucketCntBeforeDedup];
        int valueCnt = 0;
        for (var inp : inputs) {
            Objects.requireNonNull(inp, "cannot contain null values");
            int insertHash = rehash(inp.hashCode());
            for (int collisionCount = 0; collisionCount < bucketCntBeforeDedup; collisionCount++) {
                int bucket = chooseBucket(insertHash, collisionCount, bucketCntBeforeDedup);
                if (hashes[bucket] == 0) {
                    valueCnt++;
                    hashes[bucket] = insertHash;
                    keys[bucket] = inp;
                    break;
                }
                if (inp.equals(keys[bucket])) {
                    break;
                }
            }
        }
        assert valueCnt <= sizeBeforeDedup;
        assert sizeBeforeDedup == inputs.size(): "input changed during the method";
        //noinspection unchecked
        return (ToySet<K>) new ToySet<>(valueCnt, hashes, keys);
    }

    private static <K> ToySet<K> decreaseCapacity(ToySet<K> largeSet) {
        int bucketCntAfterDedup = determineInitialCapacity(largeSet.elemCount);
        int[] hashes = new int[bucketCntAfterDedup];
        Object[] keys = new Object[bucketCntAfterDedup];
        for (int i = 0; i < largeSet.hashes.length; i++) {
            int insertHash = largeSet.hashes[i];
            if (insertHash == 0) {
                continue;
            }
            for (int collisionCount = 0; collisionCount < bucketCntAfterDedup; collisionCount++) {
                int bucket = chooseBucket(insertHash, collisionCount, bucketCntAfterDedup);
                assert collisionCount < 100;  //TODO @mark: TEMPORARY! REMOVE THIS!
                @NotNull K inp = (K) largeSet.keys[i];
                if (((Integer)inp)==-438829056 || ((Integer)inp) == -855736320) {
                    System.out.println("match " + inp + " dup " + collisionCount);
                }
                if (hashes[bucket] == 0) {
                    assert insertHash != 0;  //TODO @mark: TEMPORARY! REMOVE THIS!
                    hashes[bucket] = insertHash;
                    keys[bucket] = inp;
                    break;
//                } else {
//                    assert hashes[bucket] == insertHash;  //TODO @mark: properly implement the scenario where the collision bucket is occupied by a non-hash-collision
                }
                assert !inp.equals(keys[bucket]): "there should be no more duplicates while downscaling";
            }
        }
        //noinspection unchecked
        return (ToySet<K>) new ToySet<>(largeSet.elemCount, hashes, keys);
    }

    public boolean containsKey(@NotNull K lookupKey) {
        if (this.bucketCnt == 0) {
            return false;
        }
        int lookupHash = rehash(lookupKey.hashCode());
        for (int collisionCount = 0; collisionCount < this.bucketCnt; collisionCount++) {
            int bucket = chooseBucket(lookupHash, collisionCount, this.bucketCnt);
            int bucketHash = this.hashes[bucket];
            if (bucketHash == 0) {
                return false;
            }
            if (lookupHash == bucketHash && lookupKey.equals(this.keys[bucket])) {
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
        double sizeWithLoadFactor = (elementCount + 1) * MIN_OVERCAPACITY_FACTOR;
        if (sizeWithLoadFactor >= Integer.MAX_VALUE) {
            int fallback = Prime.largestIntPrime();
            assert fallback - 1 > elementCount;
            return fallback;
        }
        return Prime.primeAbove((int) Math.round(sizeWithLoadFactor));
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
        // Remove the sign bit to get the absolute value, without having to deal with Integer.MIN_VALUE which has no absolute value.
        int unsignedTotal = (rehashCode + collisionCount) & 0x7fffffff;
        return unsignedTotal % totalBucketCnt;
    }

    public int capacity() {
        return this.bucketCnt;
    }

    //TODO @mark: also note there is a bit of tension between having capacity prime vs power of two,
    //TODO @mark: since prime prevents hash collisions, whereas power of two has much faster modulo
}
