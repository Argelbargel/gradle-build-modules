#!/usr/bin/env bash

echo "Building branch $TRAVIS_BRANCH (pull-request: $TRAVIS_PULL_REQUEST)..."


GRADLE_TASKS="clean test"
if [ "$TRAVIS_TAG" != "" ]; then
    GRADLE_TASKS="$GRADLE_TASKS build publish"
fi

chmod +x ./gradlew
./gradlew ${GRADLE_TASKS} -Prelease=${TRAVIS_TAG} --no-daemon

if [ "$TRAVIS_TAG" != "" ]; then
    ./travis/publish.sh
    ./gradlew publishPlugins -Prelease=${TRAVIS_TAG} -Dgradle.publish.key=${GRADLE_PUBLISH_KEY} -Dgradle.publish.secret=${GRADLE_PUBLISH_SECRET}
fi

