#!/usr/bin/env bash


echo "Starting up Middleware"
ssh eschaa1@cs-1.cs.mcgill.ca <<-'ENDSSH'
    cd comp512
    ./startMiddleware.sh
ENDSSH