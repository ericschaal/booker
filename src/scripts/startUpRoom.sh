#!/usr/bin/env bash


echo "Starting up RMS"
echo "Starting up Room"
ssh eschaa1@cs-13.cs.mcgill.ca <<-'ENDSSH'
    cd comp512
    ./startRoom.sh
ENDSSH