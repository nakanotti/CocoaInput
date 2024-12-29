CC	= gcc
CFLAGS	= -Wall -fPIC
OBJS	= libwaylandcocoainput.o logger.o wayland-text-input-unstable-v3.o
LIBS	= -lwayland-client
TARGET	= libwaylandcocoainput.so
DISTDIR	= ../../src/main/resources/wayland/
RM	= rm
CP	= cp
PKG_CONFIG ?= pkg-config

WAYLAND_FLAGS = $(shell $(PKG_CONFIG) wayland-client --cflags --libs)
WAYLAND_PROTOCOLS_DIR = $(shell $(PKG_CONFIG) wayland-protocols --variable=pkgdatadir)

WAYLAND_SCANNER = $(shell pkg-config --variable=wayland_scanner wayland-scanner)

XDG_INPUT_PROTOCOL = $(WAYLAND_PROTOCOLS_DIR)/unstable/text-input/text-input-unstable-v3.xml

GENERATEDS=wayland-text-input-unstable-v3-client-protocol.h wayland-text-input-unstable-v3.c

all: $(TARGET)

install: $(TARGET)
	$(CP) -f $(TARGET) $(DISTDIR)

$(TARGET): $(OBJS)
	$(CC) $(OBJS) $(CFLAGS) $(FFLAGS) $(LIBS) -shared -o $@

libwaylandcocoainput.o: wayland-text-input-unstable-v3-client-protocol.h

wayland-text-input-unstable-v3-client-protocol.h:
	$(WAYLAND_SCANNER) client-header $(XDG_INPUT_PROTOCOL) wayland-text-input-unstable-v3-client-protocol.h

wayland-text-input-unstable-v3.c:
	$(WAYLAND_SCANNER) private-code $(XDG_INPUT_PROTOCOL) wayland-text-input-unstable-v3.c

.PHONY: clean
clean:
	$(RM) -f $(TARGET) $(OBJS) $(GENERATEDS)
