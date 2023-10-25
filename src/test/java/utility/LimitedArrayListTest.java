package utility;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class LimitedArrayListTest {

    @Test
    void shouldAddOneElementToMaxCapacityFour() {

        LimitedArrayList<String> target = new LimitedArrayList<>(4);

        target.add("First element");

        assertEquals(1, target.size());
        assertEquals("First element", target.get(0));
    }


    @Test
    void shouldAddTwoElementsToMaxCapacityFour() {

        LimitedArrayList<String> target = new LimitedArrayList<>(4);

        target.add("First element");
        target.add("Second element");

        assertEquals(2, target.size());
        assertEquals("First element", target.get(0));
        assertEquals("Second element", target.get(1));
    }


    @Test
    void shouldAddThreeElementsToMaxCapacityFour() {

        LimitedArrayList<String> target = new LimitedArrayList<>(4);

        target.add("First element");
        target.add("Second element");
        target.add("Third element");

        assertEquals(3, target.size());
        assertEquals("First element", target.get(0));
        assertEquals("Second element", target.get(1));
        assertEquals("Third element", target.get(2));
    }


    @Test
    void shouldAddFourElementsToMaxCapacityFour() {

        LimitedArrayList<String> target = new LimitedArrayList<>(4);

        target.add("First element");
        target.add("Second element");
        target.add("Third element");
        target.add("Fourth element");

        assertEquals(4, target.size());
        assertEquals("First element", target.get(0));
        assertEquals("Second element", target.get(1));
        assertEquals("Third element", target.get(2));
        assertEquals("Fourth element", target.get(3));
    }


    @Test
    void shouldNotAddFiveElementsToMaxCapacityFour() {

        LimitedArrayList<String> target = new LimitedArrayList<>(4);

        assertThrows(IllegalStateException.class,
                () -> {
                    target.add("First element");
                    target.add("Second element");
                    target.add("Third element");
                    target.add("Fourth element");
                    target.add("Fifth element");
                });
    }


    @Test
    void shouldAddOneElementToMaxCapacityOneHundred() {

        LimitedArrayList<String> target = new LimitedArrayList<>(100);

        target.add("First element");

        assertEquals(1, target.size());
        assertEquals("First element", target.get(0));
    }


    @Test
    void shouldAddFourthElementAtIndexOneToMaxCapacityFourThatAlreadyContainsThreeElements() {

        LimitedArrayList<String> target = new LimitedArrayList<>(4);
        target.add("First element");
        target.add("Second element");
        target.add("Third element");

        target.add(1, "Fourth element");

        assertEquals(4, target.size());
        assertEquals("First element", target.get(0));
        assertEquals("Fourth element", target.get(1));
        assertEquals("Second element", target.get(2));
        assertEquals("Third element", target.get(3));
    }


    @Test
    void shouldNotAddFifthElementAtIndexOneToMaxCapacityFourThatAlreadyContainsFourElements() {

        LimitedArrayList<String> target = new LimitedArrayList<>(4);

        target.add("First element");
        target.add("Second element");
        target.add("Third element");
        target.add("Fourth element");

        assertThrows(IllegalStateException.class,
                () -> {
                    target.add(1, "Fifth element");
                });
    }


    @Test
    void shouldAddAllElementsOfSpecifiedCollectionOfSizeOneToMaxCapacityFour() {

        LimitedArrayList<String> target = new LimitedArrayList<>(4);
        ArrayList<String> source = new ArrayList<>();
        source.add("First element");

        target.addAll(source);

        assertEquals(1, target.size());
        assertEquals("First element", target.get(0));
    }


    @Test
    void shouldAddAllElementsOfSpecifiedCollectionOfSizeTwoToMaxCapacityFour() {

        LimitedArrayList<String> target = new LimitedArrayList<>(4);
        ArrayList<String> source = new ArrayList<>();
        source.add("First element");
        source.add("Second element");

        target.addAll(source);

        assertEquals(2, target.size());
        assertEquals("First element", target.get(0));
        assertEquals("Second element", target.get(1));
    }


    @Test
    void shouldAddAllElementsOfSpecifiedCollectionOfSizeThreeToMaxCapacityFour() {

        LimitedArrayList<String> target = new LimitedArrayList<>(4);
        ArrayList<String> source = new ArrayList<>();
        source.add("First element");
        source.add("Second element");
        source.add("Third element");

        target.addAll(source);

        assertEquals(3, target.size());
        assertEquals("First element", target.get(0));
        assertEquals("Second element", target.get(1));
        assertEquals("Third element", target.get(2));
    }


    @Test
    void shouldAddAllElementsOfSpecifiedCollectionOfSizeFourToMaxCapacityFour() {

        LimitedArrayList<String> target = new LimitedArrayList<>(4);
        ArrayList<String> source = new ArrayList<>();
        source.add("First element");
        source.add("Second element");
        source.add("Third element");
        source.add("Fourth element");

        target.addAll(source);

        assertEquals(4, target.size());
        assertEquals("First element", target.get(0));
        assertEquals("Second element", target.get(1));
        assertEquals("Third element", target.get(2));
        assertEquals("Fourth element", target.get(3));
    }


    @Test
    void shouldNotAddAllElementsOfSpecifiedCollectionOfSizeFiveToMaxCapacityFour() {

        LimitedArrayList<String> target = new LimitedArrayList<>(4);
        ArrayList<String> source = new ArrayList<>();
        source.add("First element");
        source.add("Second element");
        source.add("Third element");
        source.add("Fourth element");
        source.add("Fifth element");

        assertThrows(IllegalStateException.class,
                () -> {
                    target.addAll(source);
                });
    }


    @Test
    void shouldAddAllElementsOfSpecifiedCollectionOfSizeOneAtIndexOneToMaxCapacityFourThatAlreadyContainsTwoElements() {

        LimitedArrayList<String> target = new LimitedArrayList<>(4);
        target.add("First element");
        target.add("Second element");
        ArrayList<String> source = new ArrayList<>();
        source.add("Third element");

        target.addAll(1, source);

        assertEquals(3, target.size());
        assertEquals("First element", target.get(0));
        assertEquals("Third element", target.get(1));
        assertEquals("Second element", target.get(2));
    }


    @Test
    void shouldAddAllElementsOfSpecifiedCollectionOfSizeTwoAtIndexOneToMaxCapacityFourThatAlreadyContainsTwoElements() {

        LimitedArrayList<String> target = new LimitedArrayList<>(4);
        target.add("First element");
        target.add("Second element");
        ArrayList<String> source = new ArrayList<>();
        source.add("Third element");
        source.add("Fourth element");

        target.addAll(1, source);

        assertEquals(4, target.size());
        assertEquals("First element", target.get(0));
        assertEquals("Third element", target.get(1));
        assertEquals("Fourth element", target.get(2));
        assertEquals("Second element", target.get(3));
    }


    @Test
    void shouldNotAddAllElementsOfSpecifiedCollectionOfSizeThreeAtIndexOneToMaxCapacityFourThatAlreadyContainsTwoElements() {

        LimitedArrayList<String> target = new LimitedArrayList<>(4);
        target.add("First element");
        target.add("Second element");
        ArrayList<String> source = new ArrayList<>();
        source.add("Third element");
        source.add("Fourth element");
        source.add("Fifth element");

        assertThrows(IllegalStateException.class,
                () -> {
                    target.addAll(source);
                });
    }


    @Test
    void shouldNotAllowMaxCapacityZero() {

        assertThrows(IllegalArgumentException.class,
                () -> {
                    LimitedArrayList<String> target = new LimitedArrayList<>(0);
                });
    }


    @Test
    void shouldNotAllowMaxCapacityNegative() {

        assertThrows(IllegalArgumentException.class,
                () -> {
                    LimitedArrayList<String> target = new LimitedArrayList<>(-1);
                });
    }


    @Test
    void shouldNotAllowTrimToSize() {

        LimitedArrayList<String> target = new LimitedArrayList<>(4);
        target.add("First element");

        assertThrows(UnsupportedOperationException.class,
                () -> {
                    target.trimToSize();
                });
    }


    @Test
    void shouldNotAllowEnsureCapacity() {

        LimitedArrayList<String> target = new LimitedArrayList<>(4);
        target.add("First element");

        assertThrows(UnsupportedOperationException.class,
                () -> {
                    target.ensureCapacity(6);
                });
    }


    @Test
    void shouldRetrieveMaximumCapacity() {

        LimitedArrayList<String> target = new LimitedArrayList<>(4);

        assertEquals(4, target.maxCapacity());
    }
}
