.PHONY: build
build:
	gradle build

install:
	adb install -r app/build/outputs/apk/debug/app-debug.apk

clean:
	gradle clean
	/bin/rm -r -f .idea
	/bin/rm -r -f .gradle

log:
	adb logcat | grep Slime

