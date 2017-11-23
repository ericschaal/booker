#!/usr/bin/env bash

echo "Uploading binaries to cs-10.cs.mcgill.ca"
scp ./build/libs/booker-1.0-SNAPSHOT.jar eschaa1@cs-10.cs.mcgill.ca:~/comp512/booker.jar
scp ./src/security-remote.policy eschaa1@cs-10.cs.mcgill.ca:~/comp512/security.policy

