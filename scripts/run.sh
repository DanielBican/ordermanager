#!/bin/bash
echo Building ordermanager
../gradlew -p ../ build
echo Starting docker-compose services
docker-compose up -d
