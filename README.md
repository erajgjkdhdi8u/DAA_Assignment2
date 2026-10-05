# DAA Assignment 2 - Data Structures

Student: Islam Tauyekel  
Group: **change this line to your group before submission**

## Project idea

This project has three data structures written from scratch:

- `DynamicArray`
- `MyLinkedList`
- `MinHeap`

The main code does not use `ArrayList`, `LinkedList` or `PriorityQueue`.
All structures store primitive `int` values.

## Requirements

- JDK 17 or newer
- Maven 3.9+
- Python 3 with matplotlib only if you want to recreate the plots

## Run tests

```bash
mvn clean test
```

## Run benchmark

```bash
mvn exec:java
```

The benchmark creates:

```text
results/results.csv
```

It uses `n = 100, 1000, 10000, 100000` and `new Random(42)` for the input data.
Every benchmark case has one warm-up run and then five measured runs. The median measured time is saved.

## Create plots

```bash
python scripts/plot_results.py
```

The PNG files are saved to:

```text
results/plots/
```

## Metrics

The project counts operations inside the data structure methods.

- `steps`: reading an array cell, or moving to the next linked-list node
- `moves`: moving an element in an array, or changing a list link/pointer
- `comparisons`: comparing two stored values

## Project structure

```text
src/main/java/assignment2/
    Benchmark.java
    DynamicArray.java
    IntList.java
    Metrics.java
    MinHeap.java
    MyLinkedList.java

src/test/java/assignment2/
    DynamicArrayTest.java
    MinHeapTest.java
    MyLinkedListTest.java

results/
    results.csv
    plots/

scripts/
    plot_results.py

REPORT.md
README.md
pom.xml
```

## Git workflow expected by the assignment

The intended branches are:

```text
main
feature/array
feature/list
feature/heap
feature/metrics
```

The final working version should be on `main` and tagged `v1.0`.
