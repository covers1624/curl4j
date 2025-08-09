#!/bin/bash

set -e

PLATFORM=macos_arm64 gmake clean
PLATFORM=macos_arm64 gmake build-libcurl
