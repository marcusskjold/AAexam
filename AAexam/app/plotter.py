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


def plot_task12():
    filename = PLOT + '/t12p1.pdf'
    (fig, ax) = plt.subplots()
    sizes = [256_000, 512_000, 1_024_000, 2_000_000,
             4_000_000, 8_000_000, 16_000_000, 32_000_000]
    markers = ['1', '2', '3', '4', '+', 'x', '.', 's']
    for i in range(0, 8):
        name = RES + '/t12e1_{}.csv'.format(i)
        res = read_single_parameterized(name)
        baseline = res[-1].time
        times = [(baseline / r.time) for r in res]
        params = [(sizes[i] / r.param) for r in res]
        ax.plot(params, times, linestyle=':', linewidth='1',
                marker=markers[i], markersize='4',
                label='array size {:,.0f}'.format(sizes[i]))

    ax.legend(fontsize=8)
    # plt.ylim(ymax=120)
    # plt.xlim(xmax=150, xmin=1)
    plt.grid(axis='y', linewidth=0.3, linestyle='--')
    ax.set_xlabel('Number of parallel tasks (calculated as $\\frac{n}{c}$)')
    ax.set_ylabel('Speedup')
    ax.set_xscale('log', base=2)
    # ax.set_yscale('log')
    # ax.legend(algorithms)
    fig.savefig(filename)


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


def plot_task2():
    filename = PLOT + '/t2p1.pdf'
    source = RES + '/t2e1.csv'
    res = read_single_parameterized(source)

    (fig, ax) = plt.subplots()
    ax2 = ax.twinx()
    ax.set_title("Classic top down mergesort with random Integer array input")

    # resstds = [r.sdres for r in res]
    # timstds = [r.sdtime for r in res]
    results = [r.res for r in res]
    times = [r.time for r in res]
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


def plot_task4():
    filename = PLOT + '/t4p1.pdf'
    (fig, ax) = plt.subplots()
    sizes = [100, 1000, 10000, 100000, 1000000, 2000000]
    for i in range(2, 6):
        name = RES + '/t4e1_{}.csv'.format(i)
        res = read_single_parameterized(name)
        baseline = res[0].time
        times = [(r.time / baseline) * 100 for r in res]
        params = [r.param for r in res]
        ax.plot(params, times, label='array size ' + str(sizes[i-1]),
                linestyle=':', marker='o', markersize='2', linewidth='1')

    ax.legend(fontsize=8)
    plt.ylim(ymax=120)
    plt.xlim(xmax=150, xmin=1)
    plt.grid(axis='y', linewidth=0.3, linestyle='--')
    ax.set_xlabel('value of cutoff $c$')
    ax.set_ylabel('Time (percent)')
    ax.set_xscale('log')
    # ax.set_yscale('log')
    # ax.legend(algorithms)
    fig.savefig(filename)

#Plot for large-scale analysis of c-value for iterative merge-sort
def plot_task4_finegrained():
    filename = PLOT + '/t4p2.pdf'
    (fig, ax) = plt.subplots()
    sizes = [100_000, 200_000, 300_000]
    results = []
    #Loops through files t7e2_1 to t7e3_3
    for i in range(1, 2):
        name = RES + '/t4e2_{}.csv'.format(i)
        res = read_single_parameterized(name)
        baseline = res[0].time
        times = [(r.time / baseline) * 100 for r in res]
        params = [r.param for r in res]
        results.append((times, params))
        ax.plot(params, times, label='array size ' + str(sizes[i-1]),
                linestyle=':', marker='o', markersize='2', linewidth='1')

    ax.legend(fontsize=8)
    plt.ylim(ymax=120)
    plt.xlim(xmax=50, xmin=1)
    plt.grid(axis='y', linewidth=0.3, linestyle='--')
    ax.set_xlabel('value of cutoff $c$')
    ax.set_ylabel('Time (percent)')
    #ax.set_xscale('log')
    # ax.set_yscale('log')
    # ax.legend(algorithms)
    fig.savefig(filename)

#Plot for large-scale analysis of c-value for iterative merge-sort
def plot_task7_overview():
    filename = PLOT + '/t7p1.pdf'
    (fig, ax) = plt.subplots()
    sizes = [100, 1000, 10000, 100000, 1000000]
    results = []
    #Loops through files t7e1_2 to t7e1_5
    for i in range(2, 5):
        name = RES + '/t7e1_{}.csv'.format(i)
        res = read_single_parameterized(name)
        baseline = res[0].time
        times = [(r.time / baseline) * 100 for r in res]
        params = [r.param for r in res]
        results.append((times, params))
        ax.plot(params, times, label='array size ' + str(sizes[i-1]),
                linestyle=':', marker='o', markersize='2', linewidth='1')

    ax.legend(fontsize=8)
    plt.ylim(ymax=150)
    plt.xlim(xmax=30, xmin=1)
    plt.grid(axis='y', linewidth=0.3, linestyle='--')
    ax.set_xlabel('value of cutoff $c$')
    ax.set_ylabel('Time (percent)')
    ax.set_xscale('log')
    # ax.set_yscale('log')
    # ax.legend(algorithms)
    fig.savefig(filename)

#Plot for large-scale analysis of c-value for iterative merge-sort
def plot_task7_finegrained():
    filename = PLOT + '/t7p2.pdf'
    (fig, ax) = plt.subplots()
    sizes = [100_000, 200_000, 300_000]
    results = []
    #Loops through files t7e2_1
    for i in range(1, 2):
        name = RES + '/t7e2_{}.csv'.format(i)
        res = read_single_parameterized(name)
        baseline = res[0].time
        times = [(r.time / baseline) * 100 for r in res]
        params = [r.param for r in res]
        results.append((times, params))
        ax.plot(params, times, label='array size ' + str(sizes[i-1]),
                linestyle=':', marker='o', markersize='2', linewidth='1')

    ax.legend(fontsize=8)
    plt.ylim(ymax=120)
    plt.xlim(xmax=50, xmin=1)
    plt.grid(axis='y', linewidth=0.3, linestyle='--')
    ax.set_xlabel('value of cutoff $c$')
    ax.set_ylabel('Time (percent)')
    #ax.set_xscale('log')
    # ax.set_yscale('log')
    # ax.legend(algorithms)
    fig.savefig(filename)




def plot_task7_experimental():
    filename = PLOT + '/t7p3.pdf'
    (fig, ax) = plt.subplots()
    sizes = [10_000, 200_000, 300_000]
    results = []
    #Loops through files t7e2_1 to t7e3_3
    for i in range(1, 2):
        name = RES + '/t7e3_{}.csv'.format(i)
        res = read_single_parameterized(name)
        baseline = res[0].time
        times = [(r.time / baseline) * 100 for r in res]
        params = [r.param for r in res]
        results.append((times, params))
        ax.plot(params, times, label='array size ' + str(sizes[i-1]),
                linestyle=':', marker='o', markersize='2', linewidth='1')

    ax.legend(fontsize=8)
    plt.ylim(ymax=120, ymin=85)
    plt.xlim(xmax=50, xmin=1)
    plt.grid(axis='y', linewidth=0.3, linestyle='--')
    ax.set_xlabel('value of cutoff $c$')
    ax.set_ylabel('Time (percent)')
    #ax.set_xscale('log')
    # ax.set_yscale('log')
    # ax.legend(algorithms)
    fig.savefig(filename)



if __name__ == '__main__':
    raw_results = read_single_parameterized(FILENAME_CSV)
    # plot_task2()
    # plot_task4()
    # read_results('log/results.csv')
    #plot_task12()
    plot_task7_experimental()

####################PART 2################################3



##Plotting function for task9

def plot_parameterized(prefix, x_param_func, y_param_func, labels, xlabel, ylabel, output_filename):
    """
    Generalized plotting function for parameterized data.

    :param prefix: The prefix for the CSV files (e.g., 't9random').
    :param x_param_func: A function to extract the x-axis parameter from a ParamRes object.
    :param y_param_func: A function to extract the y-axis parameter from a ParamRes object.
    :param labels: A list of tuples [(file_suffix, label)] for the file suffixes and their corresponding labels.
    :param xlabel: Label for the x-axis.
    :param ylabel: Label for the y-axis.
    :param output_filename: The name of the output PDF file.
    """
    (fig, ax) = plt.subplots()

    # Loop through the provided file labels
    for file_suffix, label in labels:
        filepath = f'{RES}/{prefix}{file_suffix}.csv'

        # Read data from the CSV file
        res = read_single_parameterized(filepath)

        # Extract x and y values using the provided parameter functions
        x_values = [x_param_func(r) for r in res]
        y_values = [y_param_func(r) for r in res]

        # Plot each curve
        ax.plot(x_values, y_values, label=label,
                linestyle='-', marker='o', markersize=4, linewidth=1.5)

    ax.legend(fontsize=8)
    plt.grid(axis='y', linewidth=0.3, linestyle='--')
    ax.set_xlabel(xlabel)
    ax.set_ylabel(ylabel)
    plt.ylim(auto=True)
    plt.xlim(auto=True)
    fig.savefig(output_filename)


####Plots for task9
if __name__ == '__main__':
    part2Algorithms= [
        ("1", "Non-adaptive LevelSort"),
        ("2", "Adaptive LevelSort"),
        ("3", "Non-adaptive Binomial Sort"),
        ("4", "Adaptive Binomial Sort"),
    ]

    plot_parameterized(prefix="t9random",
    x_param_func=lambda r: r.param,  # X-axis: 'param' attribute
    y_param_func=lambda r: r.time,  # Y-axis: 'time' attribute
    labels=part2Algorithms,
    xlabel="value of cutoff $c$",
    ylabel="Time",
    output_filename=f"{PLOT}/t9random_time_plot.pdf"
    )

    plot_parameterized(prefix="t9random",
    x_param_func=lambda r: r.param,  # X-axis: 'param' attribute
    y_param_func=lambda r: r.res,  # Y-axis: 'time' attribute
    labels=part2Algorithms,
    xlabel="value of cutoff $c$",
    ylabel="Compares",
    output_filename=f"{PLOT}/t9random_compares_plot.pdf"
    )

    plot_parameterized(prefix="t9minrun",
    x_param_func=lambda r: r.param,  # X-axis: 'param' attribute
    y_param_func=lambda r: r.res,  # Y-axis: 'time' attribute
    labels=part2Algorithms,
    xlabel="value of cutoff $c$",
    ylabel="Compares",
    output_filename=f"{PLOT}/t9minrun_compares_plot.pdf"
    )

    plot_parameterized(prefix="t9minrun",
    x_param_func=lambda r: r.param,  # X-axis: 'param' attribute
    y_param_func=lambda r: r.time,  # Y-axis: 'time' attribute
    labels=part2Algorithms,
    xlabel="value of cutoff $c$",
    ylabel="Time",
    output_filename=f"{PLOT}/t9minrun_time_plot.pdf"
    )

    plot_parameterized(prefix="t9runs",
    x_param_func=lambda r: r.param,  # X-axis: 'param' attribute
    y_param_func=lambda r: r.res,  # Y-axis: 'time' attribute
    labels=part2Algorithms,
    xlabel="value of cutoff $c$",
    ylabel="Compares",
    output_filename=f"{PLOT}/t9more_runs_compares_plot.pdf"
    )

    plot_parameterized(prefix="t9runs",
    x_param_func=lambda r: r.param,  # X-axis: 'param' attribute
    y_param_func=lambda r: r.time,  # Y-axis: 'time' attribute
    labels=part2Algorithms,
    xlabel="value of cutoff $c$",
    ylabel="Time",
    output_filename=f"{PLOT}/t9more_runs_time_plot.pdf"
    )

    plot_parameterized(prefix="t9differentRuns",
    x_param_func=lambda r: r.param,  # X-axis: 'param' attribute
    y_param_func=lambda r: r.res,  # Y-axis: 'time' attribute
    labels=part2Algorithms,
    xlabel="amount of runs $r$",
    ylabel="Compares",
    output_filename=f"{PLOT}/t9diff_runs_compares_plot.pdf"
    )

    plot_parameterized(prefix="t9differentRuns",
    x_param_func=lambda r: r.param,  # X-axis: 'param' attribute
    y_param_func=lambda r: r.time,  # Y-axis: 'time' attribute
    labels=part2Algorithms,
    xlabel="amount of runs $r$",
    ylabel="Time",
    output_filename=f"{PLOT}/t9diff_runs_time_plot.pdf"
    )



##Plotting function for task10
def plot_parameterized_task10(prefix, x_param_func, y_param_func, labels, xlabel, ylabel, output_filename):
    """
    Generalized plotting function for parameterized data, especially for task10.

    :param prefix: The prefix for the CSV files (e.g., 't9random').
    :param x_param_func: A function to extract the x-axis parameter from a ParamRes object.
    :param y_param_func: A function to extract the y-axis parameter from a ParamRes object.
    :param labels: A list of tuples [(file_suffix, label)] for the file suffixes and their corresponding labels.
    :param xlabel: Label for the x-axis.
    :param ylabel: Label for the y-axis.
    :param output_filename: The name of the output PDF file.
    """
    (fig, ax) = plt.subplots()

    # Loop through the provided file labels
    for file_suffix, label in labels:
        filepath = f'{RES}/{prefix}{file_suffix}.csv'

        # Read data from the CSV file
        res = read_single_parameterized(filepath)

        # Extract x and y values using the provided parameter functions
        x_values = [x_param_func(r) for r in res]
        y_values = [y_param_func(r) for r in res]

        # Plot each curve
        ax.plot(x_values, y_values, label=label,
                linestyle='-', marker='o', markersize=4, linewidth=1.5)

    ax.legend(fontsize=8)
    plt.grid(axis='y', linewidth=0.3, linestyle='--')
    ax.set_xlabel(xlabel)
    ax.set_ylabel(ylabel)
    plt.ylim(auto=True)
    plt.xlim(auto=True)
    fig.savefig(output_filename)


####Plots for task10
if __name__ == '__main__':
    task10Algorithms= [
        ("1", "Adaptive LevelSort"),
        ("2", "Adaptive Binomial Sort"),
        ("3", "Arrays.sort()"),
        ("4", "Plain Merge Sort"),
    ]

    plot_parameterized_task10(prefix="t10random",
    x_param_func=lambda r: r.param,  # X-axis: 'param' attribute
    y_param_func=lambda r: r.time,  # Y-axis: 'time' attribute
    labels=task10Algorithms,
    xlabel="size of arrays $n$",
    ylabel="Time",
    output_filename=f"{PLOT}/t10diff_sizes_time_plot.pdf"
    )


    plot_parameterized_task10(prefix="t10runs",
    x_param_func=lambda r: r.param,  # X-axis: 'param' attribute
    y_param_func=lambda r: r.time,  # Y-axis: 'time' attribute
    labels=task10Algorithms,
    xlabel="number of runs $r$",
    ylabel="Time",
    output_filename=f"{PLOT}/t10runs_time_plot.pdf"
    )

