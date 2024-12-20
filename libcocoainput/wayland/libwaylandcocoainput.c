#define GLFW_EXPOSE_NATIVE_WAYLAND

#include <GLFW/glfw3.h>
#include <GLFW/glfw3native.h>
#include <stdlib.h>
#include <string.h>
#include <time.h>
#include <wayland-client.h>
#include <wayland-client-protocol.h>
#include "wayland-text-input-unstable-v3-client-protocol.h"
#include "logger.h"
#include "libwaylandcocoainput.h"

struct wl_registry *registry = NULL;
struct zwp_text_input_manager_v3 *text_input_manager = NULL;
struct zwp_text_input_v3 *text_input = NULL;
struct wl_seat *seat = NULL;

void (*doneCallback)();
int* (*preeditCallback)(const char*, int32_t, int32_t);
int* (*commitCallback)(const char*);

static void global_listener(void *data, struct wl_registry *registry,
                            uint32_t name, const char *interface, uint32_t version) {
    if (strcmp(interface, zwp_text_input_manager_v3_interface.name) == 0) {
        text_input_manager = wl_registry_bind(
            registry, name, &zwp_text_input_manager_v3_interface, 1);
    } else if (strcmp(interface, wl_seat_interface.name) == 0) {
        seat = wl_registry_bind(registry, name, &wl_seat_interface, 1);
    }
}

static void global_remove_listener(void *data, struct wl_registry *registry, uint32_t name) {}

static const struct wl_registry_listener registry_listener = {
    global_listener,
    global_remove_listener
};

void text_enter(void *data,
		        struct zwp_text_input_v3 *zwp_text_input_v3,
		        struct wl_surface *surface) {}

void text_leave(void *data,
		        struct zwp_text_input_v3 *zwp_text_input_v3,
		        struct wl_surface *surface) {}

void preedit_string(void *data,
			        struct zwp_text_input_v3 *zwp_text_input_v3,
			        const char *text,
			        int32_t cursor_begin,
			        int32_t cursor_end) {
    CIDebug("preedit: %s,%d,%d\n", text, cursor_begin, cursor_end);
    preeditCallback(text, cursor_begin, cursor_end);
}

void commit_string(void *data,
			       struct zwp_text_input_v3 *zwp_text_input_v3,
			       const char *text) {
    CIDebug("commit: %s\n", text);
    commitCallback(text);
}

void delete_surrounding_text(void *data,
                             struct zwp_text_input_v3 *zwp_text_input_v3,
                             uint32_t before_length,
                             uint32_t after_length) {}

void text_done(void *data,
               struct zwp_text_input_v3 *zwp_text_input_v3,
               uint32_t serial) {
    doneCallback();
}

static const struct zwp_text_input_v3_listener text_input_listener = {
    text_enter,
    text_leave,
    preedit_string,
    commit_string,
    delete_surrounding_text,
    text_done
};

void error_callback(int error, const char* description) {
    // fprintf(stderr, "Error: %s\n", description);
}

void focus() {
    zwp_text_input_v3_enable(text_input);
    zwp_text_input_v3_commit(text_input);
}

void unfocus() {
    zwp_text_input_v3_disable(text_input);
    zwp_text_input_v3_commit(text_input);
}

void initialize(
    void (*done)(),
    int* (*preedit)(const char*, int32_t, int32_t),
    int* (*commit)(const char*),
	LogFunction log,
	LogFunction error,
	LogFunction debug
) {
    initLogPointer(log,error,debug);
    CILog("CocoaInput Wayland Clang Initializer start. library compiled at  %s %s",__DATE__,__TIME__);
    doneCallback = done;
    preeditCallback = preedit;
    commitCallback = commit;
    struct wl_display *display = glfwGetWaylandDisplay();

    registry = wl_display_get_registry(display);
    wl_registry_add_listener(registry, &registry_listener, NULL);
    wl_display_dispatch(display);
    wl_display_roundtrip(display);

    if (text_input_manager == NULL) {
        CIError("Compositor does not support text input manager\n");
    }

    if (seat == NULL) {
        CIError("No seat found\n");
        return;
    }

    text_input = zwp_text_input_manager_v3_get_text_input(text_input_manager, seat);
    zwp_text_input_v3_add_listener(text_input, &text_input_listener, NULL);
    zwp_text_input_v3_commit(text_input);
}

void deinitialize() {
    zwp_text_input_v3_destroy(text_input);
    zwp_text_input_manager_v3_destroy(text_input_manager);
    wl_seat_destroy(seat);
    wl_registry_destroy(registry);
}
