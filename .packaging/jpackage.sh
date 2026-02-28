#!/bin/bash
set -e

# artifactId and version tags
ARTIFACT=$(grep -m 1 '<artifactId>' ../pom.xml | sed 's/ *<artifactId>\(.*\)<\/artifactId> */\1/')
APP_VERSION=$(grep -m 1 '<version>' ../pom.xml | sed 's/ *<version>\(.*\)<\/version> */\1/')

BASE_PATH=../target
DIST_PATH=$BASE_PATH/dist/app
INSTALLER_PATH=$BASE_PATH/installer

# create dist/install folders
rm -rf $BASE_PATH/dist
mkdir -p $DIST_PATH

rm -rf $INSTALLER_PATH
mkdir -p $INSTALLER_PATH

JAR_NAME="$ARTIFACT.jar"

# copy jar
cp $BASE_PATH/$JAR_NAME $DIST_PATH

# Flags for jPackage
ICON_FLAG="--icon ./icons/computer.png"
DEBIAN_FLAGS="--resource-dir ./linux/deb"


MAIN_FLAGS=(
  --name $ARTIFACT
  --input $DIST_PATH
  --main-jar $JAR_NAME
  --dest ../target/installer
  --app-version "$APP_VERSION"
  --about-url "https://github.com/KGBis/remotecontrol-tray"
  --copyright "Copyright © 2026 Enrique García (KGBis)"
  --vendor "KGBis"
  --license-file ../LICENSE
  --linux-shortcut
)

# shellcheck disable=SC2086
jpackage --type deb "${MAIN_FLAGS[@]}" $ICON_FLAG $DEBIAN_FLAGS

MAIN_FLAGS=(
  --name $ARTIFACT
  --input $DIST_PATH
  --main-jar $JAR_NAME
  --dest ../target/installer
  --app-version "$APP_VERSION"
  --copyright "Copyright © 2026 Enrique García (KGBis)"
  --vendor "KGBis"
)

jpackage --type app-image "${MAIN_FLAGS[@]}" $ICON_FLAG $DEBIAN_FLAGS