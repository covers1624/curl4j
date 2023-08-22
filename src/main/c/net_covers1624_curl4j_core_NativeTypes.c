#include "net_covers1624_curl4j_core_NativeTypes.h"

JNIEXPORT jint JNICALL Java_net_covers1624_curl4j_core_NativeTypes_pointerSize(JNIEnv *, jclass) {
    return sizeof(void *);
}

JNIEXPORT jint JNICALL Java_net_covers1624_curl4j_core_NativeTypes_getIntSize(JNIEnv *, jclass) {
    return sizeof(int);
}

JNIEXPORT jint JNICALL Java_net_covers1624_curl4j_core_NativeTypes_getLongSize(JNIEnv *, jclass) {
    return sizeof(long);
}

JNIEXPORT jint JNICALL Java_net_covers1624_curl4j_core_NativeTypes_getSizeTSize(JNIEnv *, jclass) {
    return sizeof(size_t);
}
