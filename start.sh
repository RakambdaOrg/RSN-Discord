#!/bin/sh
git pull
mvn package
tmux new -d -s GunterDiscord java -jar target/GunterDiscord.jar
