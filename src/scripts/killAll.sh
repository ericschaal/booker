#!/usr/bin/env bash

ssh eschaa1@cs-10.cs.mcgill.ca <<-'ENDSSH'
kill $(pgrep -f java)
kill $(pgrep -f SCREEN)
ENDSSH

ssh eschaa1@cs-11.cs.mcgill.ca <<-'ENDSSH'
kill $(pgrep -f java)
kill $(pgrep -f SCREEN)
ENDSSH

ssh eschaa1@cs-12.cs.mcgill.ca <<-'ENDSSH'
kill $(pgrep -f java)
kill $(pgrep -f SCREEN)
ENDSSH

ssh eschaa1@cs-13.cs.mcgill.ca <<-'ENDSSH'
kill $(pgrep -f java)
kill $(pgrep -f SCREEN)
ENDSSH

ssh eschaa1@cs-1.cs.mcgill.ca <<-'ENDSSH'
kill $(pgrep -f java)
kill $(pgrep -f SCREEN)
ENDSSH




