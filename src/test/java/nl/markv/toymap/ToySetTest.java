package nl.markv.toymap;

import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

class ToySetTest {
    @Test
    public void emptySet() {
        ToySet<Integer> set = ToySet.from(Collections.emptyList());
        assert set.isEmpty();
        assert set.size() == 0;
        assert !set.contains(0);
        assert !set.contains(1);
        assert set.collisionCount() == 0;
    }

    @Test
    public void singletonSet() {
        ToySet<Integer> set = ToySet.from(Collections.singleton(1));
        assert !set.isEmpty();
        assert set.size() == 1;
        assert !set.contains(0);
        assert set.contains(1);
        assert !set.contains(2);
        assert set.collisionCount() == 0;
    }

    @Test
    public void singletonFromDuplicatesSet() {
        ToySet<Integer> set = ToySet.from(List.of(1, 1, 1));
        assert !set.isEmpty();
        assert set.size() == 1;
        assert !set.contains(0);
        assert set.contains(1);
        assert !set.contains(2);
        assert set.collisionCount() == 0;
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
        assert set.collisionCount() <= 2;
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
        assert set.collisionCount() <= 2;
    }

    static class BadHashObj {
        private final int value;

        BadHashObj(int value) {
            this.value = value;
        }

        @Override
        public boolean equals(Object other) {
            return this.value == ((BadHashObj) other).value;
        }

        @Override
        public int hashCode() {
            return 1;
        }
    }

    @Test
    public void poorHashCode() {
        ToySet<BadHashObj> set = ToySet.from(List.of(new BadHashObj(1), new BadHashObj(2), new BadHashObj(3)));
        assert set.size() == 3;
        assert set.collisionCount() == 2;
    }

    @Test
    public void collisionsWithSimilarPostfix() {
        ToySet<Integer> set = ToySet.from(List.of(100_000, 200_000, 300_000, 400_000, 500_000, 600_000, 700_000));
        assert set.size() == 7;
        assert set.collisionCount() == 0;
    }

    //TODO @mark: somehow add a test that forces a hash collision
}