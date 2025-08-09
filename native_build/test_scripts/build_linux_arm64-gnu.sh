#!/bin/bash

set -e

runInDocker() {
  apt update
  apt install -y --no-install-recommends curl ca-certificates build-essential cmake pkg-config autoconf autotools-dev automake libtool
  useradd -u 1000 asdf
  PLATFORM=linux_arm64 LIBC=gnu runuser -u asdf make clean
  PLATFORM=linux_arm64 LIBC=gnu runuser -u asdf make build-libcurl
}

IMAGE="ubuntu:22.04"
PLATFORM="linux/arm64"
source "$( cd -- "$(dirname "$0")" >/dev/null 2>&1 ; pwd -P )"/docker_helper.sh
