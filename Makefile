VERSIONCODE=32
VERSION=1.8.2

# 1.8.2: Playストアで公開成功 Slime2という名前になってる
# 1.8.1: HUAWEIで動くように久しぶりに直したもの - 動かない
# 1.7.3: 新MBAでビルドしたら何故か署名に失敗してたので旧MBAでビルド
# 1.7.2: new Nexus7で動かない問題をとりあえず修正
# 1.7.1: ネット接続があるときは常にGoogleIME検索することに
# 1.6.1: 高速タップがブレたとき子音入力モードでなくなってしまう問題を解決
# 1.5.1: Google日本語入力を利用
# 1.5.2: ブラウザURLが消えるのを修正
#        候補がないときひらがな/カタカナを表示
#        キー入力で検索キャンセルを徹底

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

# 署名してアップロード
publish: clean
	sed -e "s/VERSIONCODE/${VERSIONCODE}/" app/build.gradle.template | sed -e "s/VERSION/${VERSION}/" > app/build.gradle
	sed -e "s/VERSIONCODE/${VERSIONCODE}/" app/src/main/AndroidManifest.template | sed -e "s/VERSION/${VERSION}/" > app/src/main/AndroidManifest.xml
	gradle build
	-mkdir bin
	/bin/cp app/build/outputs/apk/release/app-release-unsigned.apk bin/Slime.apk
	jarsigner -J-Dfile.encoding=UTF8 -keystore slime2.keystore -verbose bin/Slime.apk pitecan
	-/bin/rm bin/Slime-aligned.apk
	/usr/local/android-sdk//build-tools/26.0.1/zipalign -v 4 bin/Slime.apk bin/Slime-aligned.apk
	scp bin/Slime.apk pitecan.com:/www/www.pitecan.com/Slime
	scp bin/Slime.apk pitecan.com:/www/www.pitecan.com/Slime/Slime-${VERSION}.apk

