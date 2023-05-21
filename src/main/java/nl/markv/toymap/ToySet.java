package nl.markv.toymap;

import org.jetbrains.annotations.NotNull;

import javax.annotation.concurrent.ThreadSafe;
import java.util.Collection;

@ThreadSafe
public class ToySet<K> {

    //private final HashMap<K, V> fakeTmp;  //TODO @mark: TEMPORARY! REMOVE THIS!

    private final int bucketCnt;
    //TODO @mark: is this better than recomputing?
    private final int @NotNull [] hashes;
    private final @NotNull K @NotNull [] keys;
    //@NotNull
    //private final V[] values;
    //TODO @mark: does it make sense to store keys and values in the same array (for cache locality)?
    //TODO @mark: does not work for primitives though, and needs casting

    private ToySet(int[] hashes, @NotNull K @NotNull [] keys) {
        this.bucketCnt = hashes.length;
        assert keys.length == bucketCnt;
        this.hashes = hashes;
        this.keys = keys;
    }

    public <J> ToySet<J> from(Collection<@NotNull J> input) {

    }

    public boolean contains(K lookupKey) {
        throw new IllegalStateException();  //TODO @mark:
    }

    //TODO @mark: optimize this: balance size (larger wastes memory and also hurts cache locality)
    //TODO @mark: java one uses 75%, but has a special bucketing scheme, while https://www.youtube.com/watch?v=td0h7cv4cc0 says <50% for double hashing
    private int determineCapacity(int elementCount) {
        throw new IllegalStateException("find a prime close to 2*elementCount");
    }

    //TODO @mark: optimize this: there are some benefits to using something smart here, like DOS resistance
    //TODO @mark: but skipping that can be several times faster on simple types with cheap hashcodes
    private int bucket(int hashCode, int collisionCount) {
        return hashCode % this.bucketCnt;
    }

    //TODO @mark: also note there is a bit of tension between having capacity prime vs power of two,
    //TODO @mark: since prime prevents hash collisions, whereas power of two has much faster modulo
}
