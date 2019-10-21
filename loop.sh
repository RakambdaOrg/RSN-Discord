#!/usr/bin/env bash
while :
do
	git pull
	./gradlew shadowJar
	mkdir -p config/settings
	java -jar build/libs/rsn-discord-shaded.jar -c config/config.properties -s config/settings
done

