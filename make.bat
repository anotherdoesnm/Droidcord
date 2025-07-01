@echo off
if "%1"=="d" goto d
if "%1"=="r" goto r
if "%1"=="run" goto run
if "%1"=="install" goto install
if "%1"=="logcat" goto logcat

@..\ant\bin\ant -q compile debug
goto end

:d
@..\ant\bin\ant compile debug
goto end

:r
@..\ant\bin\ant compile release
goto end

:run
start "" ..\tools\emulator @Android
goto end

:install
..\tools\adb install bin\HelloWorld-debug.apk
goto end

:logcat
..\tools\adb logcat

:end