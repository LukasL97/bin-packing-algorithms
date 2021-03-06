#!/bin/bash

# deploy backend
git subtree push --prefix bin-packing-backend-play/ heroku main

# deploy frontend
cd bin-packing-frontend-react
npm run deploy
cd ..
