#!/bin/sh
set -e

APP_NAME=remotecontrol-tray
INSTALL_DIR=/opt/$APP_NAME
BIN_LINK=/usr/local/bin/$APP_NAME
DESKTOP_FILE=/usr/share/applications/$APP_NAME.desktop
ICON_FILE=/usr/share/icons/hicolor/256x256/apps/remotecontrol-tray.png

echo "Uninstalling $APP_NAME..."

# require root
if [ "$(id -u)" -ne 0 ]; then
  echo "Please run as root (sudo)"
  exit 1
fi

# remove symlink
if [ -L "$BIN_LINK" ]; then
  rm "$BIN_LINK"
  echo "✓ Removed $BIN_LINK"
fi

# remove desktop entry
if [ -f "$DESKTOP_FILE" ]; then
  rm "$DESKTOP_FILE"
  echo "✓ Removed desktop entry"
fi

# remove icon entry
if [ -f "$ICON_FILE" ]; then
  rm "$ICON_FILE"
  echo "✓ Removed icon entry"
fi

# remove autostart entries (best effort)
for home in /home/*; do
  AUTOSTART="$home/.config/autostart/$APP_NAME.desktop"
  if [ -f "$AUTOSTART" ]; then
    rm "$AUTOSTART"
    echo "✓ Removed autostart for user $(basename "$home")"
  fi
done

# remove app
if [ -d "$INSTALL_DIR" ]; then
  rm -rf "$INSTALL_DIR"
  echo "✓ Removed $INSTALL_DIR"
fi

update-desktop-database /usr/share/applications || true
gtk-update-icon-cache -q -t -f /usr/share/icons/hicolor || true

echo "✓ $APP_NAME uninstalled successfully"
