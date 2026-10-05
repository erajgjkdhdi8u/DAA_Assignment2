package assignment2;

public class MyLinkedList implements IntList {
    private static class Node {
        int value;
        Node next;

        Node(int value) {
            this.value = value;
        }
    }

    private Node head;
    private Node tail;
    private int size;
    private final Metrics metrics;

    public MyLinkedList() {
        metrics = new Metrics();
    }

    @Override
    public void add(int value) {
        Node newNode = new Node(value);

        if (head == null) {
            head = newNode;
            tail = newNode;
            metrics.addMove();
            metrics.addMove();
        } else {
            tail.next = newNode;
            tail = newNode;
            metrics.addMove();
            metrics.addMove();
        }

        size++;
    }

    @Override
    public void add(int index, int value) {
        checkAddIndex(index);

        if (index == size) {
            add(value);
            return;
        }

        Node newNode = new Node(value);

        if (index == 0) {
            newNode.next = head;
            head = newNode;
            metrics.addMove();
            metrics.addMove();

            if (tail == null) {
                tail = newNode;
                metrics.addMove();
            }
        } else {
            Node previous = getNode(index - 1);
            newNode.next = previous.next;
            previous.next = newNode;
            metrics.addMove();
            metrics.addMove();
        }

        size++;
    }

    @Override
    public int remove(int index) {
        checkIndex(index);

        int removedValue;

        if (index == 0) {
            removedValue = head.value;
            head = head.next;
            metrics.addStep();
            metrics.addMove();

            if (size == 1) {
                tail = null;
                metrics.addMove();
            }
        } else {
            Node previous = getNode(index - 1);
            Node removed = previous.next;
            removedValue = removed.value;

            previous.next = removed.next;
            metrics.addMove();

            if (removed == tail) {
                tail = previous;
                metrics.addMove();
            }
        }

        size--;
        return removedValue;
    }

    @Override
    public int get(int index) {
        checkIndex(index);
        return getNode(index).value;
    }

    @Override
    public boolean contains(int value) {
        Node current = head;

        while (current != null) {
            metrics.addComparison();

            if (current.value == value) {
                return true;
            }

            current = current.next;
            metrics.addStep();
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

    private Node getNode(int index) {
        Node current = head;

        for (int i = 0; i < index; i++) {
            current = current.next;
            metrics.addStep();
        }

        return current;
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
