#!/bin/sh -l

mvn clean verify sonar:sonar -Dsonar.host.url=${{ env.SONAR_URL }} -Dsonar.login=${{ secrets.SONAR_LOGIN }} -Dsonar.organization=${{ env.SONAR_ORGANIZATION }} -Dsonar.projectKey=${{ env.SONAR_PROJECT_KEY }}