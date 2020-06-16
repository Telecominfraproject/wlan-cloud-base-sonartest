#!/bin/sh -e

ls -la wlan-cloud-base-sonartest/wlan-cloud-base-sonartest/
cd $INPUT_PROJECT_DIR
mvn clean verify sonar:sonar $INPUT_SONAR_ARGS