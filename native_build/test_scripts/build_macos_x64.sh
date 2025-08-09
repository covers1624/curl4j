#!/bin/bash

set -e

PLATFORM=macos_x64 gmake clean
PLATFORM=macos_x64 gmake build-libcurl
