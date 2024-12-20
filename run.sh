#!/bin/bash

cd AAexam || exit
./gradlew run
cd app || exit
python3 plotter.py
