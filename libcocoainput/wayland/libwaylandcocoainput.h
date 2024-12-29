#ifndef _LIBWAYLANDCOCOAINPUT_H
#define _LIBWAYLANDCOCOAINPUT_H
#include <stdint.h>
#include "logger.h"
#include <wayland-client.h>
#include <wayland-client-protocol.h>

void focus();

void unfocus();

void initialize(
    void (*done)(),
    void (*preedit)(const char*, int32_t, int32_t),
    void (*commit)(const char*),
    struct wl_display *display,
	LogFunction log,
	LogFunction error,
	LogFunction debug
);

#endif
