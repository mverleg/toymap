package nl.markv.toymap;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.ThreadSafe;
import java.util.HashMap;

@ThreadSafe
public class ToyMap<K, V> {

    //private final HashMap<K, V> fakeTmp;  //TODO @mark: TEMPORARY! REMOVE THIS!

    private final int bucketCnt;
    @Nonnull
    //TODO @mark: is this better than recomputing?
    private final int[] hashes;
    @Nonnull
    private final K[] keys;
    @Nonnull
    private final V[] values;
    //TODO @mark: does it make sense to store keys and values in the same array (for cache locality)?
    //TODO @mark: does not work for primitives though, and needs casting

    private ToyMap(@Nonnull int[] hashes, @Nonnull K[] keys, @Nonnull V[] values) {
        this.bucketCnt = hashes.length;
        assert keys.length == bucketCnt;
        assert values.length == bucketCnt;
        this.hashes = hashes;
        this.keys = keys;
        this.values = values;
    }

    //TODO @mark: optimize this: balance size (larger wastes memory and also hurts cache locality)
    //TODO @mark: java one uses 75%, but has a special bucketing scheme, while https://www.youtube.com/watch?v=td0h7cv4cc0 says <50% for double hashing
    private int determineCapacity(int elementCount) {
        throw new IllegalStateException("find a prime close to 2*elementCount");
    }

    //TODO @mark: optimize this: there are some benefits to using something smart here, like DOS resistance
    //TODO @mark: but skipping that can be several times faster on simple types with cheap hashcodes
    private int bucket(int hashCode) {
        return hashCode % this.bucketCnt;
    }

    //TODO @mark: also note there is a bit of tension between having capacity prime vs power of two,
    //TODO @mark: since prime prevents hash collisions, whereas power of two has much faster modulo
}
