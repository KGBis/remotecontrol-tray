#!/bin/bash
set -e

# artifactId and version tags
ARTIFACT=$(grep -m 1 '<artifactId>' ./pom.xml | sed 's/ *<artifactId>\(.*\)<\/artifactId> */\1/')
APP_VERSION=$(grep -m 1 '<version>' ./pom.xml | sed 's/ *<version>\(.*\)<\/version> */\1/')

echo "Program name -> $ARTIFACT"
echo "Program version -> $APP_VERSION"

TARGET_PATH=./target
DIST_PATH=$TARGET_PATH/dist/app
PACKAGIN_PATH=./.packaging/linux/app-image
INSTALLER_PATH=$TARGET_PATH/installer

# create dist/install folders
rm -rf $TARGET_PATH/dist
mkdir -p $DIST_PATH

rm -rf $INSTALLER_PATH
mkdir -p $INSTALLER_PATH

JAR_NAME="$ARTIFACT.jar"

# copy jar
cp $TARGET_PATH/$JAR_NAME $DIST_PATH

# Flags for jPackage
ICON_FLAG="--icon .packaging/icons/computer.png"

MAIN_FLAGS=(
  --name $ARTIFACT
  --input $DIST_PATH
  --main-jar $JAR_NAME
  --dest $INSTALLER_PATH/app
  --app-version "$APP_VERSION"
  --copyright "Copyright © 2026 Enrique García (KGBis)"
  --vendor "KGBis"
)

# Generate Linux
echo "running -> jpackage app-image"
jpackage --type app-image "${MAIN_FLAGS[@]}" $ICON_FLAG

# create tar.gz for package
# echo "running -> tar -czvf $ARTIFACT-linux.tar.gz -C $INSTALLER_PATH $ARTIFACT"
# tar -czvf "$INSTALLER_PATH/$ARTIFACT-linux.tar.gz" -C "$INSTALLER_PATH" "$ARTIFACT"

# copy other files
echo "running -> cp $PACKAGIN_PATH/* $INSTALLER_PATH"
cp $PACKAGIN_PATH/* $INSTALLER_PATH

# create version file
echo "$APP_VERSION" > "$INSTALLER_PATH/VERSION"

# With makeself already installed...
echo "running -> makeself --xz --needroot --notemp $INSTALLER_PATH remotecontrol-tray-linux.run \"Remote Control Tray installer\" ./install.sh"
makeself \
  --xz \
  --needroot \
  $INSTALLER_PATH \
  remotecontrol-tray-linux.run \
  "Remote Control Tray installer" \
  ./install.sh