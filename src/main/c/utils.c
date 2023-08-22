//
// Created by covers1624 on 16/08/23.
//

#include "utils.h"
#include <stdlib.h>

static JavaVM *vm;

JNIEnv* getEnv() {
    JNIEnv *env;
    int ret = (*vm)->GetEnv(vm, (void **) &env, JNI_VERSION_1_8);
    if (ret == JNI_EDETACHED) {
        fprintf(stderr, "[libcurl4j] GetEnv called from non-native thread. Currently not supported!.\n");
        fflush(stderr);
        exit(1);
    } else if (!env) {
        fprintf(stderr, "[libcurl4j] GetEnv failed. Code: %d\n", ret);
        fflush(stderr);
        exit(1);
    }
    return env;
}

JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM *jvm, void *reserved) {
    vm = jvm;
    return JNI_VERSION_1_8;
}

JNIEXPORT void JNICALL JNI_OnUnload(JavaVM *, void *reserved) {
}