#!/bin/sh

set -e

SCRIPT_DIR="$( cd -- "$(dirname "$0")" >/dev/null 2>&1 ; pwd -P )"

runInDocker() {
  apk update
  apk add curl runuser build-base cmake samurai perl linux-headers pkgconf
  adduser -D -u 1000 asdf
  PLATFORM=linux_arm64 LIBC=musl runuser -u asdf make clean
  PLATFORM=linux_arm64 LIBC=musl runuser -u asdf make build-libcurl
  PLATFORM=linux_arm64 "$SCRIPT_DIR"/smoke_test.sh install/libcurl/linux/arm64/libcurl-musl.so
}

IMAGE="alpine:3.21.0"
PLATFORM="linux/arm64"
source "$SCRIPT_DIR"/docker_helper.sh
