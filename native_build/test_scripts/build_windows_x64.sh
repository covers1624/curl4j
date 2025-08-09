#!/bin/bash

SCRIPT_DIR="$( cd -- "$(dirname "$0")" >/dev/null 2>&1 ; pwd -P )"

set -e

runInDocker() {
  apt update
  apt install -y --no-install-recommends curl ca-certificates build-essential cmake pkg-config autoconf autotools-dev automake libtool mingw-w64 wine
  useradd -u 1000 asdf
  PLATFORM=windows_x64 runuser -u asdf make clean
  PLATFORM=windows_x64 runuser -u asdf make build-libcurl
  "$SCRIPT_DIR"/smoke_test.sh install/libcurl/windows/x64/curl.dll
}

IMAGE="ubuntu:22.04"
PLATFORM="linux/amd64"
source "$SCRIPT_DIR"/docker_helper.sh
