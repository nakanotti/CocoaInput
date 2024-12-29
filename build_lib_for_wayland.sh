#!/bin/bash
echo "Build libcocoainput for Wayland"
mkdir -p src/main/resources/wayland
cd libcocoainput/wayland
make && make install
