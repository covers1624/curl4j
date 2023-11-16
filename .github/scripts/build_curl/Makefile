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

all: ${curl_install}

# === musl ===
ifneq (,$(findstring musl,$(HOST)))
compiler_deps=${musl_install}
compiler=${musl_install}/bin/musl-gcc

${musl_archive}:
	@"${scripts_dir}/download.sh" "https://musl.libc.org/releases/musl-${musl_version}.tar.gz" "${musl_archive}"

${musl_build}: ${musl_archive}
	mkdir -p "${build_dir}"
	tar -xvf "${musl_archive}" -C "${build_dir}"
	mv "${build_dir}/musl-${musl_version}/" "${musl_build}"
	patch -N -p1 -d "${musl_build}" -r /dev/null -i "${scripts_dir}/patches/musl/0000-getauxval-null-guard.patch"

${musl_install}: ${musl_build}
	cd "${musl_build}"
	CC="gcc" CFLAGS="-fPIC" ./configure --disable-shared --prefix="${musl_install}"
	make -j ${nproc}
	make install

clean-musl:
	rm -rf "${musl_build}"
	rm -rf "${musl_install}"
else
# TODO IJ is whining that this target doesnt exist, but not sure it actually matters.
${compiler_deps}:
clean-musl:
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
	CC="${compiler}" cmake -DCMAKE_BUILD_TYPE=Release -DCMAKE_INSTALL_PREFIX="${brotli_install}" -DBUILD_SHARED_LIBS=OFF
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
	CC="${compiler}" CFLAGS="-I${linux_headers}" ./Configure \
		no-shared \
		-fpic \
		--prefix=${quictls_install} \
		-DOPENSSL_DEV_NO_ATOMICS=true \
		-D__STDC_NO_ATOMICS__=true
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
	CFLAGS=--static CC="${compiler}" ./configure --with-pic --enable-lib-only --disable-shared --disable-python-bindings --prefix="${nghttp2_install}"
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
	CFLAGS=--static CC="${compiler}" ./configure \
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
	CFLAGS=--static CC="${compiler}" ./configure --with-pic --enable-lib-only --disable-shared --prefix="${nghttp3_install}"
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
	patch -N -p1 -r /dev/null -i "${scripts_dir}/patches/curl/patch-0000-brotli-1.1.0-static.patch"
	autoreconf -fi

${curl_install}: ${compiler_deps} ${zlib_install} ${brotli_install} ${quictls_install} ${nghttp2_install} ${ngtcp2_install} ${nghttp3_install} ${curl_build}
	cd "${curl_build}"
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
