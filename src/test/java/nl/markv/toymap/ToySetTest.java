package nl.markv.toymap;

import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

class ToySetTest {
    @Test
    public void emptySet() {
        ToySet<Integer> set = ToySet.from(Collections.emptyList());
        assert set.isEmpty();
        assert set.size() == 0;
        assert !set.contains(0);
        assert !set.contains(1);
    }

    @Test
    public void singletonSet() {
        ToySet<Integer> set = ToySet.from(Collections.singleton(1));
        assert !set.isEmpty();
        assert set.size() == 1;
        assert !set.contains(0);
        assert set.contains(1);
        assert !set.contains(2);
    }

    @Test
    public void singletonFromDuplicatesSet() {
        ToySet<Integer> set = ToySet.from(List.of(1, 1, 1));
        assert !set.isEmpty();
        assert set.size() == 1;
        assert !set.contains(0);
        assert set.contains(1);
        assert !set.contains(2);
    }

    @Test
    public void distinctValuesSet() {
        ToySet<Integer> set = ToySet.from(List.of(1, 2, 3));
        assert !set.isEmpty();
        assert set.size() == 3;
        assert !set.contains(0);
        assert set.contains(1);
        assert set.contains(2);
        assert set.contains(3);
        assert !set.contains(4);
    }

    @Test
    public void duplicateValuesSet() {
        ToySet<Integer> set = ToySet.from(List.of(1, 2, 3, 1, 2, 3));
        assert !set.isEmpty();
        assert set.size() == 3;
        assert !set.contains(0);
        assert set.contains(1);
        assert set.contains(2);
        assert set.contains(3);
        assert !set.contains(4);
    }

    //TODO @mark: somehow add a test that forces a hash collision
}