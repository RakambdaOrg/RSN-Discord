#!/bin/sh
while :
do
	git pull
	mvn clean package -U
	java --enable-preview -jar target/GunterDiscord.jar
done

