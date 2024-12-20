This is as short introduction to the repo.

At the top level, we have:

- The final report
- A printout of the result of running script.Main.main() in full, recreating all experiments.
- The assignment description
- The gradle project folder
- The report tex source folder

**To run all experiments**, perform the gradle run task in the project folder.
This can take a very long time. The results are saved as csv files in AAexam/app/data/results.

**To run a specific experiment**, edit `scripts.Main`, comment out the unrelated Parts, go into the specific part `scripts.PartX`,
comment out all the unrelated tasks from `PartX.main()`, save, then `./gradlew run`.

**To re-generate all graphs**, move to AAexam/app/ and run the plotter.py python script.
Graphs are found in AAexam/app/data/plots.

**To run all tests**, perform the gradle test task in the project folder.

**To see our most recent results** 
Check out fullexperimentlog.txt for a formatted printout.
The raw result csv's and pdf's can be found in AAexam/app/data/report/result-backup/finalhandinresults.
This is a backup, so you are free to run your own experiments without destroying the source data.

## Structure of the project

We have divided our code into four packages:

**sorting**: All implementations of algorithms for the tasks
**scripts**: Full scripts documenting our implementations, experiments and reflections for each task.
**experiments**: Our experimental framework consisting of Experiments, Measurements & Results. To understand how to use it, see examples in the scripts package, and refer to the JavaDoc.
**data**: A package for handling, manipulating and generating test data.

We have written almost all tests following a given-when-then convention, which should ease understanding of tests.

