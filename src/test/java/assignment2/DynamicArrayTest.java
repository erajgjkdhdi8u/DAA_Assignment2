package assignment2;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class DynamicArrayTest {

    @Test
    void addAndGetWork() {
        DynamicArray array = new DynamicArray();
        array.add(10);
        array.add(20);
        array.add(30);

        assertEquals(10, array.get(0));
        assertEquals(30, array.get(2));
        assertEquals(3, array.size());
    }

    @Test
    void addByIndexWorksForFirstAndLastIndex() {
        DynamicArray array = new DynamicArray();
        array.add(10);
        array.add(20);
        array.add(0, 5);
        array.add(array.size(), 30);

        assertEquals(5, array.get(0));
        assertEquals(30, array.get(array.size() - 1));
    }

    @Test
    void removeWorks() {
        DynamicArray array = new DynamicArray();
        array.add(1);
        array.add(2);
        array.add(3);

        assertEquals(2, array.remove(1));
        assertEquals(2, array.size());
        assertEquals(3, array.get(1));
    }

    @Test
    void duplicateValuesWork() {
        DynamicArray array = new DynamicArray();
        array.add(7);
        array.add(7);
        array.add(7);

        assertTrue(array.contains(7));
        assertEquals(7, array.remove(0));
        assertTrue(array.contains(7));
    }

    @Test
    void invalidIndexesThrowException() {
        DynamicArray array = new DynamicArray();

        assertThrows(IndexOutOfBoundsException.class, () -> array.get(0));
        assertThrows(IndexOutOfBoundsException.class, () -> array.remove(0));
        assertThrows(IndexOutOfBoundsException.class, () -> array.add(-1, 5));
        assertThrows(IndexOutOfBoundsException.class, () -> array.add(1, 5));
    }

    @Test
    void randomDataMatchesArrayList() {
        DynamicArray actual = new DynamicArray();
        ArrayList<Integer> expected = new ArrayList<>();
        Random random = new Random(42);

        for (int i = 0; i < 500; i++) {
            int value = random.nextInt(1000);
            actual.add(value);
            expected.add(value);
        }

        for (int i = 0; i < expected.size(); i++) {
            assertEquals(expected.get(i).intValue(), actual.get(i));
        }
    }
}
