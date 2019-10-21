#!/usr/bin/env bash
while :
do
	git pull
	./gradlew shadowJar
	java -jar build/libs/rsn-discord.jar -c config.properties -s settings.json
done

