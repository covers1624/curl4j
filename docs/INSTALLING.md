# Installation

To get started, you will need to add the following maven repository

Gradle `build.gradle`:

```groovy
repositories {
    maven { url 'https://maven.covers1624.net/' }
}
```

Maven `pom.xml`:

```xml

<repositories>
    <repository>
        <id>covers1624</id>
        <name>covers1624</name>
        <url>https://maven.covers1624.net/</url>
    </repository>
</repositories>
```

## Choosing your variant

curl4j provides 2 distributions one with and without libcurl pre-built and statically linked.
Choosing the correct variant will depend on your use case. Generally the bundled variant will
be fine for most applications, unless you have specific security concerns.

### Bundled (Recommended)

This variant of curl4j comes with statically linked libcurl.

Please note, whilst curl4j is MIT, libcurl and associated embedded dependencies are not.
A copy of the licenses is provided inside the artifact. Please see the [Licensing](#licensing) section for more info.

Gradle `build.gradle`:

```groovy
dependencies {
    api 'net.covers1624:curl4j:2.0-SNAPSHOT:libcurl'
}
```

Maven `pom.xml`:

```xml

<dependencies>
    <dependency>
        <groupId>net.covers1624</groupId>
        <artifactId>curl4j</artifactId>
        <version>2.0-SNAPSHOT</version>
        <classifier>libcurl</classifier>
    </dependency>
</dependencies>
```

### Bring your own libcurl

This variant of curl4j does **NOT** come with a copy of libcurl. You are required to either
use the system libcurl installation, or provide your own copy. See [Examples](EXAMPLES.md#native-libraries) for more info.

Gradle `build.gradle`:

```groovy
dependencies {
    api 'net.covers1624:curl4j:2.0-SNAPSHOT'
}
```

Maven `pom.xml`:

```xml

<dependencies>
    <dependency>
        <groupId>net.covers1624</groupId>
        <artifactId>curl4j</artifactId>
        <version>2.0-SNAPSHOT</version>
    </dependency>
</dependencies>
```

## Licensing

curl4j's embedded dependencies are distributed under the following licenses

| Library                                                | License                                                                                 | Artifact  |
|--------------------------------------------------------|-----------------------------------------------------------------------------------------|-----------|
| [libffi](https://github.com/libffi/libffi)             | [MIT](https://github.com/libffi/libffi/blob/master/LICENSE)                             | All       |
| [Mozilla cacert](https://curl.se/docs/caextract.html)  | [MPL 2.0](https://www.mozilla.org/en-US/MPL/2.0/)                                       | All       |
| [libcurl](https://github.com/curl/curl)                | [Curl License](https://curl.se/docs/copyright.html)                                     | `libcurl` |
| [openssl(quictls)](https://github.com/quictls/openssl) | [Apache 2.0](https://github.com/quictls/openssl/blob/openssl-3.0.10%2Bquic/LICENSE.txt) | `libcurl` |
| [nghttp2](https://github.com/nghttp2/nghttp2)          | [MIT](https://github.com/nghttp2/nghttp2/blob/master/COPYING)                           | `libcurl` |
| [ngtcp2](https://github.com/ngtcp2/ngtcp2)             | [MIT](https://github.com/ngtcp2/ngtcp2/blob/main/COPYING)                               | `libcurl` |
| [nghttp3](https://github.com/ngtcp2/nghttp3)           | [MIT](https://github.com/ngtcp2/nghttp3/blob/main/COPYING)                              | `libcurl` |
| [brotli](https://github.com/google/brotli)             | [MIT](https://github.com/google/brotli/blob/master/LICENSE)                             | `libcurl` |
| [zlib](https://github.com/madler/zlib)                 | [zlib License](https://github.com/madler/zlib/blob/develop/LICENSE)                     | `libcurl` |
| [zstd](https://github.com/facebook/zstd)               | [BSD or GPLv2](https://github.com/facebook/zstd#license)                                | `libcurl` |
