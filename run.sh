#!/bin/bash

# build backend image
cd OptAlgoBackendPlay
sbt docker:publishLocal
cd ..

docker-compose build
docker-compose up
