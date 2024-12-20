#!/bin/bash

cd AAexam || exit
./gradlew test
./gradlew run
cd app || exit
python3 plotter.py
