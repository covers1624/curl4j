.ONESHELL:

# === common ===
flavour=$(shell echo "$(PLATFORM)" | cut -d_ -f1)
arch=$(shell echo "$(PLATFORM)" | cut -d_ -f2)

curr_dir=$(shell pwd)
scripts_dir=$(curr_dir)/../.github/scripts
build_dir=$(curr_dir)/build
install_dir=$(curr_dir)/install
cache_dir=$(curr_dir)/cache
linux_headers=$(install_dir)/linux/include

# === VERSIONS ===
musl_version=1.2.4
musl_archive=$(cache_dir)/musl-$(musl_version).tar.gz
musl_build=$(build_dir)/musl
musl_install=$(install_dir)/musl

zlib_version=1.3
zlib_archive=$(cache_dir)/zlib-$(zlib_version).tar.gz
zlib_build=$(build_dir)/zlib
zlib_install=$(install_dir)/zlib

brotli_version=1.1.0
brotli_archive=$(cache_dir)/brotli-$(brotli_version).tar.gz
brotli_build=$(build_dir)/brotli
brotli_install=$(install_dir)/brotli

quictls_version=3.1.2-quic1
quictls_archive=$(cache_dir)/quictls-$(quictls_version).tar.gz
quictls_build=$(build_dir)/quictls
quictls_install=$(install_dir)/quictls

nghttp2_version=1.55.1
nghttp2_archive=$(cache_dir)/nghttp2-$(nghttp2_version).tar.gz
nghttp2_build=$(build_dir)/nghttp2
nghttp2_install=$(install_dir)/nghttp2

ngtcp2_version=0.18.0
ngtcp2_archive=$(cache_dir)/ngtcp2-$(ngtcp2_version).tar.gz
ngtcp2_build=$(build_dir)/ngtcp2
ngtcp2_install=$(install_dir)/ngtcp2

nghttp3_version=0.14.0
nghttp3_archive=$(cache_dir)/nghttp3-$(nghttp3_version).tar.gz
nghttp3_build=$(build_dir)/nghttp3
nghttp3_install=$(install_dir)/nghttp3

curl_version=8.2.1
curl_archive=$(cache_dir)/curl-$(curl_version).tar.gz
curl_build=$(build_dir)/curl
curl_install=$(install_dir)/curl

libcurl_install=$(install_dir)/libcurl
libcurl_zip=$(install_dir)/libcurl-$(curl_version)-$(flavour)_$(arch).zip

# === state ===
nproc=$(shell getconf _NPROCESSORS_ONLN)
COMPILER_DEPS=
C_COMPILER=gcc

CMAKE_ARGS=
CFLAGS=
LDFLAGS=
QUICTLS_ARGS=

all: $(libcurl_zip)

# === OS Specific configuration ===
# Linux
ifeq (linux, $(flavour))
ifeq (x64, $(arch))
HOST=x86_64-pc-linux-musl
MUSL_CC=gcc
QUICTLS_ARGS=linux-x86_64
else
HOST=aarch64-pc-linux-musl
MUSL_CC=aarch64-linux-gnu-gcc-10
QUICTLS_ARGS=linux-aarch64
endif

# Windows
else ifeq (windows, $(flavour))
# Bail arm64 windows. Not currently supported.
ifeq (arm64, $(arch))
$(error Arm64 Windows not currently supported.)
endif

HOST=x86_64-w64-mingw32
C_COMPILER=x86_64-w64-mingw32-gcc-posix
WINDRES=x86_64-w64-mingw32-windres
CMAKE_ARGS=-DCMAKE_SYSTEM_NAME=Windows -DCMAKE_FIND_ROOT_PATH=/usr/x86_64-w64-mingw32
LDFLAGS=--static -static-libgcc
QUICTLS_ARGS=mingw64

# Macos
else ifeq (macos, $(flavour))
C_COMPILER=clang

ifeq (x64, $(arch))
HOST=x86_64-apple-darwin20.6.0
QUICTLS_ARGS=darwin64-x86_64
else
HOST=aarch64-apple-darwin20.6.0
CMAKE_ARGS=-DCMAKE_OSX_ARCHITECTURES=arm64
CFLAGS=--target=aarch64-apple-darwin20.6.0
QUICTLS_ARGS=darwin64-arm64
endif

else
$(error Unknown Operating System: $(flavour))
endif

# === musl ===
ifeq (linux, $(flavour))
COMPILER_DEPS=$(musl_install)
C_COMPILER=$(musl_install)/bin/musl-gcc
CMAKE_ARGS=-DCMAKE_FIND_ROOT_PATH="$(musl_install)"

$(musl_archive):
	@"$(scripts_dir)/download.sh" "https://musl.libc.org/releases/musl-$(musl_version).tar.gz" "$(musl_archive)"

$(musl_build): $(musl_archive)
	mkdir -p "$(build_dir)"
	tar -xvf "$(musl_archive)" -C "$(build_dir)"
	mv "$(build_dir)/musl-$(musl_version)/" "$(musl_build)"
	patch -N -p1 -d "$(musl_build)" -r /dev/null -i "$(scripts_dir)/patches/musl/0000-libc-lazy-init.patch"

$(musl_install): $(musl_build)
	set -e
	cd "$(musl_build)"
	CC="$(MUSL_CC)" CFLAGS="-fPIC" ./configure --disable-shared --prefix="$(musl_install)"
	$(MAKE) -j $(nproc)
	$(MAKE) install
endif

clean-musl:
	rm -rf "$(musl_build)"
	rm -rf "$(musl_install)"

# === zlib ===
$(zlib_archive):
	@"$(scripts_dir)/download.sh" "https://github.com/madler/zlib/releases/download/v$(zlib_version)/zlib-$(zlib_version).tar.gz" "$(zlib_archive)"

$(zlib_build): $(zlib_archive)
	mkdir -p "$(build_dir)"
	tar -xvf "$(zlib_archive)" -C "$(build_dir)"
	mv "$(build_dir)/zlib-$(zlib_version)/" "$(zlib_build)/"

$(zlib_install): $(COMPILER_DEPS) $(zlib_build)
	set -e
	cd "$(zlib_build)"
	CC="$(C_COMPILER)" CFLAGS="-fPIC $(CFLAGS)" \
		./configure \
		--static \
		--prefix="$(zlib_install)"
	$(MAKE) -j $(nproc)
	$(MAKE) install

clean-zlib:
	rm -rf $(zlib_build)
	rm -rf $(zlib_install)

# === brotli ===
$(brotli_archive):
	@"$(scripts_dir)/download.sh" "https://github.com/google/brotli/archive/refs/tags/v$(brotli_version).tar.gz" "$(brotli_archive)"

$(brotli_build): $(brotli_archive)
	mkdir -p "$(build_dir)"
	tar -xvf "$(brotli_archive)" -C "$(build_dir)"
	mv "$(build_dir)/brotli-$(brotli_version)/" "$(brotli_build)/"

$(brotli_install): $(COMPILER_DEPS) $(brotli_build)
	set -e
	cd "$(brotli_build)"
	CC="$(C_COMPILER)" cmake \
		-DCMAKE_BUILD_TYPE=Release \
		-DCMAKE_INSTALL_PREFIX="$(brotli_install)" \
		-DBUILD_SHARED_LIBS=OFF \
		$(CMAKE_ARGS)
	cmake --build . --config Release
	$(MAKE) -j $(nproc)
	$(MAKE) install

clean-brotli:
	rm -rf $(brotli_build)
	rm -rf $(brotli_install)

# === QuicTLS/OpenSSL ===
$(quictls_archive):
	@"$(scripts_dir)/download.sh" "https://github.com/quictls/openssl/archive/refs/tags/openssl-$(quictls_version).tar.gz" "$(quictls_archive)"

$(quictls_build): $(quictls_archive)
	mkdir -p "$(build_dir)"
	tar -xvf "$(quictls_archive)" -C "$(build_dir)"
	mv "$(build_dir)/openssl-openssl-$(quictls_version)/" "$(quictls_build)/"

$(quictls_install): $(COMPILER_DEPS) $(linux_headers) $(quictls_build)
	set -e
	cd "$(quictls_build)"
	CC="$(C_COMPILER)" CFLAGS="-I$(linux_headers)" WINDRES=$(WINDRES) ./Configure \
		no-tests \
		-fpic \
		--prefix=$(quictls_install) \
		-DOPENSSL_DEV_NO_ATOMICS=true \
		-D__STDC_NO_ATOMICS__=true \
		$(QUICTLS_ARGS)
	$(MAKE) -j $(nproc)
	$(MAKE) install_sw

clean-quictls:
	rm -rf $(quictls_build)
	rm -rf $(quictls_install)

# === nghttp2 ===
$(nghttp2_archive):
	@"$(scripts_dir)/download.sh" "https://github.com/nghttp2/nghttp2/releases/download/v$(nghttp2_version)/nghttp2-$(nghttp2_version).tar.gz" "$(nghttp2_archive)"

$(nghttp2_build): $(nghttp2_archive)
	mkdir -p "$(build_dir)"
	tar -xvf "$(nghttp2_archive)" -C "$(build_dir)"
	mv "$(build_dir)/nghttp2-$(nghttp2_version)/" "$(nghttp2_build)/"

$(nghttp2_install): $(COMPILER_DEPS) $(nghttp2_build)
	set -e
	cd "$(nghttp2_build)"
	CC="$(C_COMPILER)" CFLAGS="$(CFLAGS)" ./configure \
		--host=$(HOST) \
		--with-pic \
		--enable-lib-only \
		--disable-shared \
		--disable-python-bindings \
		--prefix="$(nghttp2_install)"
	$(MAKE) -j $(nproc)
	$(MAKE) install

clean-nghttp2:
	rm -rf $(nghttp2_build)
	rm -rf $(nghttp2_install)

# === ngtcp2 ===
$(ngtcp2_archive):
	@"$(scripts_dir)/download.sh" "https://github.com/ngtcp2/ngtcp2/releases/download/v$(ngtcp2_version)/ngtcp2-$(ngtcp2_version).tar.gz" "$(ngtcp2_archive)"

$(ngtcp2_build): $(ngtcp2_archive)
	mkdir -p "$(build_dir)"
	tar -xvf "$(ngtcp2_archive)" -C "$(build_dir)"
	mv "$(build_dir)/ngtcp2-$(ngtcp2_version)/" "$(ngtcp2_build)/"

$(ngtcp2_install): $(COMPILER_DEPS) $(ngtcp2_build) $(quictls_install)
	set -e
	cd "$(ngtcp2_build)"
	CC="$(C_COMPILER)" CFLAGS="$(CFLAGS)" ./configure \
		--host=$(HOST) \
		--prefix="$(ngtcp2_install)" \
		--with-pic \
		--enable-lib-only \
		--disable-shared \
		--with-jemalloc=no \
		--with-openssl \
		PKG_CONFIG_PATH="$(quictls_install)/lib64/pkgconfig:$(quictls_install)/lib/pkgconfig"
	$(MAKE) -j $(nproc)
	$(MAKE) install

clean-ngtcp2:
	rm -rf $(ngtcp2_build)
	rm -rf $(ngtcp2_install)

# === nghttp3 ===
$(nghttp3_archive):
	@"$(scripts_dir)/download.sh" "https://github.com/ngtcp2/nghttp3/releases/download/v$(nghttp3_version)/nghttp3-$(nghttp3_version).tar.gz" "$(nghttp3_archive)"

$(nghttp3_build): $(nghttp3_archive)
	mkdir -p "$(build_dir)"
	tar -xvf "$(nghttp3_archive)" -C "$(build_dir)"
	mv "$(build_dir)/nghttp3-$(nghttp3_version)/" "$(nghttp3_build)/"

$(nghttp3_install): $(COMPILER_DEPS) $(nghttp3_build)
	set -e
	cd "$(nghttp3_build)"
	CC="$(C_COMPILER)" CFLAGS="$(CFLAGS)" ./configure \
		--host=$(HOST) \
		--prefix="$(nghttp3_install)" \
		--disable-shared \
		--with-pic \
		--enable-lib-only
	$(MAKE) -j $(nproc)
	$(MAKE) install

clean-nghttp3:
	rm -rf $(nghttp3_build)
	rm -rf $(nghttp3_install)

# === curl ===
$(curl_archive):
	@"$(scripts_dir)/download.sh" "https://github.com/curl/curl/releases/download/curl-$(shell echo "$(curl_version)" | tr '.' '_')/curl-$(curl_version).tar.gz" "$(curl_archive)"

$(curl_build): $(curl_archive)
	mkdir -p "$(build_dir)"
	tar -xvf "$(curl_archive)" -C "$(build_dir)"
	mv "$(build_dir)/curl-$(curl_version)/" "$(curl_build)/"
	cd "$(curl_build)"

# TODO On Linux we use automake, on Windows/Mingw/mac we use cmake.
#      As of writing this the following issues are present:
# 		- Windows/Mingw on automake does not emit a libcurl.dll, at all.
#		- Linux-x64 on cmake works just fine
#		- Linux-aarch64 on cmake does not link correctly and emits a broken assembly.
#		  - During OpenSSL static init, several calls to sigsetjmp are made, however, it incorrectly links setjmp.
#			Ultimately causing a Bus error as execution runs off into fucking narnia.
ifneq (,$(findstring linux, $(HOST)))
$(curl_install): $(COMPILER_DEPS) $(zlib_install) $(brotli_install) $(quictls_install) $(nghttp2_install) $(ngtcp2_install) $(nghttp3_install) $(curl_build)
	set -e
	cd "$(curl_build)"
	patch -N -p1 -r /dev/null -i "$(scripts_dir)/patches/curl/patch-0000-brotli-1.1.0-static.patch"
	autoreconf -fi
	# TODO, Required due to the hack required for mingw bellow.
	rm "$(quictls_install)"/lib*/*.so || true
	./configure \
		CC="$(C_COMPILER)" \
		--host=$(HOST) \
		--prefix="$(curl_install)" \
		--with-zlib=$(zlib_install) \
		--with-brotli=$(brotli_install) \
		--with-openssl=$(quictls_install) \
		--with-nghttp2=$(nghttp2_install) \
		--with-ngtcp2=$(ngtcp2_install) \
		--with-nghttp3=$(nghttp3_install) \
		--enable-pthreads \
		--disable-manual \
		--disable-static \
		--without-librtmp \
		--without-libidn2 \
		--without-libpsl
	$(MAKE) -j $(nproc)
	$(MAKE) install
else
$(curl_install): $(COMPILER_DEPS) $(zlib_install) $(brotli_install) $(quictls_install) $(nghttp2_install) $(ngtcp2_install) $(nghttp3_install) $(curl_build)
	set -e
	cd "$(curl_build)"
	# TODO This is a hack. Find a way to nuke this.
	# 	   This forces curl to link against the .a files instead of the .dll.a files. Thus they are actually static linked in.
	#      However, ngtcp2 requires the .dll.a files to locate and configure itself. So, we delete them here.
	rm "$(quictls_install)"/lib*/*.dll.a || true
	rm "$(quictls_install)"/lib*/*.dylib || true
	export CC="$(C_COMPILER)"
	export LDFLAGS="$(LDFLAGS)"
	export OpenSSL_ROOT=$(quictls_install)
	export OPENSSL_ROOT_DIR=$(quictls_install)
	export ZLIB_ROOT=$(zlib_install)
	export Brotli_ROOT=$(brotli_install)
	export NGHTTP2_ROOT=$(nghttp2_install)
	export NGTCP2_ROOT=$(ngtcp2_install)
	export NGHTTP3_ROOT=$(nghttp3_install)
	export HAVE_SSL_CTX_SET_QUIC_METHOD=yes
	cmake \
		-DCMAKE_C_FLAGS="-DNGHTTP2_STATICLIB -DNGTCP2_STATICLIB -DNGHTTP3_STATICLIB" \
		-DCMAKE_BUILD_TYPE=Release \
		-DCMAKE_INSTALL_PREFIX="$(curl_install)" \
		-DBUILD_CURL_EXE=OFF \
		-DBUILD_TESTING=OFF \
		-DCURL_BROTLI=ON \
		-DCURL_USE_OPENSSL=ON \
		-DHAVE_SSL_CTX_SET_QUIC_METHOD=ON \
		-DUSE_NGHTTP2=ON \
		-DUSE_NGTCP2=ON \
		-DCURL_USE_LIBSSH2=OFF \
		$(CMAKE_ARGS) \
		-G "Unix Makefiles"
	$(MAKE) -j $(nproc)
	$(MAKE) install
endif

$(libcurl_install): $(curl_install)
	set -e
	mkdir -p $(libcurl_install)
	# Copy licenses
	mkdir -p "$(libcurl_install)/$(flavour)/$(arch)/licenses/zlib" && cp "$(zlib_build)/LICENSE" "$(libcurl_install)/$(flavour)/$(arch)/licenses/zlib/LICENSE"
	mkdir -p "$(libcurl_install)/$(flavour)/$(arch)/licenses/brotli" && cp "$(brotli_build)/LICENSE" "$(libcurl_install)/$(flavour)/$(arch)/licenses/brotli/LICENSE"
	mkdir -p "$(libcurl_install)/$(flavour)/$(arch)/licenses/nghttp2" && cp "$(nghttp2_build)/COPYING" "$(libcurl_install)/$(flavour)/$(arch)/licenses/nghttp2/COPYING"
	mkdir -p "$(libcurl_install)/$(flavour)/$(arch)/licenses/ngtcp2" && cp "$(ngtcp2_build)/COPYING" "$(libcurl_install)/$(flavour)/$(arch)/licenses/ngtcp2/COPYING"
	mkdir -p "$(libcurl_install)/$(flavour)/$(arch)/licenses/quictls" && cp "$(quictls_build)/LICENSE.txt" "$(libcurl_install)/$(flavour)/$(arch)/licenses/quictls/LICENSE.txt"
	mkdir -p "$(libcurl_install)/$(flavour)/$(arch)/licenses/curl" && cp "$(curl_build)/COPYING" "$(libcurl_install)/$(flavour)/$(arch)/licenses/curl/COPYING"
ifeq (linux, $(flavour))
	mkdir -p "$(libcurl_install)/$(flavour)/$(arch)/licenses/musl" && cp "$(musl_build)/COPYRIGHT" "$(libcurl_install)/$(flavour)/$(arch)/licenses/musl/COPYRIGHT"
	cp "$(curl_install)/lib/libcurl.so.4.8.0" "$(libcurl_install)/$(flavour)/$(arch)/libcurl.so"
else ifeq (windows, $(flavour))
	cp "$(curl_install)/bin/libcurl.dll" "$(libcurl_install)/$(flavour)/$(arch)/curl.dll"
else ifeq (macos, $(flavour))
	cp "$(curl_install)/lib/libcurl.4.dylib" "$(libcurl_install)/$(flavour)/$(arch)/libcurl.dylib"
else
$(error "I don't know where libcurl was installed to on $(flavour)")
endif

$(libcurl_zip): $(libcurl_install)
	set -e
	cd "$(libcurl_install)"
	zip -r "$(libcurl_zip)" ./*
	sha256sum $(libcurl_zip)

clean-curl:
	rm -rf $(curl_build)
	rm -rf $(curl_install)

# === Misc ===
$(linux_headers):
	mkdir -p "$(linux_headers)"
	ln -s /usr/include/linux "$(linux_headers)"
	ln -s /usr/include/asm-generic "$(linux_headers)"
	ln -s /usr/include/$(shell uname -m)-linux-gnu/asm "$(linux_headers)"

clean-linux:
	rm -rf "$(linux_headers)"

clean: clean-linux clean-musl clean-zlib clean-brotli clean-nghttp2 clean-ngtcp2 clean-nghttp3
	rm -rf "$(build_dir)"
	rm -rf "$(install_dir)"

clean-cache:
	rm -rf "$(cache_dir)"