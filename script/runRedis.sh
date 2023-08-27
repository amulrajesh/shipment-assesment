#!/usr/bin/env sh

echo "Running Redis...."

docker container run --publish 3000:6379 redis