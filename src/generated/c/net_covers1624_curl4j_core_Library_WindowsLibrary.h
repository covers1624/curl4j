/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class net_covers1624_curl4j_core_Library_WindowsLibrary */

#ifndef _Included_net_covers1624_curl4j_core_Library_WindowsLibrary
#define _Included_net_covers1624_curl4j_core_Library_WindowsLibrary
#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     net_covers1624_curl4j_core_Library_WindowsLibrary
 * Method:    GetModuleHandle
 * Signature: (Ljava/lang/String;)J
 */
JNIEXPORT jlong JNICALL Java_net_covers1624_curl4j_core_Library_00024WindowsLibrary_GetModuleHandle
  (JNIEnv *, jclass, jstring);

/*
 * Class:     net_covers1624_curl4j_core_Library_WindowsLibrary
 * Method:    GetLastError
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_net_covers1624_curl4j_core_Library_00024WindowsLibrary_GetLastError
  (JNIEnv *, jclass);

/*
 * Class:     net_covers1624_curl4j_core_Library_WindowsLibrary
 * Method:    GetProcAddress
 * Signature: (JLjava/lang/String;)J
 */
JNIEXPORT jlong JNICALL Java_net_covers1624_curl4j_core_Library_00024WindowsLibrary_GetProcAddress
  (JNIEnv *, jclass, jlong, jstring);

#ifdef __cplusplus
}
#endif
#endif
