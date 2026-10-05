package assignment2;

public class DynamicArray implements IntList {
    private int[] data;
    private int size;
    private final Metrics metrics;

    public DynamicArray() {
        data = new int[10];
        size = 0;
        metrics = new Metrics();
    }

    @Override
    public void add(int value) {
        if (size == data.length) {
            grow();
        }

        data[size] = value;
        size++;
    }

    @Override
    public void add(int index, int value) {
        checkAddIndex(index);

        if (size == data.length) {
            grow();
        }

        for (int i = size; i > index; i--) {
            int oldValue = data[i - 1];
            metrics.addStep();

            data[i] = oldValue;
            metrics.addMove();
        }

        data[index] = value;
        size++;
    }

    @Override
    public int remove(int index) {
        checkIndex(index);

        int removedValue = data[index];
        metrics.addStep();

        for (int i = index; i < size - 1; i++) {
            int nextValue = data[i + 1];
            metrics.addStep();

            data[i] = nextValue;
            metrics.addMove();
        }

        size--;
        return removedValue;
    }

    @Override
    public int get(int index) {
        checkIndex(index);
        metrics.addStep();
        return data[index];
    }

    @Override
    public boolean contains(int value) {
        for (int i = 0; i < size; i++) {
            int current = data[i];
            metrics.addStep();
            metrics.addComparison();

            if (current == value) {
                return true;
            }
        }

        return false;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void resetMetrics() {
        metrics.reset();
    }

    @Override
    public Metrics getMetrics() {
        return metrics;
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

    private void checkIndex(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index: " + index);
        }
    }

    private void checkAddIndex(int index) {
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException("Index: " + index);
        }
    }
}
