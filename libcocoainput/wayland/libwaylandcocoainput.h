#ifndef _LIBWAYLANDCOCOAINPUT_H
#define _LIBWAYLANDCOCOAINPUT_H
#include <stdint.h>
#include "logger.h"

void focus();

void unfocus();

void initialize(
    void (*done)(),
    int* (*preedit)(const char*, int32_t, int32_t),
    int* (*commit)(const char*),
	LogFunction log,
	LogFunction error,
	LogFunction debug
);

#endif
