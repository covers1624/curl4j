# curl4j

libcurl bindings for Java built on top of the LWJGL3 framework.

These bindings are very much still a work-in-progress. Contributions welcome!

## Features

- curl_easy (Mostly complete)
- curl_multi (No support. Planned)
- Optional curl-impersonate support.

## Usage

curl4j does not hold your hand, you are given raw access to curl as-is. There are
a few Java-specific method overloads for java types. There is no high-level wrapper
included.

Please refer to the official [libcurl](https://curl.se/libcurl/c/) documentation.


## Installation

Add the maven repository
```groovy
repositories {
    maven { url 'https://maven.covers1624.net/' }
}
```

Add the maven dependency. Replace `1.0-SNAPSHOT` with your desired version.
```groovy
dependencies {
    api 'net.covers1624:curl4j:1.0-SNAPSHOT'
}
```

## TODO list

In no particular order..

- Finish curl_easy and curl_multi bindings.
- Build curl/curl-impersonate for all platforms and provide additional maven dependencies

