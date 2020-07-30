# Estimating the Covid-19 Infection Fatality Rate

This program uses SEIR modeling, published probabilities of death throughout the course of an infection, and daily death data to estimate the Covid-19 IFR.

When running, use flags to specify inputs:

-f: input file name

-c: boolean indicating that file has cumulative counts (if false, omit flag)

-case: column name of file for case counts

-death: column name of file for death counts

-min: minimum IFR value

-max: maximum IFR value

-m: number of models

-i: number of IFRs

-s: start day

-e: end day

Example: 

java IFR -f nyc-date-cases-hospitalizations-deaths.csv -case CASES -death DEATHS -min 0.01 -max 0.05 -m 30 -i 15 -s 0 -e 100