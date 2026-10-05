package assignment2;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class MyLinkedListTest {

    @Test
    void addAndGetWork() {
        MyLinkedList list = new MyLinkedList();
        list.add(10);
        list.add(20);
        list.add(30);

        assertEquals(10, list.get(0));
        assertEquals(30, list.get(2));
        assertEquals(3, list.size());
    }

    @Test
    void addByIndexWorksForFirstMiddleAndLast() {
        MyLinkedList list = new MyLinkedList();
        list.add(10);
        list.add(30);
        list.add(1, 20);
        list.add(0, 5);
        list.add(list.size(), 40);

        assertEquals(5, list.get(0));
        assertEquals(20, list.get(2));
        assertEquals(40, list.get(list.size() - 1));
    }

    @Test
    void removeWorks() {
        MyLinkedList list = new MyLinkedList();
        list.add(1);
        list.add(2);
        list.add(3);

        assertEquals(1, list.remove(0));
        assertEquals(3, list.remove(list.size() - 1));
        assertEquals(1, list.size());
        assertEquals(2, list.get(0));
    }

    @Test
    void duplicateValuesWork() {
        MyLinkedList list = new MyLinkedList();
        list.add(4);
        list.add(4);

        assertTrue(list.contains(4));
        list.remove(0);
        assertTrue(list.contains(4));
    }

    @Test
    void invalidIndexesThrowException() {
        MyLinkedList list = new MyLinkedList();

        assertThrows(IndexOutOfBoundsException.class, () -> list.get(0));
        assertThrows(IndexOutOfBoundsException.class, () -> list.remove(0));
        assertThrows(IndexOutOfBoundsException.class, () -> list.add(-1, 5));
        assertThrows(IndexOutOfBoundsException.class, () -> list.add(1, 5));
    }

    @Test
    void randomDataMatchesArrayList() {
        MyLinkedList actual = new MyLinkedList();
        ArrayList<Integer> expected = new ArrayList<>();
        Random random = new Random(42);

        for (int i = 0; i < 300; i++) {
            int value = random.nextInt(1000);
            actual.add(value);
            expected.add(value);
        }

        for (int i = 0; i < expected.size(); i++) {
            assertEquals(expected.get(i).intValue(), actual.get(i));
        }
    }
}
