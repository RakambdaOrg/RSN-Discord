#!/bin/sh
cd /home/ubuntu/Gunter-Discord/
while :
do
	git pull
	mvn package
	java -jar target/GunterDiscord.jar
done

