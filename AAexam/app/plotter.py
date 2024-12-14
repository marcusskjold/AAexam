#!/usr/bin/env python3

import csv
from typing import Dict, List
import matplotlib.pyplot as plt  # type: ignore
import numpy as np  # type: ignore

FILENAME_TAB = 'results/t2e1.tex'
FILENAME_PLOT = 'results/t2e1.pdf'
FILENAME_CSV = 'results/t2e1.csv'
ALGORITHM = 'quartic'
PROBLEM = 'foursum'


det read_simple(filename: str) -> Dict[str, List[]]


def read_results(filename: str) -> Dict[str, Dict[int, List[float]]]:
    results: Dict[str, Dict[str, Dict[int, List[float]]]] = dict()
    with open(filename, 'r') as f:
        reader = csv.DictReader(f)
        for row in reader:
            problem: str = row['problem']
            algorithm: str = row['algorithm']
            n: int = int(row['n'])
            t: float = float(row['time'])
            if problem not in results:
                results[problem] = dict()
            if algorithm not in results[problem]:
                results[problem][algorithm] = dict()
            if n not in results[problem][algorithm]:
                results[problem][algorithm][n] = list()
            results[problem][algorithm][n].append(t)
    return results


def compute_median(raw: Dict[int, List[float]]) -> np.ndarray:
    result = np.zeros((len(raw), 2))
    for i, n in enumerate(sorted(raw)):
        result[i, 0] = n
        result[i, 1] = np.median(raw[n])
    return result


def compute_mean_std(raw: Dict[int, List[float]]) -> np.ndarray:
    result = np.zeros((len(raw), 3))
    for i, n in enumerate(sorted(raw)):
        result[i, 0] = n
        result[i, 1] = np.mean(raw[n])
        result[i, 2] = np.std(raw[n], ddof=1)
    return result


def write_latex_tabular(res: np.ndarray,
                        filename: str):
    with open(filename, 'w') as f:
        f.write(r'\begin{tabular}{rrr}' + '\n')
        f.write(r'$n$ & Average (s) & ' +
                'Standard deviation (s)')
        f.write(r'\\\hline' + '\n')
        for i in range(res.shape[0]):
            fields = [str(int(res[i, 0])),
                      f'{res[i, 1]:.6f}',
                      f'{res[i, 2]:.6f}']
            f.write(' & '.join(fields) + r'\\'+'\n')
        f.write(r'\end{tabular}' + '\n')


def plot_algorithms(res: Dict[str, np.ndarray],
                    filename: str):
    (fig, ax) = plt.subplots()
    # algorithms = ['cubic', 'quadratic', 'hashmap']
    algorithms = res.keys()
    for algorithm in algorithms:
        ns = res[algorithm][:, 0]
        means = res[algorithm][:, 1]
        stds = res[algorithm][:, 2]
        ax.errorbar(ns, means, stds, marker='o',
                    capsize=3.0)
    ax.set_xlabel('Number of elements $n$')
    ax.set_ylabel('Time (s)')
    ax.set_xscale('log')
    ax.set_yscale('log')
    ax.legend(algorithms)
    fig.savefig(filename)


if __name__ == '__main__':
    raw_results: Dict[str, Dict[int, List[float]]] = \
        read_results(FILENAME_CSV)
    # read_results('log/results.csv')
    refined_results: Dict[str, Dict[str, np.ndarray]] = dict()
    for problem in raw_results:
        if problem not in refined_results:
            refined_results[problem] = dict()
        for algorithm in raw_results[problem]:
            refined_results[problem][algorithm] \
                = compute_mean_std(raw_results[problem][algorithm])
    write_latex_tabular(refined_results[PROBLEM][ALGORITHM], FILENAME_TAB)
    plot_algorithms(refined_results[PROBLEM], FILENAME_PLOT)
