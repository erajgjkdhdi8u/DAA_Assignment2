import csv
from pathlib import Path

import matplotlib.pyplot as plt

ROOT = Path(__file__).resolve().parents[1]
CSV_FILE = ROOT / "results" / "results.csv"
PLOT_DIR = ROOT / "results" / "plots"
PLOT_DIR.mkdir(parents=True, exist_ok=True)


def read_rows():
    with CSV_FILE.open(newline="", encoding="utf-8") as file:
        rows = list(csv.DictReader(file))

    for row in rows:
        row["n"] = int(row["n"])
        row["time_ms"] = float(row["time_ms"])
        row["steps"] = int(row["steps"])
        row["moves"] = int(row["moves"])
        row["comparisons"] = int(row["comparisons"])

    return rows


def filtered(rows, workload, variant=None):
    result = []
    for row in rows:
        if row["workload"] != workload:
            continue
        if variant is not None and row["variant"] != variant:
            continue
        result.append(row)
    return result


def save_time_plot(rows, title, filename):
    plt.figure(figsize=(8, 5))

    structures = sorted(set(row["structure"] for row in rows))
    for structure in structures:
        current = sorted(
            [row for row in rows if row["structure"] == structure],
            key=lambda row: row["n"],
        )
        plt.plot(
            [row["n"] for row in current],
            [row["time_ms"] for row in current],
            marker="o",
            label=structure,
        )

    plt.xscale("log")
    plt.xlabel("n")
    plt.ylabel("Time (ms)")
    plt.title(title)
    plt.legend()
    plt.grid(True, alpha=0.3)
    plt.tight_layout()
    plt.savefig(PLOT_DIR / filename, dpi=160)
    plt.close()


def save_operations_plot(rows, title, filename):
    plt.figure(figsize=(9, 5.5))

    structures = sorted(set(row["structure"] for row in rows))
    metrics = ["steps", "moves", "comparisons"]

    for structure in structures:
        current = sorted(
            [row for row in rows if row["structure"] == structure],
            key=lambda row: row["n"],
        )

        for metric in metrics:
            values = [row[metric] for row in current]
            if any(value != 0 for value in values):
                plt.plot(
                    [row["n"] for row in current],
                    values,
                    marker="o",
                    label=f"{structure} {metric}",
                )

    plt.xscale("log")
    plt.yscale("log")
    plt.xlabel("n")
    plt.ylabel("Operation count")
    plt.title(title)
    plt.legend()
    plt.grid(True, alpha=0.3)
    plt.tight_layout()
    plt.savefig(PLOT_DIR / filename, dpi=160)
    plt.close()


def main():
    rows = read_rows()

    w1 = filtered(rows, "W1")
    save_time_plot(w1, "W1 Random Access - Time vs n", "w1_time.png")
    save_operations_plot(w1, "W1 Random Access - Operations vs n", "w1_operations.png")

    w2 = filtered(rows, "W2")
    save_time_plot(w2, "W2 Search - Time vs n", "w2_time.png")
    save_operations_plot(w2, "W2 Search - Operations vs n", "w2_operations.png")

    w3_head = filtered(rows, "W3", "head")
    save_time_plot(w3_head, "W3 Head - Time vs n", "w3_head_time.png")
    save_operations_plot(w3_head, "W3 Head - Operations vs n", "w3_head_operations.png")

    w3_middle = filtered(rows, "W3", "middle")
    save_time_plot(w3_middle, "W3 Middle - Time vs n", "w3_middle_time.png")
    save_operations_plot(w3_middle, "W3 Middle - Operations vs n", "w3_middle_operations.png")

    w4 = filtered(rows, "W4")
    save_time_plot(w4, "W4 Priority Processing - Time vs n", "w4_time.png")
    save_operations_plot(w4, "W4 Priority Processing - Operations vs n", "w4_operations.png")

    print("Plots were saved to", PLOT_DIR)


if __name__ == "__main__":
    main()
