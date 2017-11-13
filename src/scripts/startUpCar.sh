#!/usr/bin/env bash


echo "Starting up RMS"
echo "Starting up Car"
ssh eschaa1@cs-10.cs.mcgill.ca <<-'ENDSSH'
    cd comp512
    ./startCar.sh
ENDSSH