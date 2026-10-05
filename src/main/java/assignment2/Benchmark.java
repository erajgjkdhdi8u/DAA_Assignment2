package assignment2;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Locale;
import java.util.Random;

public class Benchmark {
    private static final int[] SIZES = {100, 1_000, 10_000, 100_000};
    private static final int RUNS = 5;

    private interface Action {
        RunResult run();
    }

    private static class RunResult {
        double timeMs;
        long steps;
        long moves;
        long comparisons;

        RunResult(double timeMs, Metrics metrics) {
            this.timeMs = timeMs;
            this.steps = metrics.getSteps();
            this.moves = metrics.getMoves();
            this.comparisons = metrics.getComparisons();
        }
    }

    public static void main(String[] args) throws IOException {
        Locale.setDefault(Locale.US);

        File resultFolder = new File("results");
        if (!resultFolder.exists()) {
            resultFolder.mkdirs();
        }

        try (PrintWriter writer = new PrintWriter(new FileWriter("results/results.csv"))) {
            writer.println("workload,variant,structure,n,time_ms,steps,moves,comparisons");

            for (int n : SIZES) {
                int[] data = createData(n);

                System.out.println("Running n = " + n);
                runW1(writer, data);
                runW2(writer, data);
                runW3(writer, data);
                runW4(writer, data);
            }
        }

        System.out.println("Done. Results are in results/results.csv");
    }

    private static int[] createData(int n) {
        Random random = new Random(42);
        int[] data = new int[n];

        for (int i = 0; i < n; i++) {
            data[i] = random.nextInt(1_000_000);
        }

        return data;
    }

    private static void runW1(PrintWriter writer, int[] data) {
        int n = data.length;
        int[] indexes = new int[10_000];
        Random random = new Random(42);

        for (int i = 0; i < indexes.length; i++) {
            indexes[i] = random.nextInt(n);
        }

        RunResult arrayResult = measure(() -> runRandomAccess(new DynamicArray(), data, indexes));
        writeRow(writer, "W1", "-", "DynamicArray", n, arrayResult);

        RunResult listResult = measure(() -> runRandomAccess(new MyLinkedList(), data, indexes));
        writeRow(writer, "W1", "-", "MyLinkedList", n, listResult);
    }

    private static RunResult runRandomAccess(IntList structure, int[] data, int[] indexes) {
        fillList(structure, data);
        structure.resetMetrics();

        long start = System.nanoTime();
        long check = 0;

        for (int index : indexes) {
            check += structure.get(index);
        }

        long end = System.nanoTime();
        use(check);

        return new RunResult(toMs(start, end), structure.getMetrics());
    }

    private static void runW2(PrintWriter writer, int[] data) {
        int n = data.length;
        int[] queries = new int[1_000];
        Random random = new Random(42);

        for (int i = 0; i < 500; i++) {
            queries[i] = data[random.nextInt(n)];
        }

        for (int i = 500; i < 1_000; i++) {
            queries[i] = -(i + 1);
        }

        shuffle(queries, new Random(42));

        RunResult arrayResult = measure(() -> runSearch(new DynamicArray(), data, queries));
        writeRow(writer, "W2", "-", "DynamicArray", n, arrayResult);

        RunResult listResult = measure(() -> runSearch(new MyLinkedList(), data, queries));
        writeRow(writer, "W2", "-", "MyLinkedList", n, listResult);
    }

    private static RunResult runSearch(IntList structure, int[] data, int[] queries) {
        fillList(structure, data);
        structure.resetMetrics();

        long start = System.nanoTime();
        int found = 0;

        for (int query : queries) {
            if (structure.contains(query)) {
                found++;
            }
        }

        long end = System.nanoTime();
        use(found);

        return new RunResult(toMs(start, end), structure.getMetrics());
    }

    private static void runW3(PrintWriter writer, int[] data) {
        int n = data.length;
        int[] values = new int[1_000];
        Random random = new Random(42);

        for (int i = 0; i < values.length; i++) {
            values[i] = random.nextInt(1_000_000);
        }

        RunResult arrayHead = measure(() -> runInsertRemove(new DynamicArray(), data, values, 0));
        writeRow(writer, "W3", "head", "DynamicArray", n, arrayHead);

        RunResult listHead = measure(() -> runInsertRemove(new MyLinkedList(), data, values, 0));
        writeRow(writer, "W3", "head", "MyLinkedList", n, listHead);

        int middle = n / 2;

        RunResult arrayMiddle = measure(() -> runInsertRemove(new DynamicArray(), data, values, middle));
        writeRow(writer, "W3", "middle", "DynamicArray", n, arrayMiddle);

        RunResult listMiddle = measure(() -> runInsertRemove(new MyLinkedList(), data, values, middle));
        writeRow(writer, "W3", "middle", "MyLinkedList", n, listMiddle);
    }

    private static RunResult runInsertRemove(IntList structure, int[] data, int[] values, int index) {
        fillList(structure, data);
        structure.resetMetrics();

        long start = System.nanoTime();

        for (int value : values) {
            structure.add(index, value);
        }

        for (int i = 0; i < values.length; i++) {
            structure.remove(index);
        }

        long end = System.nanoTime();

        return new RunResult(toMs(start, end), structure.getMetrics());
    }

    private static void runW4(PrintWriter writer, int[] data) {
        int n = data.length;
        RunResult result = measure(() -> runHeap(data));
        writeRow(writer, "W4", "-", "MinHeap", n, result);
    }

    private static RunResult runHeap(int[] data) {
        MinHeap heap = new MinHeap();
        heap.resetMetrics();

        long start = System.nanoTime();

        for (int value : data) {
            heap.insert(value);
        }

        int previous = Integer.MIN_VALUE;

        while (heap.size() > 0) {
            int value = heap.extractMin();

            if (value < previous) {
                throw new IllegalStateException("Heap output is not sorted");
            }

            previous = value;
        }

        long end = System.nanoTime();

        return new RunResult(toMs(start, end), heap.getMetrics());
    }

    private static RunResult measure(Action action) {
        action.run();

        RunResult[] results = new RunResult[RUNS];

        for (int i = 0; i < RUNS; i++) {
            results[i] = action.run();
        }

        RunResult[] sorted = Arrays.copyOf(results, results.length);
        Arrays.sort(sorted, Comparator.comparingDouble(result -> result.timeMs));
        return sorted[RUNS / 2];
    }

    private static void fillList(IntList structure, int[] data) {
        for (int value : data) {
            structure.add(value);
        }
    }

    private static void shuffle(int[] values, Random random) {
        for (int i = values.length - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            int temp = values[i];
            values[i] = values[j];
            values[j] = temp;
        }
    }

    private static void writeRow(PrintWriter writer, String workload, String variant,
                                 String structure, int n, RunResult result) {
        writer.printf(Locale.US, "%s,%s,%s,%d,%.4f,%d,%d,%d%n",
                workload,
                variant,
                structure,
                n,
                result.timeMs,
                result.steps,
                result.moves,
                result.comparisons);
        writer.flush();
    }

    private static double toMs(long start, long end) {
        return (end - start) / 1_000_000.0;
    }

    private static volatile long blackHole;

    private static void use(long value) {
        blackHole = value;
    }
}
