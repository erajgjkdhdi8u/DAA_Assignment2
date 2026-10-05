package assignment2;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class MinHeapTest {

    @Test
    void peekAndExtractWork() {
        MinHeap heap = new MinHeap();
        heap.insert(7);
        heap.insert(2);
        heap.insert(10);
        heap.insert(1);

        assertEquals(1, heap.peekMin());
        assertEquals(1, heap.extractMin());
        assertEquals(2, heap.peekMin());
    }

    @Test
    void emptyHeapThrowsException() {
        MinHeap heap = new MinHeap();

        assertThrows(IllegalStateException.class, heap::peekMin);
        assertThrows(IllegalStateException.class, heap::extractMin);
    }

    @Test
    void oneElementWorks() {
        MinHeap heap = new MinHeap();
        heap.insert(5);

        assertTrue(heap.isValidHeap());
        assertEquals(5, heap.extractMin());
        assertEquals(0, heap.size());
    }

    @Test
    void duplicatesWork() {
        MinHeap heap = new MinHeap();
        heap.insert(3);
        heap.insert(3);
        heap.insert(3);

        assertEquals(3, heap.extractMin());
        assertEquals(3, heap.extractMin());
        assertEquals(3, heap.extractMin());
    }

    @Test
    void heapPropertyHoldsAfterEveryInsertAndExtract() {
        MinHeap heap = new MinHeap();
        Random random = new Random(42);

        for (int i = 0; i < 500; i++) {
            heap.insert(random.nextInt(10_000));
            assertTrue(heap.isValidHeap());
        }

        while (heap.size() > 0) {
            heap.extractMin();
            assertTrue(heap.isValidHeap());
        }
    }

    @Test
    void extractMinReturnsSortedValues() {
        int[] values = {8, 1, 9, 3, 3, 7, 2, 5};
        int[] expected = Arrays.copyOf(values, values.length);
        Arrays.sort(expected);

        MinHeap heap = new MinHeap();
        for (int value : values) {
            heap.insert(value);
        }

        for (int value : expected) {
            assertEquals(value, heap.extractMin());
        }
    }
}
