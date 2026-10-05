package assignment2;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Locale;
import java.util.Random;

public class Benchmark {
    private static final int[] SIZES = {100, 1_000, 10_000, 100_000};
    private static final int RUNS = 5;
    private static long checkValue;

    private static class RunResult {
        double timeMs;
        long steps;
        long moves;
        long comparisons;

        RunResult(double timeMs, Metrics metrics) {
            this.timeMs = timeMs;
            steps = metrics.getSteps();
            moves = metrics.getMoves();
            comparisons = metrics.getComparisons();
        }
    }

    public static void main(String[] args) throws IOException {
        Locale.setDefault(Locale.US);

        File resultFolder = new File("results");
        if (!resultFolder.exists()) {
            resultFolder.mkdirs();
        }

        PrintWriter writer = new PrintWriter(new FileWriter("results/results.csv"));
        writer.println("workload,variant,structure,n,time_ms,steps,moves,comparisons");

        for (int n : SIZES) {
            int[] data = createData(n);

            System.out.println("Running n = " + n);
            runW1(writer, data);
            runW2(writer, data);
            runW3(writer, data);
            runW4(writer, data);
        }

        writer.close();
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
        int[] indexes = new int[10_000];
        Random random = new Random(42);

        for (int i = 0; i < indexes.length; i++) {
            indexes[i] = random.nextInt(data.length);
        }

        RunResult arrayResult = measureRandomAccess("DynamicArray", data, indexes);
        writeRow(writer, "W1", "-", "DynamicArray", data.length, arrayResult);

        RunResult listResult = measureRandomAccess("MyLinkedList", data, indexes);
        writeRow(writer, "W1", "-", "MyLinkedList", data.length, listResult);
    }

    private static RunResult measureRandomAccess(String name, int[] data, int[] indexes) {
        runRandomAccess(createList(name), data, indexes);

        RunResult[] results = new RunResult[RUNS];
        for (int i = 0; i < RUNS; i++) {
            results[i] = runRandomAccess(createList(name), data, indexes);
        }

        return getMedian(results);
    }

    private static RunResult runRandomAccess(IntList structure, int[] data, int[] indexes) {
        fillList(structure, data);
        structure.resetMetrics();

        long sum = 0;
        long start = System.nanoTime();

        for (int index : indexes) {
            sum += structure.get(index);
        }

        long end = System.nanoTime();
        checkValue = sum;

        return new RunResult(toMs(start, end), structure.getMetrics());
    }

    private static void runW2(PrintWriter writer, int[] data) {
        int[] queries = new int[1_000];
        Random random = new Random(42);

        for (int i = 0; i < 500; i++) {
            queries[i] = data[random.nextInt(data.length)];
        }

        for (int i = 500; i < 1_000; i++) {
            queries[i] = -(i + 1);
        }

        shuffle(queries, new Random(42));

        RunResult arrayResult = measureSearch("DynamicArray", data, queries);
        writeRow(writer, "W2", "-", "DynamicArray", data.length, arrayResult);

        RunResult listResult = measureSearch("MyLinkedList", data, queries);
        writeRow(writer, "W2", "-", "MyLinkedList", data.length, listResult);
    }

    private static RunResult measureSearch(String name, int[] data, int[] queries) {
        runSearch(createList(name), data, queries);

        RunResult[] results = new RunResult[RUNS];
        for (int i = 0; i < RUNS; i++) {
            results[i] = runSearch(createList(name), data, queries);
        }

        return getMedian(results);
    }

    private static RunResult runSearch(IntList structure, int[] data, int[] queries) {
        fillList(structure, data);
        structure.resetMetrics();

        int found = 0;
        long start = System.nanoTime();

        for (int query : queries) {
            if (structure.contains(query)) {
                found++;
            }
        }

        long end = System.nanoTime();
        checkValue = found;

        return new RunResult(toMs(start, end), structure.getMetrics());
    }

    private static void runW3(PrintWriter writer, int[] data) {
        int[] values = new int[1_000];
        Random random = new Random(42);

        for (int i = 0; i < values.length; i++) {
            values[i] = random.nextInt(1_000_000);
        }

        RunResult arrayHead = measureInsertRemove("DynamicArray", data, values, 0);
        writeRow(writer, "W3", "head", "DynamicArray", data.length, arrayHead);

        RunResult listHead = measureInsertRemove("MyLinkedList", data, values, 0);
        writeRow(writer, "W3", "head", "MyLinkedList", data.length, listHead);

        int middle = data.length / 2;

        RunResult arrayMiddle = measureInsertRemove("DynamicArray", data, values, middle);
        writeRow(writer, "W3", "middle", "DynamicArray", data.length, arrayMiddle);

        RunResult listMiddle = measureInsertRemove("MyLinkedList", data, values, middle);
        writeRow(writer, "W3", "middle", "MyLinkedList", data.length, listMiddle);
    }

    private static RunResult measureInsertRemove(String name, int[] data, int[] values, int index) {
        runInsertRemove(createList(name), data, values, index);

        RunResult[] results = new RunResult[RUNS];
        for (int i = 0; i < RUNS; i++) {
            results[i] = runInsertRemove(createList(name), data, values, index);
        }

        return getMedian(results);
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
        runHeap(data);

        RunResult[] results = new RunResult[RUNS];
        for (int i = 0; i < RUNS; i++) {
            results[i] = runHeap(data);
        }

        RunResult median = getMedian(results);
        writeRow(writer, "W4", "-", "MinHeap", data.length, median);
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

    private static IntList createList(String name) {
        if (name.equals("DynamicArray")) {
            return new DynamicArray();
        }

        return new MyLinkedList();
    }

    private static RunResult getMedian(RunResult[] results) {
        for (int i = 0; i < results.length - 1; i++) {
            for (int j = i + 1; j < results.length; j++) {
                if (results[j].timeMs < results[i].timeMs) {
                    RunResult temp = results[i];
                    results[i] = results[j];
                    results[j] = temp;
                }
            }
        }

        return results[results.length / 2];
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
}
