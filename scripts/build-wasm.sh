#!/bin/sh

echo "Building distribution..."
./gradlew :composeApp:wasmJsBrowserDistribution --stacktrace --no-daemon

echo "Building docker image..."
docker build -t arnyminerz/escalaralcoiaicomtat-web:development -f standalone.Dockerfile .
