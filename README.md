<h1 align="center">curl4j</h1>
<p align="center">
libcurl bindings for Java 8+
</p>
<p align="center">
    <a href="#features">Features</a> &nbsp;&bull;&nbsp;
    <a href="docs/INSTALLING.md">Installation</a> &nbsp;&bull;&nbsp;
    <a href="docs/EXAMPLES.md">Examples</a>
</p>

## Features

- Low-level bindings to libcurl.
    - Bundled libcurl for convenience.
    - Bundled CA info for convenience.
    - Bring your own libcurl binary. Use the system libcurl, or specify your own path.
    - Support for curl_easy and curl_multi.
- High performance, extremely low overhead
- Provides native memory management primitives for..
    - Wrapping/reading/writing native memory.
    - Java safe, native callback function pointers.
- Support for Linux (x64/arm64)(gnu/musl), macOS (x64/arm64), and Windows (x64)
- Supports [curl-impersonate](https://github.com/lwthiker/curl-impersonate). _*requires BYO libcurl_ See [docs](docs/EXAMPLES.md#curl-impersonate).

## Usage

Please refer to the official [libcurl](https://curl.se/libcurl/c/) documentation for specifics
on how libcurl functions work.

Please see our [Examples](docs/EXAMPLES.md) for usage.

## TODO list (unsorted)

- Musl arm64 linux.
- Feature parity between Windows and macOS/Linux libcurl.
- Maven central?
- Finish curl_easy and curl_multi bindings.
- Windows arm64
- 32 bit support
- At some point we need to leave J8 behind, experiment with Java FFM API.

