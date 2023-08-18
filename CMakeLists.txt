cmake_minimum_required(VERSION 3.11)
project(curl4j C)
set(CMAKE_C_STANDARD 23)

add_library(curl4j SHARED
        src/main/c/net_covers1624_curl4j_core_Callback.c
        src/main/c/net_covers1624_curl4j_core_Library_UnixLibrary.c
        src/main/c/net_covers1624_curl4j_core_Memory.c
        src/main/c/net_covers1624_curl4j_core_NativeTypes.c
        src/main/c/net_covers1624_curl4j_CURL_Functions.c
        src/main/c/net_covers1624_curl4j_CurlHeaderCallback.c
        src/main/c/net_covers1624_curl4j_CurlReadCallback.c
        src/main/c/net_covers1624_curl4j_CurlWriteCallback.c
        src/main/c/net_covers1624_curl4j_CurlXferInfoCallback.c
        src/main/c/utils.c
)

find_package(JNI REQUIRED)
include_directories(src/generated/c ${JNI_INCLUDE_DIRS} thirdparty/libffi/build_/include/)

target_link_libraries(curl4j ${PROJECT_SOURCE_DIR}/thirdparty/libffi/build_/.libs/libffi.a)
