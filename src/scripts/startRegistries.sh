#!/usr/bin/env bash

ssh eschaa1@cs-10.cs.mcgill.ca <<-'ENDSSH'
cd comp512
screen -d -m ./startRegistry.sh
ENDSSH

ssh eschaa1@cs-11.cs.mcgill.ca <<-'ENDSSH'
cd comp512
screen -d -m ./startRegistry.sh
ENDSSH

ssh eschaa1@cs-12.cs.mcgill.ca <<-'ENDSSH'
cd comp512
screen -d -m ./startRegistry.sh
ENDSSH

ssh eschaa1@cs-13.cs.mcgill.ca <<-'ENDSSH'
cd comp512
screen -d -m ./startRegistry.sh
ENDSSH

ssh eschaa1@cs-1.cs.mcgill.ca <<-'ENDSSH'
cd comp512
screen -d -m ./startRegistry.sh
ENDSSH

