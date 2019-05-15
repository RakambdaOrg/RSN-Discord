#!/bin/sh
while :
do
	git pull
	mvn clean package -U
	java -jar target/RSNDiscord.jar -tf gunter_token -s settings.json
done

