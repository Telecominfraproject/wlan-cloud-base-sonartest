#!/bin/sh -e

pwd
echo $INPUT_PROJECT_DIR
cd $INPUT_PROJECT_DIR
ls -la
mvn clean verify sonar:sonar $INPUT_SONAR_ARGS
