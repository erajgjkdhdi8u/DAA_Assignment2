package assignment2;

public class MinHeap {
    private int[] data;
    private int size;
    private final Metrics metrics;

    public MinHeap() {
        data = new int[10];
        metrics = new Metrics();
    }

    public void insert(int value) {
        if (size == data.length) {
            grow();
        }

        data[size] = value;
        size++;
        bubbleUp(size - 1);
    }

    public int peekMin() {
        if (size == 0) {
            throw new IllegalStateException("Heap is empty");
        }

        metrics.addStep();
        return data[0];
    }

    public int extractMin() {
        if (size == 0) {
            throw new IllegalStateException("Heap is empty");
        }

        int min = data[0];
        metrics.addStep();

        if (size == 1) {
            size = 0;
            return min;
        }

        int last = data[size - 1];
        metrics.addStep();
        data[0] = last;
        metrics.addMove();
        size--;

        bubbleDown(0);
        return min;
    }

    public int size() {
        return size;
    }

    public void resetMetrics() {
        metrics.reset();
    }

    public Metrics getMetrics() {
        return metrics;
    }

    boolean isValidHeap() {
        for (int i = 1; i < size; i++) {
            int parent = (i - 1) / 2;
            if (data[parent] > data[i]) {
                return false;
            }
        }
        return true;
    }

    private void bubbleUp(int index) {
        int current = index;

        while (current > 0) {
            int parent = (current - 1) / 2;

            int currentValue = data[current];
            int parentValue = data[parent];
            metrics.addStep();
            metrics.addStep();
            metrics.addComparison();

            if (parentValue <= currentValue) {
                break;
            }

            data[current] = parentValue;
            data[parent] = currentValue;
            metrics.addMove();
            metrics.addMove();

            current = parent;
        }
    }

    private void bubbleDown(int index) {
        int current = index;

        while (true) {
            int left = current * 2 + 1;
            int right = current * 2 + 2;
            int smallest = current;

            if (left < size) {
                int leftValue = data[left];
                int smallestValue = data[smallest];
                metrics.addStep();
                metrics.addStep();
                metrics.addComparison();

                if (leftValue < smallestValue) {
                    smallest = left;
                }
            }

            if (right < size) {
                int rightValue = data[right];
                int smallestValue = data[smallest];
                metrics.addStep();
                metrics.addStep();
                metrics.addComparison();

                if (rightValue < smallestValue) {
                    smallest = right;
                }
            }

            if (smallest == current) {
                break;
            }

            int currentValue = data[current];
            int smallestValue = data[smallest];
            metrics.addStep();
            metrics.addStep();

            data[current] = smallestValue;
            data[smallest] = currentValue;
            metrics.addMove();
            metrics.addMove();

            current = smallest;
        }
    }

    private void grow() {
        int[] newData = new int[data.length * 2];

        for (int i = 0; i < size; i++) {
            int value = data[i];
            metrics.addStep();
            newData[i] = value;
            metrics.addMove();
        }

        data = newData;
    }
}
