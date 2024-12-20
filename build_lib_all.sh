#!/bin/bash
./build_lib_for_win.sh
./build_lib_for_x11.sh
./build_lib_for_wayland.sh
remote_build.sh
