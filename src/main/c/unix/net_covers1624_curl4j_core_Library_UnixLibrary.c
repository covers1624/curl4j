//
// Created by covers1624 on 14/08/23.
//
#include "net_covers1624_curl4j_core_Library_UnixLibrary.h"
#include "stdint.h"
#include "dlfcn.h"

JNIEXPORT jlong JNICALL Java_net_covers1624_curl4j_core_Library_00024UnixLibrary_dlopen(JNIEnv *env, jclass, jstring name, jint mode) {
    const char *str = (*env)->GetStringUTFChars(env, name, NULL);
    jlong ret = (jlong) (uintptr_t) dlopen(str, mode);
    (*env)->ReleaseStringUTFChars(env, name, str);
    return ret;
}

JNIEXPORT jlong JNICALL Java_net_covers1624_curl4j_core_Library_00024UnixLibrary_dlsym(JNIEnv *env, jclass, jlong handle, jstring name) {
    const char *str = (*env)->GetStringUTFChars(env, name, NULL);
    long ret = (jlong) (uintptr_t) dlsym((void *) (uintptr_t) handle, str);
    (*env)->ReleaseStringUTFChars(env, name, str);
    return ret;
}

JNIEXPORT jint JNICALL Java_net_covers1624_curl4j_core_Library_00024UnixLibrary_dlclose(JNIEnv *, jclass, jlong handle) {
    return dlclose((void *) (uintptr_t) handle);
}

JNIEXPORT jstring JNICALL Java_net_covers1624_curl4j_core_Library_00024UnixLibrary_dlerror(JNIEnv *env, jclass) {
    const char *err = dlerror();
    if (err == NULL) return NULL;

    return (*env)->NewStringUTF(env, err);
}
