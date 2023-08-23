#!/bin/bash

# Builds libcur/curl.
#  Enabled protocols: file, ftp, ftps, http, https
#  Features: alt-svc, AsynchDNS, HSTS, HTTP2, HTTP3, HTTPS-proxy, LargeFile, SSL, threadsafe, UnixSockets
#  Compression: brotli, libz, zstd
#  Static libraries: nghttp2, boringssl, quiche, brotli, libz, zstd

#  Expected to be run on ubuntu with the following installed:
#  build-essential (gcc, make), git autoreconf, libtool, cmake, ninja-build zip, unzip, rustup/rustc, golang, python?

# If anything breaks, abort.
set -e

script_dir=$(
  cd "$(dirname "$0")"
  pwd
)

os=$(uname -s)

if [ "$os" == "Darwin" ]; then
  export MACOSX_DEPLOYMENT_TARGET=11.0
fi

suffix=$1

curr_dir=$(pwd)
build_dir="$curr_dir/build"
cache_dir="$curr_dir/cache"

nproc=$(getconf _NPROCESSORS_ONLN)

nghttp2_install_dir="$build_dir/nghttp2"
nghttp2_version=1.55.1

boringssl_install_dir="$build_dir/boringssl"
boringssl_version=3a667d10e94186fd503966f5638e134fe9fb4080

brotli_install_dir="$build_dir/brotli"
brotli_version=1.0.9

zlib_install_dir="$build_dir/zlib"
zlib_version=1.3

zstd_install_dir="$build_dir/zstd"
zstd_version=1.5.5

quiche_install_dir="$build_dir/quiche"
quiche_version=0.17.2

curl_install_dir="$build_dir/curl"
curl_version=8.2.1
curl_version_under=8_2_1

function main() {
  mkdir -p "$cache_dir"
  rm -rf "$build_dir"
  make_nghttp2
  make_boringssl
  make_brotli
  make_zlib
  make_zstd
  make_quiche
  make_curl

  package
  if [ -n "$FILE_SERVER" ]; then
    upload
  fi
}

function make_nghttp2() {
  echo "Making nghttp2.."
  zip="$cache_dir/nghttp2.tar.bz2"
  download_file "$zip" "https://github.com/nghttp2/nghttp2/releases/download/v$nghttp2_version/nghttp2-$nghttp2_version.tar.bz2"

  rm -rf nghttp2
  tar -xvf "$zip"
  mv "nghttp2-$nghttp2_version" nghttp2
  cd nghttp2

  ./configure --with-pic --enable-lib-only --disable-shared --disable-python-bindings --prefix="$nghttp2_install_dir"
  make -j "$nproc"
  make install
  cd ..
}

function make_boringssl() {
  echo "Making BoringSSL"
  zip="$cache_dir/boringssl.zip"
  download_file "$zip" "https://github.com/google/boringssl/archive/$boringssl_version.zip"

  rm -rf boringssl
  unzip "$zip"
  mv "boringssl-$boringssl_version" boringssl
  cd boringssl

  cmake \
    -DCMAKE_BUILD_TYPE=Release \
    -DCMAKE_POSITION_INDEPENDENT_CODE=on \
    -DCMAKE_C_FLAGS="-Wno-unknown-warning-option -Wno-stringop-overflow -Wno-array-bounds" \
    -GNinja

  cmake --build . --target ssl --target crypto
  mkdir -p "$boringssl_install_dir"
  cp -R include "$boringssl_install_dir"
  mkdir "$boringssl_install_dir/lib"
  cp ssl/libssl.a "$boringssl_install_dir/lib/"
  cp crypto/libcrypto.a "$boringssl_install_dir/lib/"

  cd ..
}

function make_brotli() {
  echo "Making brotli"
  zip="$cache_dir/brotli.tar.gz"
  download_file "$zip" "https://github.com/google/brotli/archive/refs/tags/v$brotli_version.tar.gz"

  rm -rf brotli
  tar -xvf "$zip"
  mv "brotli-$brotli_version" brotli
  cd brotli

  cmake -DCMAKE_BUILD_TYPE=Release -DCMAKE_INSTALL_PREFIX="$brotli_install_dir" -GNinja
  cmake --build . --config Release --target install
  cd ..
}

function make_zlib() {
  echo "Making zlib"
  zip="$cache_dir/zlib.tar.gz"
  download_file "$zip" "https://github.com/madler/zlib/releases/download/v$zlib_version/zlib-$zlib_version.tar.gz"

  rm -rf zlib
  tar -xvf "$zip"
  mv "zlib-$zlib_version" zlib
  cd zlib

  ./configure --static --prefix="$zlib_install_dir"
  make install -j "$nproc"

  cd ..
}

function make_zstd() {
  echo "Making zstandard"
  zip="$cache_dir/zstd.tar.gz"
  download_file "$zip" "https://github.com/facebook/zstd/releases/download/v$zstd_version/zstd-$zstd_version.tar.gz"

  rm -rf zstd
  tar -xvf "$zip"
  mv "zstd-$zstd_version" zstd
  cd zstd

  PREFIX="$zstd_install_dir" make lib-mt install CFLAGS="-DZSTD_MULTITHREAD -fPIC" -j "$nproc"
  rm "$zstd_install_dir/lib/"libzstd.so* || rm "$zstd_install_dir/lib/"libzstd.dylib*

  cd ..
}

function make_quiche() {
  echo "Making Quiche"
  zip="$cache_dir/quiche.tar.gz"
  download_file "$zip" "https://github.com/cloudflare/quiche/archive/refs/tags/$quiche_version.tar.gz"

  rm -rf quiche
  tar -xvf "$zip"
  mv "quiche-$quiche_version" quiche
  cd quiche

  # APPARENTLY, Patch is braindead and does not actually care about the path in the a/b def of the patch.
  cd quiche
  # Patch quiche to use alpha version of ring, fixes issues on mac.
  patch -i "$script_dir/patches/quiche/patch-0000-quiche-use-ring-0.17.0.patch"
  cd ..

  QUICHE_BSSL_PATH="$boringssl_install_dir/lib/" cargo build --package quiche --release --features ffi,pkg-config-meta --target-dir "$quiche_install_dir"
  rm "$quiche_install_dir/release/libquiche.so" || rm "$quiche_install_dir/release/libquiche.dylib"

  cd ..
}

function make_curl() {
  echo "Making curl"
  zip="$cache_dir/curl.tar.bz2"
  download_file "$zip" "https://github.com/curl/curl/releases/download/curl-$curl_version_under/curl-$curl_version.tar.bz2"

  rm -rf curl
  tar -xvf "$zip"
  mv "curl-$curl_version" curl
  cd curl

  # Patch curl to use brotli static.
  patch -i "$script_dir/patches/curl/patch-0000-use-brotli-static.patch"

  autoreconf -fi

  cflags=""
  if [ "$os" == Linux ]; then
    cflags="-static-libgcc"
  fi

  ./configure \
    CFLAGS="$cflags" \
    LDFLAGS="-Wl,-L$quiche_install_dir/release" \
    --prefix="$curl_install_dir" \
    --with-nghttp2="$nghttp2_install_dir" \
    --with-brotli="$brotli_install_dir" \
    --with-zlib="$zlib_install_dir" \
    --with-zstd="$zstd_install_dir" \
    --with-openssl="$boringssl_install_dir" \
    --with-quiche="$quiche_install_dir/release" \
    --disable-manual \
    --disable-static \
    --disable-dict \
    --disable-gopher \
    --disable-imap \
    --disable-ldap \
    --disable-ldaps \
    --disable-mqtt \
    --disable-pop3 \
    --disable-rtsp \
    --disable-smb \
    --disable-smtp \
    --disable-telnet \
    --disable-tftp \
    --disable-ntlm \
    --without-librtmp \
    --without-libidn2 \
    --without-libpsl

  make -j "$nproc"
  make install
  cd ..
}

function package() {
  echo "Packaging.."
  dir="$build_dir/packaging"
  libcurl_dir="$dir/libcurl"
  mkdir -p "$libcurl_dir"
  if [ "$os" == "Linux" ]; then
    cp "$build_dir/curl/lib/libcurl.so.4.8.0" "$libcurl_dir/libcurl.so"
  elif [ "$os" == "Darwin" ]; then
    cp "$build_dir/curl/lib/libcurl.4.dylib" "$libcurl_dir/libcurl.dylib"
  else
    echo "I don't know where libcurl lives on $os"
    exit 1
  fi
  libcurl_zip_name="libcurl-$curl_version-$suffix.zip"
  libcurl_zip="$dir/$libcurl_zip_name"
  cd "$libcurl_dir"
  echo "Packaging libcurl.."
  zip -r "$libcurl_zip" ./*
  libcurl_checksum=$(sha256sum "$libcurl_zip" | cut -d " " -f 1)
  echo "SHA256: $libcurl_checksum"
  echo "$libcurl_checksum" >"$libcurl_zip.sha256"

  licenses_dir="$dir/licenses"
  mkdir -p "$licenses_dir/boringssl" && cp "$curr_dir/boringssl/LICENSE" "$licenses_dir/boringssl/LICENSE"
  mkdir -p "$licenses_dir/brotli" && cp "$curr_dir/brotli/LICENSE" "$licenses_dir/brotli/LICENSE"
  mkdir -p "$licenses_dir/curl" && cp "$curr_dir/curl/COPYING" "$licenses_dir/curl/COPYING"
  mkdir -p "$licenses_dir/nghttp2" && cp "$curr_dir/nghttp2/COPYING" "$licenses_dir/nghttp2/COPYING"
  mkdir -p "$licenses_dir/quiche" && cp "$curr_dir/quiche/COPYING" "$licenses_dir/quiche/COPYING"
  mkdir -p "$licenses_dir/zlib" && cp "$curr_dir/zlib/LICENSE" "$licenses_dir/zlib/LICENSE"
  mkdir -p "$licenses_dir/zstd" && cp "$curr_dir/zstd/LICENSE" "$licenses_dir/zstd/LICENSE"
  mkdir -p "$licenses_dir/zstd" && cp "$curr_dir/zstd/COPYING" "$licenses_dir/zstd/COPYING"
  licenses_zip_name="libcurl-$curl_version-$suffix-licenses.zip"
  licenses_zip="$dir/$licenses_zip_name"
  cd "$licenses_dir"
  echo "Packaging libcurl & dependencies licenses.."
  zip -r "$licenses_zip" ./*

  licenses_checksum=$(sha256sum "$licenses_zip" | cut -d " " -f 1)
  echo "SHA256: $licenses_checksum"
  echo "$licenses_checksum" >"$licenses_zip.sha256"

  cd "$curr_dir"
}

function upload() {
  echo "Uploading.."
  curl --insecure --user "$FILE_SERVER_USER:$FILE_SERVER_PASSWORD" -T "$libcurl_zip" "sftp://$FILE_SERVER/libcurl/$libcurl_zip_name"
  curl --insecure --user "$FILE_SERVER_USER:$FILE_SERVER_PASSWORD" -T "$libcurl_zip.sha256" "sftp://$FILE_SERVER/libcurl/$libcurl_zip_name.sha256"
  curl --insecure --user "$FILE_SERVER_USER:$FILE_SERVER_PASSWORD" -T "$licenses_zip" "sftp://$FILE_SERVER/libcurl/$licenses_zip_name"
  curl --insecure --user "$FILE_SERVER_USER:$FILE_SERVER_PASSWORD" -T "$licenses_zip.sha256" "sftp://$FILE_SERVER/libcurl/$licenses_zip_name.sha256"
}

download_file() {
  dest=$1
  url=$2
  if [ ! -f "$dest" ]; then
    echo "Downloading $url -> $dest"
    curl -Lo "$dest" "$url"
  fi
}

main
