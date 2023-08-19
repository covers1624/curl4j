#!/bin/bash

# If anything breaks, actually break.
set -e

COMMIT=$1
# shellcheck disable=SC2086
SHORT_COMMIT=$(echo $COMMIT | cut -c -7)

ARCH=$2

# Clone
git clone https://github.com/libffi/libffi.git
cd libffi
git checkout "$COMMIT"

# Gen configure
./autogen.sh

# Build
mkdir build
cd build
../configure --disable-docs --disable-shared --enable-static --with-pic "${@:2}"
make

# Build package zip
mkdir libffi
cp .libs/libffi.a libffi/libffi.a
mkdir libffi/include
cp include/*.h libffi/include/
zip -r libffi-"$SHORT_COMMIT"-"$ARCH".zip libffi

#Test code
unzip -l libffi-"$SHORT_COMMIT"-"$ARCH".zip
