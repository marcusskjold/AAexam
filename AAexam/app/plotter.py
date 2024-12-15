#!/usr/bin/env python3

import csv
from typing import Dict, List
import matplotlib.pyplot as plt  # type: ignore
import numpy as np  # type: ignore
from collections import namedtuple

BASE = 'data'
RES = BASE + '/results'
PLOT = BASE + '/plots'

FILENAME_TAB = 'data/results/t2e1.tex'
FILENAME_PLOT = 'data/results/t2e1.pdf'
FILENAME_CSV = 'data/results/t2e1.csv'

ParamRes = namedtuple("ParamRes", "param rep time sdtime res sdres")


def read_single_parameterized(filename: str) -> List[ParamRes]:
    results: List[tuple] = list()

    with open(filename, 'r', newline='') as f:
        reader = csv.DictReader(f)
        for row in reader:
            param = float(row['PARAMETER'])
            rep = float(row['REPETITIONS'])
            time = float(row['MEANTIME'])
            sdtime = float(row['SDEVTIME'])
            res = float(row['MEANRESULT'])
            sdres = float(row['SDEVRESULT'])
            t = ParamRes(param, rep, time, sdtime, res, sdres)
            results.append(t)

    return results


def plot_task1():
    filename = PLOT + '/t2p1.pdf'
    source = RES + '/t2e1.csv'
    res = read_single_parameterized(source)

    (fig, ax) = plt.subplots()
    ax2 = ax.twinx()
    ax.set_title("Classic top down mergesort with random Integer array input")

    results = [r.res for r in res]
    # resstds = [r.sdres for r in res]
    times = [r.time for r in res]
    # timstds = [r.sdtime for r in res]
    params = [r.param for r in res]
    ax.plot(params, results, marker='o',
            color='blue', linestyle='solid', label='compares')
    ax2.plot(params, times, marker='^',
             color='red', linestyle='--', label='time')
    ax.legend(loc='upper left')
    ax2.legend(loc='upper right')
    # to remember how to do errorbars
    # ax2.errorbar(params, times, timstds, marker='^', capsize=7.0,
    #              color='red', linestyle='--', label='time')
    ax.set_xlabel('Number of elements $n$')
    ax.set_ylabel('Compares')
    ax2.set_ylabel('Time (ns)')
    ax.set_xscale('log')
    ax.set_yscale('log')
    ax2.set_yscale('log')
    # ax.legend(algorithms)
    fig.savefig(filename)


if __name__ == '__main__':
    raw_results = read_single_parameterized(FILENAME_CSV)
    plot_task1()
    # read_results('log/results.csv')
