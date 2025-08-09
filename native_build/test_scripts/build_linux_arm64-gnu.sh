#!/bin/bash

set -e

SCRIPT_DIR="$( cd -- "$(dirname "$0")" >/dev/null 2>&1 ; pwd -P )"

runInDocker() {
  apt update
  apt install -y --no-install-recommends curl ca-certificates build-essential cmake pkg-config autoconf autotools-dev automake libtool
  useradd -u 1000 asdf
  PLATFORM=linux_arm64 LIBC=gnu runuser -u asdf make clean
  PLATFORM=linux_arm64 LIBC=gnu runuser -u asdf make build-libcurl
  PLATFORM=linux_arm64 "$SCRIPT_DIR"/smoke_test.sh install/libcurl/linux/arm64/libcurl-gnu.so
}

IMAGE="ubuntu:22.04"
PLATFORM="linux/arm64"
source "$SCRIPT_DIR"/docker_helper.sh
