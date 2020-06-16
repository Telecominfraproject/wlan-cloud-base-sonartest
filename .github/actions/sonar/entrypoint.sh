#!/bin/sh -e

pwd
echo $INPUT_PROJECT_DIR
cd $INPUT_PROJECT_DIR
ls -la
export MAVEN_OPTS=$INPUT_MAVEN_OPTS
printenv
mvn clean verify sonar:sonar $INPUT_SONAR_ARGS