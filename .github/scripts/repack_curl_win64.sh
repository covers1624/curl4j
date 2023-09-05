#!/bin/bash

# If anything breaks, abort.
set -e

curr_dir=$(pwd)

version="8.2.1"
build="7"
ver_str="$version"_"$build"

suffix="windows_x64"

dist_zip_name="curl-$ver_str-win64-mingw"

dist_zip="$curr_dir/$dist_zip_name.zip"
dist_dir="$curr_dir/$dist_zip_name"

rm -rf "$dist_dir"

curl -Lo "$dist_zip" "https://curl.se/windows/dl-$ver_str/$dist_zip_name.zip"

unzip "$dist_zip"

package_dir="$curr_dir/package"

libcurl_zip_name="libcurl-$version-$suffix.zip"
libcurl_zip="$curr_dir/$libcurl_zip_name"

libcurl_dir="$package_dir/libcurl"

mkdir -p "$libcurl_dir"
cp -r "$dist_dir/bin/libcurl-x64.dll" "$libcurl_dir/curl.dll"

echo "Packaging libcurl.."
cd "$libcurl_dir"
zip -r "$libcurl_zip" ./*

libcurl_checksum=$(sha256sum "$libcurl_zip" | cut -d " " -f 1)
echo "SHA256: $libcurl_checksum"
echo "$libcurl_checksum" >"$libcurl_zip.sha256"

licenses_dir="$package_dir/licenses"
mkdir -p "$licenses_dir"

cp -r "$dist_dir/dep/"* "$licenses_dir/"

licenses_zip_name="libcurl-$version-$suffix-licenses.zip"
licenses_zip="$curr_dir/$licenses_zip_name"

cd "$licenses_dir"
zip -r "$licenses_zip" ./*

licenses_checksum=$(sha256sum "$licenses_zip" | cut -d " " -f 1)
echo "SHA256: $licenses_checksum"
echo "$licenses_checksum" >"$licenses_zip.sha256"

curl --insecure --user "$FILE_SERVER_USER:$FILE_SERVER_PASSWORD" -T "$libcurl_zip" "sftp://$FILE_SERVER/libcurl/$libcurl_zip_name"
curl --insecure --user "$FILE_SERVER_USER:$FILE_SERVER_PASSWORD" -T "$libcurl_zip.sha256" "sftp://$FILE_SERVER/libcurl/$libcurl_zip_name.sha256"
curl --insecure --user "$FILE_SERVER_USER:$FILE_SERVER_PASSWORD" -T "$licenses_zip" "sftp://$FILE_SERVER/libcurl/$licenses_zip_name"
curl --insecure --user "$FILE_SERVER_USER:$FILE_SERVER_PASSWORD" -T "$licenses_zip.sha256" "sftp://$FILE_SERVER/libcurl/$licenses_zip_name.sha256"
