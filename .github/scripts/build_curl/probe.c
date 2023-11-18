// Super terrible program to probe a libcurl binary.
// gcc -ldl -o probe probe.c && ./probe ./install/curl/lib/libcurl.so

#include <sys/stat.h>
#include <stdio.h>
#ifdef WIN32
#include <libloaderapi.h>
#include <errhandlingapi.h>
#else
#include <dlfcn.h>
#endif

#ifdef WIN32
HMODULE libcurl = NULL;
#else
void *libcurl = NULL;
#endif

void *curlSym(const char *sym) {
	printf(" Loading symbol: %s\n", sym);
#ifdef WIN32
	void *func = (void *)GetProcAddress(libcurl, sym);
#else
	void *func = dlsym(libcurl, sym);
#endif
	if (!func) {
		printf("Could not find symbol '%s'\n", sym);
		return NULL;
	}
	return func;
}

void print_string_list(char **);

int main(int argc, char *argv[]) {
	if (argc != 2) {
		printf("Usage: %s <libcurl.so>\n", argv[0]);
		return 1;
	}

	char *libcurlPath = argv[1];

	struct stat st;
	if (stat(libcurlPath, &st) != 0) {
		printf("Provided file does not exist or is not accessible.\n");
		return 1;
	}
	printf("Testing libcurl at: %s\n", argv[1]);

#ifdef WIN32
	libcurl = LoadLibrary(libcurlPath);
	if (!libcurl) {
		printf("Failed to open library: %d\n", GetLastError());
		return 1;
	}
#else
	libcurl = dlopen(libcurlPath, RTLD_LAZY);
	if (!libcurl) {
		printf("Failed to open library: %s\n", dlerror());
		return 1;
	}
#endif

	printf("Loading symbols:\n");
	void *f_curl_version = curlSym("curl_version");
	if (!f_curl_version) return 1;

	void *f_curl_version_info = curlSym("curl_version_info");
	if (f_curl_version_info == NULL) return 1;

	const char *feature_versions = ((const char *(*)(void)) f_curl_version)();
	void **curl_version_info = ((void **(*)(int)) f_curl_version_info)(10);
	char *version = curl_version_info[1];
	char *host = curl_version_info[3];

	printf("libcurl %s (%s) %s\n", version, host, feature_versions);
	printf("Protocols: ");
	print_string_list(curl_version_info[8]);
	printf("\n");
	printf("Features: ");
	print_string_list(curl_version_info[25]);
	printf("\n");

#ifdef WIN32
	FreeLibrary(libcurl);
#else
	dlclose(libcurl);
#endif
}

void print_string_list(char **list) {
	char **ptr = list;
	while (*ptr != NULL) {
		if (ptr != list) {
			printf(" ");
		}
		printf("%s", *ptr++);
	}
}
