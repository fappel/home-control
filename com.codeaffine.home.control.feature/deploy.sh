#!/bin/bash

deploy="/Users/FrankAppel/openhab/deploy"
if [ -f "$deploy" ]
then
	rm /Users/FrankAppel/openhab/deploy
else
	touch /Users/FrankAppel/openhab/deploy
	rm /Volumes/openHAB-sys/addons/com.codeaffine.home.control*.jar
	sleep 3
	cp -rf /Users/FrankAppel/openhab/home-control-feature-build/plugins/ /Volumes/openHAB-sys/addons/
	rm -r /Users/FrankAppel/openhab/home-control-feature-build/*
fi