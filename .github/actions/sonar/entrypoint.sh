#!/bin/sh -e

cd $INPUT_PROJECT_DIR
mvn clean verify sonar:sonar $INPUT_SONAR_ARGS