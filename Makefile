.PHONY: build
build:
	gradle build

install:
	adb install -r app/build/outputs/apk/debug/app-debug.apk

log:
	adb logcat | grep Slime

