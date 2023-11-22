//
// Simple wrapper around jni_md.h to avoid needing platform specific
// logic in the build system. The compiler is more than capable of
// doing exactly this.
//
// Created by covers1624 on 21/11/23.
//
#ifdef WIN32
#include "win32/jni_md.h"
#endif

#ifdef __APPLE__
#include "darwin/jni_md.h"
#endif

#ifdef __linux__
#include "linux/jni_md.h"
#endif
