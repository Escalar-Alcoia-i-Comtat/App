#!/bin/sh

cd $CI_PRIMARY_REPOSITORY_PATH

touch local.properties

echo "SENTRY_AUTH_TOKEN=$SENTRY_AUTH_TOKEN" > local.properties
echo "MAPBOX_ACCESS_TOKEN=$MAPBOX_ACCESS_TOKEN" > local.properties
echo "GITHUB_TOKEN=$GITHUB_TOKEN" > local.properties
