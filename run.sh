#!/bin/bash

# build backend image
cd bin-packing-backend-play
sbt docker:publishLocal
cd ..

docker-compose build
docker-compose up
