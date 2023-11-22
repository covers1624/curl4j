//
// Simple wrapper around jvm_md.h to avoid needing platform specific
// logic in the build system. The compiler is more than capable of
// doing exactly this.
//
// Created by covers1624 on 21/11/23.
//
#ifdef WIN32
#include "win32/jvm_md.h"
#endif

#ifdef __APPLE__
#include "darwin/jvm_md.h"
#endif

#ifdef __linux__
#include "linux/jvm_md.h"
#endif
