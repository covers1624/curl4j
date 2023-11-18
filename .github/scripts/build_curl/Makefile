.ONESHELL:

# === common directories ===
curr_dir=$(shell pwd)
scripts_dir=$(curr_dir)/..
build_dir=$(curr_dir)/build
install_dir=$(curr_dir)/install
cache_dir=$(curr_dir)/cache
linux_headers=${install_dir}/linux/include

# === VERSIONS ===
musl_version=1.2.4
musl_archive=${cache_dir}/musl-${musl_version}.tar.gz
musl_build=${build_dir}/musl
musl_install=${install_dir}/musl

zlib_version=1.3
zlib_archive=${cache_dir}/zlib-${zlib_version}.tar.gz
zlib_build=${build_dir}/zlib
zlib_install=${install_dir}/zlib

brotli_version=1.1.0
brotli_archive=${cache_dir}/brotli-${brotli_version}.tar.gz
brotli_build=${build_dir}/brotli
brotli_install=${install_dir}/brotli

quictls_version=3.1.2-quic1
quictls_archive=${cache_dir}/quictls-${quictls_version}.tar.gz
quictls_build=${build_dir}/quictls
quictls_install=${install_dir}/quictls

nghttp2_version=1.55.1
nghttp2_archive=${cache_dir}/nghttp2-${nghttp2_version}.tar.gz
nghttp2_build=${build_dir}/nghttp2
nghttp2_install=${install_dir}/nghttp2

ngtcp2_version=0.18.0
ngtcp2_archive=${cache_dir}/ngtcp2-${ngtcp2_version}.tar.gz
ngtcp2_build=${build_dir}/ngtcp2
ngtcp2_install=${install_dir}/ngtcp2

nghttp3_version=0.14.0
nghttp3_archive=${cache_dir}/nghttp3-${nghttp3_version}.tar.gz
nghttp3_build=${build_dir}/nghttp3
nghttp3_install=${install_dir}/nghttp3

curl_version=8.2.1
curl_archive=${cache_dir}/curl-${curl_version}.tar.gz
curl_build=${build_dir}/curl
curl_install=${install_dir}/curl

# === state ===
nproc=$(shell getconf _NPROCESSORS_ONLN)
compiler_deps=
compiler=gcc
windres=

cmake_extra=
quictls_extra=

all: ${curl_install}

# === musl ===
ifneq (,$(findstring musl,$(HOST)))
musl_compiler=gcc

ifneq (,$(findstring aarch64,$(HOST)))
musl_compiler=aarch64-linux-gnu-gcc-10
quictls_extra=linux-aarch64
endif

compiler_deps=${musl_install}
compiler=${musl_install}/bin/musl-gcc
cmake_extra=-DCMAKE_FIND_ROOT_PATH="${musl_install}"

${musl_archive}:
	@"${scripts_dir}/download.sh" "https://musl.libc.org/releases/musl-${musl_version}.tar.gz" "${musl_archive}"

${musl_build}: ${musl_archive}
	mkdir -p "${build_dir}"
	tar -xvf "${musl_archive}" -C "${build_dir}"
	mv "${build_dir}/musl-${musl_version}/" "${musl_build}"
	patch -N -p1 -d "${musl_build}" -r /dev/null -i "${scripts_dir}/patches/musl/0000-getauxval-null-guard.patch"

${musl_install}: ${musl_build}
	cd "${musl_build}"
	CC="${musl_compiler}" CFLAGS="-fPIC" ./configure --disable-shared --prefix="${musl_install}"
	make -j ${nproc}
	make install

clean-musl:
	rm -rf "${musl_build}"
	rm -rf "${musl_install}"
else
# TODO IJ is whining that this target doesnt exist, but not sure it actually matters.
${compiler_deps}:
clean-musl:
ifneq (,$(findstring w64, $(HOST)))
compiler=x86_64-w64-mingw32-gcc-posix
windres=x86_64-w64-mingw32-windres

cmake_extra=-DCMAKE_SYSTEM_NAME=Windows -DCMAKE_FIND_ROOT_PATH=/usr/x86_64-w64-mingw32
quictls_extra=mingw64
endif
endif

# === zlib ===
${zlib_archive}:
	@"${scripts_dir}/download.sh" "https://github.com/madler/zlib/releases/download/v${zlib_version}/zlib-${zlib_version}.tar.gz" "${zlib_archive}"

${zlib_build}: ${zlib_archive}
	mkdir -p "${build_dir}"
	tar -xvf "${zlib_archive}" -C "${build_dir}"
	mv "${build_dir}/zlib-${zlib_version}/" "${zlib_build}/"

${zlib_install}: ${compiler_deps} ${zlib_build}
	cd "${zlib_build}"
	CC="${compiler}" CFLAGS="-fPIC" ./configure --static --prefix="${zlib_install}"
	make -j ${nproc}
	make install

clean-zlib:
	rm -rf ${zlib_build}
	rm -rf ${zlib_install}

# === brotli ===
${brotli_archive}:
	@"${scripts_dir}/download.sh" "https://github.com/google/brotli/archive/refs/tags/v${brotli_version}.tar.gz" "${brotli_archive}"

${brotli_build}: ${brotli_archive}
	mkdir -p "${build_dir}"
	tar -xvf "${brotli_archive}" -C "${build_dir}"
	mv "${build_dir}/brotli-${brotli_version}/" "${brotli_build}/"

${brotli_install}: ${compiler_deps} ${brotli_build}
	cd "${brotli_build}"
	CC="${compiler}" cmake \
		-DCMAKE_BUILD_TYPE=Release \
		-DCMAKE_INSTALL_PREFIX="${brotli_install}" \
		-DBUILD_SHARED_LIBS=OFF \
		${cmake_extra}
	cmake --build . --config Release
	make -j ${nproc}
	make install

clean-brotli:
	rm -rf ${brotli_build}
	rm -rf ${brotli_install}

# === QuicTLS/OpenSSL ===
${quictls_archive}:
	@"${scripts_dir}/download.sh" "https://github.com/quictls/openssl/archive/refs/tags/openssl-${quictls_version}.tar.gz" "${quictls_archive}"

${quictls_build}: ${quictls_archive}
	mkdir -p "${build_dir}"
	tar -xvf "${quictls_archive}" -C "${build_dir}"
	mv "${build_dir}/openssl-openssl-${quictls_version}/" "${quictls_build}/"

${quictls_install}: ${compiler_deps} ${linux_headers} ${quictls_build}
	cd "${quictls_build}"
	CC="${compiler}" CFLAGS="-I${linux_headers}" WINDRES=${windres} ./Configure \
		no-tests \
		-fpic \
		--prefix=${quictls_install} \
		-DOPENSSL_DEV_NO_ATOMICS=true \
		-D__STDC_NO_ATOMICS__=true \
		${quictls_extra}
	make -j ${nproc}
	make install_sw

clean-quictls:
	rm -rf ${quictls_build}
	rm -rf ${quictls_install}

# === nghttp2 ===
${nghttp2_archive}:
	@"${scripts_dir}/download.sh" "https://github.com/nghttp2/nghttp2/releases/download/v${nghttp2_version}/nghttp2-${nghttp2_version}.tar.gz" "${nghttp2_archive}"

${nghttp2_build}: ${nghttp2_archive}
	mkdir -p "${build_dir}"
	tar -xvf "${nghttp2_archive}" -C "${build_dir}"
	mv "${build_dir}/nghttp2-${nghttp2_version}/" "${nghttp2_build}/"

${nghttp2_install}: ${compiler_deps} ${nghttp2_build}
	cd "${nghttp2_build}"
	CFLAGS=--static CC="${compiler}" ./configure \
		--host=${HOST} \
		--with-pic \
		--enable-lib-only \
		--disable-shared \
		--disable-python-bindings \
		--prefix="${nghttp2_install}"
	make -j ${nproc}
	make install

clean-nghttp2:
	rm -rf ${nghttp2_build}
	rm -rf ${nghttp2_install}

# === ngtcp2 ===
${ngtcp2_archive}:
	@"${scripts_dir}/download.sh" "https://github.com/ngtcp2/ngtcp2/releases/download/v${ngtcp2_version}/ngtcp2-${ngtcp2_version}.tar.gz" "${ngtcp2_archive}"

${ngtcp2_build}: ${ngtcp2_archive}
	mkdir -p "${build_dir}"
	tar -xvf "${ngtcp2_archive}" -C "${build_dir}"
	mv "${build_dir}/ngtcp2-${ngtcp2_version}/" "${ngtcp2_build}/"

${ngtcp2_install}: ${compiler_deps} ${ngtcp2_build} ${quictls_install}
	cd "${ngtcp2_build}"
	CC="${compiler}" ./configure \
		--host=${HOST} \
		--prefix="${ngtcp2_install}" \
		--with-pic \
		--enable-lib-only \
		--disable-shared \
		--with-jemalloc=no \
		--with-openssl \
		PKG_CONFIG_PATH="${quictls_install}/lib64/pkgconfig:${quictls_install}/lib/pkgconfig"
	make -j ${nproc}
	make install

clean-ngtcp2:
	rm -rf ${ngtcp2_build}
	rm -rf ${ngtcp2_install}

# === nghttp3 ===
${nghttp3_archive}:
	@"${scripts_dir}/download.sh" "https://github.com/ngtcp2/nghttp3/releases/download/v${nghttp3_version}/nghttp3-${nghttp3_version}.tar.gz" "${nghttp3_archive}"

${nghttp3_build}: ${nghttp3_archive}
	mkdir -p "${build_dir}"
	tar -xvf "${nghttp3_archive}" -C "${build_dir}"
	mv "${build_dir}/nghttp3-${nghttp3_version}/" "${nghttp3_build}/"

${nghttp3_install}: ${compiler_deps} ${nghttp3_build}
	cd "${nghttp3_build}"
	CC="${compiler}" ./configure \
		--host=${HOST} \
		--prefix="${nghttp3_install}" \
		--disable-shared \
		--with-pic \
		--enable-lib-only
	make -j ${nproc}
	make install

clean-nghttp3:
	rm -rf ${nghttp3_build}
	rm -rf ${nghttp3_install}

# === curl ===
${curl_archive}:
	@"${scripts_dir}/download.sh" "https://github.com/curl/curl/releases/download/curl-$(shell echo "${curl_version}" | tr '.' '_')/curl-${curl_version}.tar.gz" "${curl_archive}"

${curl_build}: ${curl_archive}
	mkdir -p "${build_dir}"
	tar -xvf "${curl_archive}" -C "${build_dir}"
	mv "${build_dir}/curl-${curl_version}/" "${curl_build}/"
	cd "${curl_build}"

# TODO On Linux/mac we use automake, on Windows/Mingw we use cmake.
#      As of writing this the following issues are present:
# 		- Windows/Mingw on automake does not emit a libcurl.dll, at all.
#		- Linux-x64 on cmake works just fine
#		- Linux-aarch64 on cmake does not link correctly and emits a broken assembly.
#		  - During OpenSSL static init, several calls to sigsetjmp are made, however, it incorrectly links setjmp.
#			Ultimately causing a Bus error as execution runs off into fucking narnia.
ifeq (,$(findstring w64, $(HOST)))
${curl_install}: ${compiler_deps} ${zlib_install} ${brotli_install} ${quictls_install} ${nghttp2_install} ${ngtcp2_install} ${nghttp3_install} ${curl_build}
	cd "${curl_build}"
	patch -N -p1 -r /dev/null -i "${scripts_dir}/patches/curl/patch-0000-brotli-1.1.0-static.patch"
	autoreconf -fi
	# TODO, Required due to the hack required for mingw bellow.
	rm "${quictls_install}"/lib*/*.so || true
	./configure \
		CC="${compiler}" \
		--host=${HOST} \
		--prefix="${curl_install}" \
		--with-zlib=${zlib_install} \
		--with-brotli=${brotli_install} \
		--with-openssl=${quictls_install} \
		--with-nghttp2=${nghttp2_install} \
		--with-ngtcp2=${ngtcp2_install} \
		--with-nghttp3=${nghttp3_install} \
		--enable-pthreads \
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
	make -j ${nproc}
	make install
else
${curl_install}: ${compiler_deps} ${zlib_install} ${brotli_install} ${quictls_install} ${nghttp2_install} ${ngtcp2_install} ${nghttp3_install} ${curl_build}
	set -e
	cd "${curl_build}"
	# TODO This is a hack. Find a way to nuke this.
	# 	   This forces curl to link against the .a files instead of the .dll.a files. Thus they are actually static linked in.
	#      However, ngtcp2 requires the .dll.a files to locate and configure itself. So, we delete them here.
	rm "${quictls_install}"/lib*/*.dll.a || true
	export CC="${compiler}"
	export CFLAGS="-gdwarf-4"
	export LDFLAGS="--static -static-libgcc"
	export OpenSSL_ROOT=${quictls_install}
	export OPENSSL_ROOT_DIR=${quictls_install}
	export ZLIB_ROOT=${zlib_install}
	export Brotli_ROOT=${brotli_install}
	export NGHTTP2_ROOT=${nghttp2_install}
	export NGTCP2_ROOT=${ngtcp2_install}
	export NGHTTP3_ROOT=${nghttp3_install}
	export HAVE_SSL_CTX_SET_QUIC_METHOD=yes
	cmake \
		-DCMAKE_C_FLAGS="-DNGHTTP2_STATICLIB -DNGTCP2_STATICLIB -DNGHTTP3_STATICLIB" \
		-DCMAKE_BUILD_TYPE=Release \
		-DCMAKE_INSTALL_PREFIX="${curl_install}" \
		-DBUILD_CURL_EXE=OFF \
		-DBUILD_TESTING=OFF \
		-DCURL_BROTLI=ON \
		-DCURL_USE_OPENSSL=ON \
		-DHAVE_SSL_CTX_SET_QUIC_METHOD=ON \
		-DUSE_NGHTTP2=ON \
		-DUSE_NGTCP2=ON \
		${cmake_extra} \
		-G "Unix Makefiles"
	make -j ${nproc}
	make install
endif

clean-curl:
	rm -rf ${curl_build}
	rm -rf ${curl_install}

# === Misc ===
${linux_headers}:
	mkdir -p "${linux_headers}"
	ln -s /usr/include/linux "${linux_headers}"
	ln -s /usr/include/asm-generic "${linux_headers}"
	ln -s /usr/include/$(shell uname -m)-linux-gnu/asm "${linux_headers}"

clean-linux:
	rm -rf "${linux_headers}"

clean: clean-linux clean-musl clean-zlib clean-brotli clean-nghttp2 clean-ngtcp2 clean-nghttp3
	rm -rf "${build_dir}"
	rm -rf "${install_dir}"

clean-cache:
	rm -rf "${cache_dir}"
