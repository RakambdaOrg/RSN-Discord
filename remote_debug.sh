#!/usr/bin/env bash
git pull
./gradlew shadowJar
java -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address="*:8080" -jar build/libs/rsn-discord-shaded.jar -c config/config.properties -s config/settings
