#include "net_covers1624_curl4j_core_Memory.h"
#include "string.h"

JNIEXPORT jstring JNICALL Java_net_covers1624_curl4j_core_Memory_readUtf8(JNIEnv *env, jclass clazz, jlong str) {
    return (*env)->NewStringUTF(env, (const char *) str);
}

JNIEXPORT jobject JNICALL Java_net_covers1624_curl4j_core_Memory_newDirectByteBuffer(JNIEnv *env, jclass clazz, jlong address, jint capacity) {
    return (*env)->NewDirectByteBuffer(env, (void *) address, capacity);
}

JNIEXPORT jlong JNICALL Java_net_covers1624_curl4j_core_Memory_getDirectByteBufferAddress(JNIEnv *env, jclass clazz, jobject buffer) {
    return (jlong) (*env)->GetDirectBufferAddress(env, buffer);
}

JNIEXPORT jlong JNICALL Java_net_covers1624_curl4j_core_Memory_newGlobalRef(JNIEnv *env, jclass clazz, jobject obj) {
    return (jlong) (*env)->NewGlobalRef(env, obj);
}

JNIEXPORT void JNICALL Java_net_covers1624_curl4j_core_Memory_deleteGlobalRef(JNIEnv *env, jclass clazz, jlong ref) {
    (*env)->DeleteGlobalRef(env, (jobject) ref);
}

JNIEXPORT jobject JNICALL Java_net_covers1624_curl4j_core_Memory_getGlobalRefValue(JNIEnv *env, jclass clazz, jlong obj) {
    return (jobject) obj;
}
