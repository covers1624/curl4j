cmake_minimum_required(VERSION 3.11)
project(curl4j C)
set(CMAKE_C_STANDARD 23)

set(curl4j_conditional_sources)
if (UNIX)
    list(APPEND curl4j_conditional_sources src/main/c/unix/net_covers1624_curl4j_core_Library_UnixLibrary.c)
elseif (WIN32)
    list(APPEND curl4j_conditional_sources src/main/c/windows/net_covers1624_curl4j_core_Library_WindowsLibrary.c)
endif ()

add_library(curl4j SHARED
        src/main/c/net_covers1624_curl4j_core_Callback.c
        src/main/c/net_covers1624_curl4j_core_Memory.c
        src/main/c/net_covers1624_curl4j_core_NativeTypes.c
        src/main/c/net_covers1624_curl4j_CURL_Functions.c
        src/main/c/utils.c
        ${curl4j_conditional_sources}
)

find_package(JNI REQUIRED)
include_directories(src/generated/c ${JNI_INCLUDE_DIRS} thirdparty/libffi/build_/include/)

target_link_libraries(curl4j ${PROJECT_SOURCE_DIR}/thirdparty/libffi/build_/.libs/libffi.a)
