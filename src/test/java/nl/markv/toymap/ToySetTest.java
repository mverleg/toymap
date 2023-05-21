package nl.markv.toymap;

import org.junit.jupiter.api.Test;

import java.util.Collections;

class ToySetTest {
    @Test
    public void emptySet() {
        ToySet<Integer> set = ToySet.from(Collections.emptyList());
        assert set.isEmpty();
        assert set.size() == 0;
        assert ! set.contains(0);
        assert ! set.contains(1);
    }
}