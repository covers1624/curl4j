#!/bin/sh

set -e

SCRIPT_DIR="$( cd -- "$(dirname "$0")" >/dev/null 2>&1 ; pwd -P )"

cd "$SCRIPT_DIR/.."

PLATFORM=macos_x64 gmake clean
PLATFORM=macos_x64 gmake build-libcurl
PLATFORM=macos_x64 "$SCRIPT_DIR"/smoke_test.sh install/libcurl/macos/x64/libcurl.dylib
