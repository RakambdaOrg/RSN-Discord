#!/usr/bin/env bash
while :
do
	git pull
	mkdir -p config/settings
	./gradlew run --args='-c config/config.properties -s config/settings'
done

