#!/bin/sh
set -e

APP_NAME=remotecontrol-tray
INSTALL_DIR=/opt/$APP_NAME
BIN_LINK=/usr/local/bin/$APP_NAME
DESKTOP_DST=/usr/share/applications/$APP_NAME.desktop

ICON_SRC="$INSTALL_DIR/lib/remotecontrol-tray.png"
ICON_DIR="/usr/share/icons/hicolor/256x256/apps"
ICON_DST="$ICON_DIR/remotecontrol-tray.png"

echo "======================================="
echo " Remote Control Tray Installer"
echo "======================================="

# require root
if [ "$(id -u)" -ne 0 ]; then
  echo "Please run as root (sudo)"
  exit 1
fi

# previous install detection
if [ -d "$INSTALL_DIR" ]; then
  printf "Previous installation detected. Overwrite? [y/N] "
  read -r ans
  case "$ans" in
    y|Y) ;;
    *) exit 0 ;;
  esac
fi

# create installation directory
rm -rf "$INSTALL_DIR"
mkdir -p "$INSTALL_DIR"
chmod -R a+rX "$INSTALL_DIR"

# copy application to /opt
cp -r "app/$APP_NAME/." "$INSTALL_DIR"

# fix permissions
chmod 755 "$INSTALL_DIR/bin/$APP_NAME"
chmod -R a+rX "$INSTALL_DIR"

echo "✓ Installed application to $INSTALL_DIR"

# version file
cp VERSION "$INSTALL_DIR"

# symlink
ln -sf "$INSTALL_DIR/bin/$APP_NAME" "$BIN_LINK"
echo "✓ Added $APP_NAME to PATH"

# desktop entry
cp "$APP_NAME.desktop" "$DESKTOP_DST"
chmod 644 "$DESKTOP_DST"
echo "✓ Installed desktop entry"

# Desktop icon
mkdir -p "$ICON_DIR"
cp "$ICON_SRC" "$ICON_DST"
chmod 644 "$ICON_DST"

update-desktop-database /usr/share/applications || true
gtk-update-icon-cache -q -t -f /usr/share/icons/hicolor || true

# autostart option
printf "Start app automatically on login? [y/N] "
read -r ans

case "$ans" in
  y|Y)
    REAL_USER="${SUDO_USER:-$(logname 2>/dev/null || echo "")}"

    if [ -n "$REAL_USER" ] && [ "$REAL_USER" != "root" ]; then
      REAL_HOME=$(getent passwd "$REAL_USER" | cut -d: -f6)
      AUTOSTART_DIR="$REAL_HOME/.config/autostart"

      mkdir -p "$AUTOSTART_DIR"
      cp "$DESKTOP_DST" "$AUTOSTART_DIR/$APP_NAME.desktop"

      sed -i '/^\[Desktop Entry\]/a NoDisplay=true' \
        "$AUTOSTART_DIR/$APP_NAME.desktop"

      chmod 644 "$AUTOSTART_DIR/$APP_NAME.desktop"

      chown "$REAL_USER":"$REAL_USER" \
        "$AUTOSTART_DIR/$APP_NAME.desktop" \
        "$AUTOSTART_DIR"

      echo "✓ Autostart enabled for $REAL_USER"
    else
      echo "⚠ Could not determine real user, skipping autostart"
    fi
    ;;
esac

# install uninstall script
cp uninstall.sh "$INSTALL_DIR/uninstall.sh"
chmod +x "$INSTALL_DIR/uninstall.sh"

echo
echo "✓ Installation complete"
echo "✓ Run '$APP_NAME' or find it in the application menu"
echo "✓ To uninstall: sudo $INSTALL_DIR/uninstall.sh"