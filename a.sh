#!/usr/bin/zsh
./gradlew build
rm build/libs/*-sources.jar
mv build/libs/*.jar ../pulse/run/mods