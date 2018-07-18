#!/bin/sh
cd /home/ubuntu/Gunter-Discord/
while :
do
	git pull
	mvn clean package -U
	java -jar target/GunterDiscord.jar
done

