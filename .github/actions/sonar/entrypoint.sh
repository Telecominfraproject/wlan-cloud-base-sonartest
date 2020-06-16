#!/bin/sh -e

pwd
ls -la
echo $INPUT_PROJECT_DIR
# mvn clean verify sonar:sonar $INPUT_SONAR_ARGS