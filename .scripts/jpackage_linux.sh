#!/bin/bash
set -euo pipefail

# Maven
mvn clean package

# Artifact name and version from pom.xml
ARTIFACT=$(grep -m 1 '<artifactId>' ./pom.xml | sed 's/ *<artifactId>\(.*\)<\/artifactId> */\1/')
APP_VERSION=$(grep -m 1 '<version>' ./pom.xml | sed 's/ *<version>\(.*\)<\/version> */\1/')

echo "Program name  -> $ARTIFACT"
echo "Program version -> $APP_VERSION"

# Paths
TARGET_PATH=./target
DIST_PATH="$TARGET_PATH/dist/app"
PACKAGING_PATH=./.packaging/linux
INSTALLERS_PATH="$TARGET_PATH/installer"

# Clean and create directories
rm -rf "$DIST_PATH" "$INSTALLERS_PATH"
mkdir -p "$DIST_PATH" "$INSTALLERS_PATH"

JAR_NAME="$ARTIFACT.jar"

# Copy jar to distribution folder
cp "$TARGET_PATH/$JAR_NAME" "$DIST_PATH"

# Flags for jpackage
ICON_FLAG="--icon .packaging/icons/computer.png"

MAIN_FLAGS=(
  --name "$ARTIFACT"
  --input "$DIST_PATH"
  --main-jar "$JAR_NAME"
  --dest "$INSTALLERS_PATH/app"
  --app-version "$APP_VERSION"
  --copyright "Copyright © 2026 Enrique García (KGBis)"
  --vendor "KGBis"
)

# Generate Linux app-image
echo "Running jpackage app-image..."
jpackage --type app-image "${MAIN_FLAGS[@]}" "$ICON_FLAG"

# Copy scripts, .desktop files, etc.
echo "Copying additional packaging files..."
cp -r "$PACKAGING_PATH/"* "$INSTALLERS_PATH/"

# ensure installer/uninstaller scripts executable
chmod +x $INSTALLERS_PATH/install.sh
chmod +x $INSTALLERS_PATH/uninstall.sh

# Create version file
echo "$APP_VERSION" > "$INSTALLERS_PATH/VERSION"

# Generate self-extracting installer with makeself
echo "Creating self-extracting installer..."
makeself \
  --xz \
  --needroot \
  "$INSTALLERS_PATH" \
  remotecontrol-tray-linux.run \
  "Remote Control Tray installer" \
  ./install.sh

echo "Done! Installer generated: remotecontrol-tray-linux.run"