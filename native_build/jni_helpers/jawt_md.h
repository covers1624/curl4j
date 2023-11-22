//
// Simple wrapper around jawt_md.h to avoid needing platform specific
// logic in the build system. The compiler is more than capable of
// doing exactly this.
//
// Created by covers1624 on 21/11/23.
//
#ifdef WIN32
#include "win32/jawt_md.h"
#endif

#ifdef __APPLE__
#include "darwin/jawt_md.h"
#endif

#ifdef __linux__
#include "linux/jawt_md.h"
#endif
