#!/bin/sh

set -e

runInDocker() {
  apk update
  apk add curl runuser build-base cmake perl linux-headers pkgconf autoconf libtool automake
  adduser -D -u 1000 asdf
  PLATFORM=linux_arm64 LIBC=musl runuser -u asdf make clean
  PLATFORM=linux_arm64 LIBC=musl runuser -u asdf make build-libcurl
}

IMAGE="alpine:3.21.0"
PLATFORM="linux/arm64"
source "$( cd -- "$(dirname "$0")" >/dev/null 2>&1 ; pwd -P )"/docker_helper.sh
