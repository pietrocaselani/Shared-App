cmake_minimum_required(VERSION 3.6)
project(Duktape_Example)

set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=c++11")

set(SOURCE_FILES
        main.cpp
        duk_config.h
        duktape.h
        duktape.c)

add_executable(Duktape_Example ${SOURCE_FILES})

add_custom_target(
        MyDuktapeTarget
        COMMAND cp -rf ${CMAKE_SOURCE_DIR}/js ${CMAKE_BINARY_DIR})

add_dependencies(Duktape_Example MyDuktapeTarget)