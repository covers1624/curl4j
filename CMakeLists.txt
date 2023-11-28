cmake_minimum_required(VERSION 3.11)
project(curl4j C)
set(CMAKE_C_STANDARD 23)

if (NOT EXISTS ${CMAKE_SOURCE_DIR}/native_build/install/libffi)
    if (NOT DISABLE_LIBFFI_CHECK)
        message(FATAL_ERROR "Missing LibFFI Please run 'setup-dev' make target of 'native_build'")
    endif ()
else ()
    set(LIBFFI_DIR ${CMAKE_SOURCE_DIR}/native_build/install/libffi)
endif ()

if (NOT EXISTS ${CMAKE_SOURCE_DIR}/native_build/install/jdk)
    if (NOT DISABLE_JNI_CHECK)
        message(FATAL_ERROR "Missing JDK Please run 'setup-dev' make target of 'native_build'")
    endif ()
else ()
    set(JDK_DIR ${CMAKE_SOURCE_DIR}/native_build/install/jdk)
endif ()

include(FindPkgConfig)
find_package(PkgConfig REQUIRED)
set(ENV{PKG_CONFIG_PATH} "$ENV{PKG_CONFIG_PATH}:${LIBFFI_DIR}/lib/pkgconfig")
pkg_check_modules(LIBFFI REQUIRED IMPORTED_TARGET libffi)
message("Found LibFFI at ${LIBFFI_LIBRARY_DIRS}")

add_library(curl4j SHARED
        src/main/c/unix/net_covers1624_curl4j_core_Library_UnixLibrary.c
        src/main/c/windows/net_covers1624_curl4j_core_Library_WindowsLibrary.c
        src/main/c/net_covers1624_curl4j_core_Callback.c
        src/main/c/net_covers1624_curl4j_core_Memory.c
        src/main/c/net_covers1624_curl4j_core_NativeTypes.c
        src/main/c/net_covers1624_curl4j_CURL_Functions.c
        src/main/c/utils.c
)

include_directories(src/generated/c ${JDK_DIR}/include)

target_link_libraries(curl4j PkgConfig::LIBFFI)
