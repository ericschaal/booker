#!/usr/bin/env bash


echo "Starting up RMS"
echo "Starting up Customers"
ssh -tt eschaa1@cs-11.cs.mcgill.ca <<-'ENDSSH'
    cd comp512
    ./startCustomer.sh
ENDSSH