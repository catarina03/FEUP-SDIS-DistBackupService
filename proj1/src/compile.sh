#! /usr/bin/bash

# Compilation script
# To be executed in the root of the package (source code) hierarchy
# Compiled code is placed under ./classDir/

rm -rf classDir
mkdir -p classDir

javac $(find . | grep .java) -d classDir &
# javac $(find . | grep .java) -d build 2> /dev/null &

mkdir -p classDir/peer/chunks
mkdir -p classDir/peer/files