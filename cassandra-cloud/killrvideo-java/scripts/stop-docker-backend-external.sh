#!/bin/bash

# command to bring down supporting KillrVideo infrastructure started via
# `run-docker-backend-external.sh`

export KILLRVIDEO_BACKEND=192.168.0.59
export KILLRVIDEO_YOUTUBE_API_KEY=AIzaSyCuvspiQtqIIwSy3eiYAkfiQsrCy-XpBvQ

docker-compose -f docker-compose-backend-external.yaml down

$SHELL