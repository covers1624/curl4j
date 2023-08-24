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

# Technically quictls/openssl
openssl_install_dir="$build_dir/openssl"
openssl_version=3.1.2-quic1

ngtcp2_install_dir="$build_dir/ngtcp2"
ngtcp2_version=0.18.0

nghttp3_install_dir="$build_dir/nghttp3"
nghttp3_version=0.14.0

brotli_install_dir="$build_dir/brotli"
brotli_version=1.0.9

zlib_install_dir="$build_dir/zlib"
zlib_version=1.3

zstd_install_dir="$build_dir/zstd"
zstd_version=1.5.5

curl_install_dir="$build_dir/curl"
curl_version=8.2.1
curl_version_under=8_2_1

function main() {
  mkdir -p "$cache_dir"
  rm -rf "$build_dir"
  make_nghttp2
  make_openssl
  make_ngtcp2
  make_nghttp3
  make_brotli
  make_zlib
  #    make_zstd
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

function make_openssl() {
  echo "Making OpenSSL"
  zip="$cache_dir/openssl.zip"
  download_file "$zip" "https://github.com/quictls/openssl/archive/refs/tags/openssl-$openssl_version.zip"
  rm -rf openssl
  unzip "$zip"

  mv openssl-openssl-$openssl_version openssl
  cd openssl

  ./Configure -static --pic --prefix="$openssl_install_dir"
  make -j "$nproc"
  make install_sw
  cd ..
}

function make_ngtcp2() {
  echo "Making ngtpc2"
  zip="$cache_dir/ngtcp2.zip"
  download_file "$zip" "https://github.com/ngtcp2/ngtcp2/archive/refs/tags/v$ngtcp2_version.zip"

  rm -rf ngtcp2
  unzip "$zip"
  mv "ngtcp2-$ngtcp2_version" ngtcp2
  cd ngtcp2

  autoreconf -i

  ./configure \
    --prefix="$ngtcp2_install_dir" \
    --with-pic \
    --enable-lib-only \
    --disable-shared \
    --with-jemalloc=no \
    --with-openssl \
    PKG_CONFIG_PATH="$openssl_install_dir/lib64/pkgconfig"

  make -j "$nproc"
  make install
  cd ..
}

function make_nghttp3() {
  echo "Making nghttp3"
  zip="$cache_dir/nghttp3.zip"
  download_file "$zip" "https://github.com/ngtcp2/nghttp3/archive/refs/tags/v$nghttp3_version.zip"

  rm -rf nghttp3
  unzip "$zip"
  mv "nghttp3-$nghttp3_version" nghttp3
  cd nghttp3

  autoreconf -i

  ./configure \
    --prefix="$nghttp3_install_dir" \
    --with-pic \
    --enable-lib-only \
    --disable-shared

  make -j "$nproc"
  make install
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

  # TODO, disabled for now, Not actually used as a transport encoding, kinda pointless. Also angry at build issues with it.
  # --with-zstd="$zstd_install_dir" \
  ./configure \
    CFLAGS="$cflags" \
    --prefix="$curl_install_dir" \
    --with-nghttp2="$nghttp2_install_dir" \
    --with-brotli="$brotli_install_dir" \
    --with-zlib="$zlib_install_dir" \
    --without-zstd \
    --with-openssl="$openssl_install_dir" \
    --with-ngtcp2="$ngtcp2_install_dir" \
    --with-nghttp3="$nghttp3_install_dir" \
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
  mkdir -p "$licenses_dir/brotli" && cp "$curr_dir/brotli/LICENSE" "$licenses_dir/brotli/LICENSE"
  mkdir -p "$licenses_dir/curl" && cp "$curr_dir/curl/COPYING" "$licenses_dir/curl/COPYING"
  mkdir -p "$licenses_dir/nghttp2" && cp "$curr_dir/nghttp2/COPYING" "$licenses_dir/nghttp2/COPYING"
  mkdir -p "$licenses_dir/nghttp3" && cp "$curr_dir/nghttp3/COPYING" "$licenses_dir/nghttp3/COPYING"
  mkdir -p "$licenses_dir/ngtcp2" && cp "$curr_dir/ngtcp2/COPYING" "$licenses_dir/ngtcp2/COPYING"
  mkdir -p "$licenses_dir/openssl" && cp "$curr_dir/openssl/LICENSE.txt" "$licenses_dir/openssl/LICENSE.txt"
  mkdir -p "$licenses_dir/zlib" && cp "$curr_dir/zlib/LICENSE" "$licenses_dir/zlib/LICENSE"
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
