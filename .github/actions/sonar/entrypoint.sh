#!/bin/sh -e

cd $INPUT_PROJECT_DIR
export MAVEN_OPTS=$INPUT_MAVEN_OPTS
mvn clean verify sonar:sonar $INPUT_SONAR_ARGS