#!/usr/bin/env bash


echo "Starting up RMS"
echo "Starting up Flight"
ssh eschaa1@cs-12.cs.mcgill.ca <<-'ENDSSH'
    cd comp512
    ./startFlight.sh
ENDSSH