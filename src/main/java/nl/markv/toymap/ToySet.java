package nl.markv.toymap;

import org.jetbrains.annotations.NotNull;

import javax.annotation.concurrent.ThreadSafe;
import java.util.Collection;
import java.util.Iterator;

@ThreadSafe
public class ToySet<K> implements Iterable<K> {

    //private final HashMap<K, V> fakeTmp;  //TODO @mark: TEMPORARY! REMOVE THIS!

    private final int elemCount;
    private final int bucketCnt;
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

    public static <K> ToySet<K> from(Collection<@NotNull K> input) {
        var n = determineInitialCapacity(input.size());
        int[] hashes = new int[n];
        Object[] keys = new Object[n];
        //TODO @mark:
        //noinspection unchecked
        return (ToySet<K>) new ToySet<>(input.size(), hashes, keys);
    }

    public boolean contains(K lookupKey) {
        throw new IllegalStateException();  //TODO @mark:
    }

    public boolean isEmpty() {
        return this.elemCount == 0;
    }

    public int size() {
        return this.elemCount;
    }

    @NotNull
    @Override
    public Iterator<K> iterator() {
        throw new UnsupportedOperationException();  //TODO @mark:
    }

    //TODO @mark: optimize this: balance size (larger wastes memory and also hurts cache locality)
    //TODO @mark: java one uses 75%, but has a special bucketing scheme, while https://www.youtube.com/watch?v=td0h7cv4cc0 says <50% for double hashing
    private static int determineInitialCapacity(int elementCount) {
        return 2 * elementCount;
    }

    //TODO @mark: optimize this: there are some benefits to using something smart here, like DOS resistance
    //TODO @mark: but skipping that can be several times faster on simple types with cheap hashcodes
    private int bucket(int hashCode, int collisionCount) {
        return hashCode % this.bucketCnt;
    }

    //TODO @mark: also note there is a bit of tension between having capacity prime vs power of two,
    //TODO @mark: since prime prevents hash collisions, whereas power of two has much faster modulo
}
