package utility;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LimitedLinkedHashMapTest {

    @Test
    void shouldAddOneMappingToMaxCapacityFour() {

        LimitedLinkedHashMap<Integer, String> target = new LimitedLinkedHashMap<>(4);

        target.put(1, "First mapping");

        assertEquals(4, target.maxCapacity());
        assertEquals(1, target.size());
        assertEquals("First mapping", target.get(1));
    }


    @Test
    void shouldAddTwoMappingsToMaxCapacityFour() {

        LimitedLinkedHashMap<Integer, String> target = new LimitedLinkedHashMap<>(4);

        target.put(1, "First mapping");
        target.put(2, "Second mapping");

        assertEquals(4, target.maxCapacity());
        assertEquals(2, target.size());
        assertEquals("First mapping", target.get(1));
        assertEquals("Second mapping", target.get(2));
    }


    @Test
    void shouldAddThreeMappingsToMaxCapacityFour() {

        LimitedLinkedHashMap<Integer, String> target = new LimitedLinkedHashMap<>(4);

        target.put(1, "First mapping");
        target.put(2, "Second mapping");
        target.put(3, "Third mapping");

        assertEquals(4, target.maxCapacity());
        assertEquals(3, target.size());
        assertEquals("First mapping", target.get(1));
        assertEquals("Second mapping", target.get(2));
        assertEquals("Third mapping", target.get(3));
    }


    @Test
    void shouldAddFourMappingsToMaxCapacityFour() {

        LimitedLinkedHashMap<Integer, String> target = new LimitedLinkedHashMap<>(4);

        target.put(1, "First mapping");
        target.put(2, "Second mapping");
        target.put(3, "Third mapping");
        target.put(4, "Fourth mapping");

        assertEquals(4, target.maxCapacity());
        assertEquals(4, target.size());
        assertEquals("First mapping", target.get(1));
        assertEquals("Second mapping", target.get(2));
        assertEquals("Third mapping", target.get(3));
        assertEquals("Fourth mapping", target.get(4));
    }


    @Test
    void shouldNotAddFiveMappingsToMaxCapacityFour() {

        LimitedLinkedHashMap<Integer, String> target = new LimitedLinkedHashMap<>(4);

        assertThrows(IllegalStateException.class,
                () -> {
                    target.put(1, "First mapping");
                    target.put(2, "Second mapping");
                    target.put(3, "Third mapping");
                    target.put(4, "Fourth mapping");
                    target.put(5, "Fifth mapping");
                });
    }


    @Test
    void shouldAddAllMappingsOfSpecifiedMapOfSizeTwoToMaxCapacityFourThatAlreadyContainsTwoMappings() {

        LimitedLinkedHashMap<Integer, String> target = new LimitedLinkedHashMap<>(4);
        target.put(1, "First mapping");
        target.put(2, "Second mapping");
        LimitedLinkedHashMap<Integer, String> source = new LimitedLinkedHashMap<>(2);
        source.put(3, "Third mapping");
        source.put(4, "Fourth mapping");

        target.putAll(source);

        assertEquals(4, target.maxCapacity());
        assertEquals(4, target.size());
        assertEquals("First mapping", target.get(1));
        assertEquals("Second mapping", target.get(2));
        assertEquals("Third mapping", target.get(3));
        assertEquals("Fourth mapping", target.get(4));
    }


    @Test
    void shouldNotAddAllMappingsOfSpecifiedMapOfSizeThreeToMaxCapacityFourThatAlreadyContainsTwoMappings() {

        LimitedLinkedHashMap<Integer, String> target = new LimitedLinkedHashMap<>(4);
        target.put(1, "First mapping");
        target.put(2, "Second mapping");
        LimitedLinkedHashMap<Integer, String> source = new LimitedLinkedHashMap<>(3);
        source.put(3, "Third mapping");
        source.put(4, "Fourth mapping");
        source.put(5, "Fifth mapping");


        assertThrows(IllegalStateException.class,
                () -> {
                    target.putAll(source);
                });
    }


    @Test
    void shouldAddAllMappingsOfSpecifiedMapOfSizeThreeWithARepeatKeyToMaxCapacityFourThatAlreadyContainsTwoMappings() {

        LimitedLinkedHashMap<Integer, String> target = new LimitedLinkedHashMap<>(4);
        target.put(1, "First mapping");
        target.put(2, "Second mapping");
        LimitedLinkedHashMap<Integer, String> source = new LimitedLinkedHashMap<>(3);
        source.put(3, "Third mapping");
        source.put(4, "Fourth mapping");
        source.put(2, "Fifth mapping");

        target.putAll(source);

        assertEquals(4, target.maxCapacity());
        assertEquals(4, target.size());
        assertEquals("First mapping", target.get(1));
        assertEquals("Fifth mapping", target.get(2));
        assertEquals("Third mapping", target.get(3));
        assertEquals("Fourth mapping", target.get(4));
    }


    @Test
    void shouldAddAbsentMappingToMaxCapacityFourThatAlreadyContainsThreeMappings() {

        LimitedLinkedHashMap<Integer, String> target = new LimitedLinkedHashMap<>(4);
        target.put(1, "First mapping");
        target.put(2, "Second mapping");
        target.put(3, "Third mapping");

        target.putIfAbsent(4, "Fourth mapping");

        assertEquals(4, target.maxCapacity());
        assertEquals(4, target.size());
        assertEquals("First mapping", target.get(1));
        assertEquals("Second mapping", target.get(2));
        assertEquals("Third mapping", target.get(3));
        assertEquals("Fourth mapping", target.get(4));
    }


    @Test
    void shouldNotAddAbsentMappingToMaxCapacityFourThatAlreadyContainsFourMappings() {

        LimitedLinkedHashMap<Integer, String> target = new LimitedLinkedHashMap<>(4);
        target.put(1, "First mapping");
        target.put(2, "Second mapping");
        target.put(3, "Third mapping");
        target.put(4, "Fourth mapping");

        assertThrows(IllegalStateException.class,
                () -> {
                    target.putIfAbsent(5, "Fifth mapping");
                });
    }


    @Test
    void shouldAddAbsentMappingToMaxCapacityFourThatAlreadyContainsFourMappingsWhereAbsentMappingReplacesNullValue() {

        LimitedLinkedHashMap<Integer, String> target = new LimitedLinkedHashMap<>(4);
        target.put(1, "First mapping");
        target.put(2, "Second mapping");
        target.put(3, null);
        target.put(4, "Fourth mapping");

        target.putIfAbsent(3, "Fifth mapping");

        assertEquals(4, target.maxCapacity());
        assertEquals(4, target.size());
        assertEquals("First mapping", target.get(1));
        assertEquals("Second mapping", target.get(2));
        assertEquals("Fifth mapping", target.get(3));
        assertEquals("Fourth mapping", target.get(4));
    }


    @Test
    void shouldComputeAndAddAbsentMappingToMaxCapacityFourThatAlreadyContainsThreeMappings() {

        LimitedLinkedHashMap<Integer, String> target = new LimitedLinkedHashMap<>(4);
        target.put(1, "First mapping");
        target.put(2, "Second mapping");
        target.put(3, "Third mapping");

        target.computeIfAbsent(4, s -> String.valueOf(s));

        assertEquals(4, target.maxCapacity());
        assertEquals(4, target.size());
        assertEquals("First mapping", target.get(1));
        assertEquals("Second mapping", target.get(2));
        assertEquals("Third mapping", target.get(3));
        assertEquals("4", target.get(4));
    }


    @Test
    void shouldNotComputeAndAddAbsentMappingToMaxCapacityFourThatAlreadyContainsFourMappings() {

        LimitedLinkedHashMap<Integer, String> target = new LimitedLinkedHashMap<>(4);
        target.put(1, "First mapping");
        target.put(2, "Second mapping");
        target.put(3, "Third mapping");
        target.put(4, "Fourth mapping");

        assertThrows(IllegalStateException.class,
                () -> {
                    target.computeIfAbsent(5, s -> String.valueOf(s));
                });
    }


    @Test
    void shouldComputeAndAddAbsentMappingToMaxCapacityFourThatAlreadyContainsFourMappingsWhereAbsentMappingReplacesNullValue() {

        LimitedLinkedHashMap<Integer, String> target = new LimitedLinkedHashMap<>(4);
        target.put(1, "First mapping");
        target.put(2, "Second mapping");
        target.put(3, null);
        target.put(4, "Fourth mapping");

        target.computeIfAbsent(3, s -> String.valueOf(s));

        assertEquals(4, target.maxCapacity());
        assertEquals(4, target.size());
        assertEquals("First mapping", target.get(1));
        assertEquals("Second mapping", target.get(2));
        assertEquals("3", target.get(3));
        assertEquals("Fourth mapping", target.get(4));
    }


    @Test
    void shouldComputeAndNotAddAbsentMappingToMaxCapacityFourThatAlreadyContainsFourMappingsWhereAbsentMappingComputesToNull() {

        LimitedLinkedHashMap<Integer, String> target = new LimitedLinkedHashMap<>(4);
        target.put(1, "First mapping");
        target.put(2, "Second mapping");
        target.put(3, "Third mapping");
        target.put(4, "Fourth mapping");

        target.computeIfAbsent(5, s -> null);

        assertEquals(4, target.maxCapacity());
        assertEquals(4, target.size());
        assertEquals("First mapping", target.get(1));
        assertEquals("Second mapping", target.get(2));
        assertEquals("Third mapping", target.get(3));
        assertEquals("Fourth mapping", target.get(4));
    }


    @Test
    void shouldMergeMapContainingNewKeyWithMaxCapacityFourThatAlreadyContainsThreeMappings() {

        LimitedLinkedHashMap<Integer, String> target = new LimitedLinkedHashMap<>(4);
        target.put(1, "First mapping");
        target.put(2, "Second mapping");
        target.put(3, "Third mapping");

        target.merge(4, "Fourth mapping", (v1, v2) -> v1 + " " + v2);

        assertEquals(4, target.maxCapacity());
        assertEquals(4, target.size());
        assertEquals("First mapping", target.get(1));
        assertEquals("Second mapping", target.get(2));
        assertEquals("Third mapping", target.get(3));
        assertEquals("Fourth mapping", target.get(4));
    }


    @Test
    void shouldNotMergeMapContainingNewKeyWithMaxCapacityFourThatAlreadyContainsFourMappings() {

        LimitedLinkedHashMap<Integer, String> target = new LimitedLinkedHashMap<>(4);
        target.put(1, "First mapping");
        target.put(2, "Second mapping");
        target.put(3, "Third mapping");
        target.put(4, "Fourth mapping");

        assertThrows(IllegalStateException.class,
                () -> {
                    target.merge(5, "Fifth mapping", (v1, v2) -> v1 + " " + v2);
                });
    }


    @Test
    void shouldMergeMapContainingRepeatKeyWithMaxCapacityFourThatAlreadyContainsFourMappings() {

        LimitedLinkedHashMap<Integer, String> target = new LimitedLinkedHashMap<>(4);
        target.put(1, "First mapping");
        target.put(2, "Second mapping");
        target.put(3, "Third mapping");
        target.put(4, "Fourth mapping");

        target.merge(3, "Fifth mapping", (v1, v2) -> v1 + " + " + v2);

        assertEquals(4, target.maxCapacity());
        assertEquals(4, target.size());
        assertEquals("First mapping", target.get(1));
        assertEquals("Second mapping", target.get(2));
        assertEquals("Third mapping + Fifth mapping", target.get(3));
        assertEquals("Fourth mapping", target.get(4));
    }


    @Test
    void shouldMergeAndRemoveMapContainingRepeatKeyWithMaxCapacityFourThatAlreadyContainsFourMappingsWhereRepeatKeyMapsToNull() {

        LimitedLinkedHashMap<Integer, String> target = new LimitedLinkedHashMap<>(4);
        target.put(1, "First mapping");
        target.put(2, "Second mapping");
        target.put(3, "Third mapping");
        target.put(4, "Fourth mapping");

        target.merge(3, "Fifth mapping", (v1, v2) -> null);

        assertEquals(4, target.maxCapacity());
        assertEquals(3, target.size());
        assertEquals("First mapping", target.get(1));
        assertEquals("Second mapping", target.get(2));
        assertEquals("Fourth mapping", target.get(4));
    }


    @Test
    void shouldRetrieveMaximumCapacity() {

        LimitedLinkedHashMap<Integer, String> target = new LimitedLinkedHashMap<>(4);

        assertEquals(4, target.maxCapacity());
    }
}
