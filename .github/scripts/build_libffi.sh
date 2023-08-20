#!/bin/bash

# If anything breaks, actually break.
set -e

commit=$1
# shellcheck disable=SC2086
short_commit=$(echo $commit | cut -c -7)

suffix=$2

# Clone
git clone https://github.com/libffi/libffi.git
cd libffi
git checkout "$commit"

# Gen configure
./autogen.sh

# Build
mkdir build
cd build
../configure --disable-docs --disable-shared --enable-static --with-pic "${@:3}"
make

# Build package zip
mkdir libffi
cp .libs/libffi.a libffi/libffi.a
mkdir libffi/include
cp include/*.h libffi/include/
zip_name=libffi-"$short_commit"-"$suffix".zip
zip -r "$zip_name" libffi
# Print zip contents for giggles.
unzip -l "$zip_name"

checksum=$(sha256sum "$zip_name" | cut -d " " -f 1)
echo "SHA256: $checksum"

# Write checksum file
echo "$checksum" >"$zip_name".sha256

#Upload to file server
if [ -n "$FILE_SERVER" ]; then
  curl --insecure --user "$FILE_SERVER_USER:$FILE_SERVER_PASSWORD" -T "$zip_name" "sftp://$FILE_SERVER/libffi/$zip_name"
  curl --insecure --user "$FILE_SERVER_USER:$FILE_SERVER_PASSWORD" -T "$zip_name".sha256 "sftp://$FILE_SERVER/libffi/$zip_name".sha256
fi
