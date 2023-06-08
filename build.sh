#!/bin/bash

function Build()
{
    local type=$1
    local tag=$2
    local install_mods=$3

    # BUILD
    pushd ${type}
    local base_name=`cat gradle.properties | sed -E '/[^#]\W*archives_base_name\s*=/p;d' | sed -E 's/.+=\s*([-+.0-z]+)/\1/'`
    local mod_ver=`cat gradle.properties | sed -E '/[^#]\W*mod_version\s*=/p;d' | sed -E 's/.+=\s*([-+.0-z]+)/\1/'`
    local name=${base_name}-${mod_ver}
    local target=${name}${tag}.jar
    rm -f ../${target}
    ./gradlew --no-daemon build && cp -f ./build/libs/${name}.jar ../${target}
    popd

    # INSTALL for Debug
    if [ -f "${target}" -a -d "${install_mods}" ]; then
        echo "INSTALL ... ${install_mods}/${target}"
        rm -f ${install_mods}/CocoaInput*.jar
        cp -f ${target} ${install_mods}/
    fi
}

TAG=-PREVIEW
DEFAULT_FABRIC_MODS=${APPDATA:-~}/.minecraft_fabric_1.20/mods
DEFAULT_FORGE_MODS=${APPDATA:-~}/.minecraft_forge_1.20/mods

### Fabric
Build fabric ${TAG} ${MC_INSTALL_FABRIC_MODS-${DEFAULT_FABRIC_MODS}}

### Forge
Build forge ${TAG} ${MC_INSTALL_FORGE_MODS-${DEFAULT_FORGE_MODS}}
