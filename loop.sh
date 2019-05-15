#!/bin/sh
while :
do
	git pull
	mvn clean package -U
	java -jar target/RSNDiscord.jar -c config.properties -s settings.json
done

