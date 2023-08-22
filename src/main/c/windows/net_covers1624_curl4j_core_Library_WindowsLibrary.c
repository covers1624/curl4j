#include "net_covers1624_curl4j_core_Library_WindowsLibrary.h"
#include "libloaderapi.h"
#include "errhandlingapi.h"

JNIEXPORT jlong JNICALL Java_net_covers1624_curl4j_core_Library_00024WindowsLibrary_GetModuleHandle(JNIEnv *env, jclass, jstring name) {
    const jchar *str = (*env)->GetStringChars(env, name, NULL);
    HMODULE handle = LoadLibraryW(str);
    (*env)->ReleaseStringChars(env, name, str);
    return (jlong) handle;
}

JNIEXPORT jint JNICALL Java_net_covers1624_curl4j_core_Library_00024WindowsLibrary_GetLastError(JNIEnv *, jclass) {
    return (jint) GetLastError();
}

JNIEXPORT jlong JNICALL Java_net_covers1624_curl4j_core_Library_00024WindowsLibrary_GetProcAddress(JNIEnv *env, jclass, jlong handle, jstring name) {
    const char *str = (*env)->GetStringUTFChars(env, name, NULL);
    FARPROC proc = GetProcAddress((HMODULE) handle, str);
    (*env)->ReleaseStringUTFChars(env, name, str);
    return (jlong) proc;
}