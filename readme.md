# Remote PC Control Tray

This is the 'server' side of an Android App which can be used to wake-on-LAN and shutdown a computer remotely.

## How it works?

This program basically opens a server socket on port 6800 and listens to messages sent by the App counterpart.
The app can send `INFO` requests to gather target computer hostname and MAC address and `SHUTDOWN` requests to turn off
the computer. That's it.

In the target computer where this program is installed a tray icon will be available so you can see the network
interfaces IPs and their MACs or exit the program. Simple but enough.

Currently __A Java 11+ JRE/JDK is needed to be installed in the target computer to run this program__. The idea is to be
able to build an
executable with embedded dependencies and JRE.

## Technically speaking

It is written entirely in Java and compiles for Java 11, so at the moment, a JRE 11+ is needed in the target computer.

### Fedora 43 Workstation (Gnome + Wayland)

```log
Gtk-Message: 21:34:37.718: Failed to load module "canberra-gtk-module"
Gtk-Message: 21:34:37.718: Failed to load module "pk-gtk-module"
...
2025-12-09 21:51:29,939 DEBUG [AWT-EventQueue-0]  d.s.SystemTray - Version 4.4
2025-12-09 21:51:29,939 DEBUG [AWT-EventQueue-0]  d.s.SystemTray - OS: Linux
2025-12-09 21:51:29,939 DEBUG [AWT-EventQueue-0]  d.s.SystemTray - Arch: amd64
2025-12-09 21:51:29,939 DEBUG [AWT-EventQueue-0]  d.s.SystemTray - Oracle Corporation OpenJDK 64-Bit Server VM 21.0.8
2025-12-09 21:51:29,939 DEBUG [AWT-EventQueue-0]  d.s.SystemTray - JPMS enabled: true
2025-12-09 21:51:29,939 DEBUG [AWT-EventQueue-0]  d.s.SystemTray - Is Auto sizing tray/menu? true
2025-12-09 21:51:29,940 DEBUG [AWT-EventQueue-0]  d.s.SystemTray - Is JavaFX detected? false
2025-12-09 21:51:29,940 DEBUG [AWT-EventQueue-0]  d.s.SystemTray - Is SWT detected? false
2025-12-09 21:51:29,940 DEBUG [AWT-EventQueue-0]  d.s.SystemTray - Java Swing L&F: GTK
2025-12-09 21:51:29,940 DEBUG [AWT-EventQueue-0]  d.s.SystemTray - Auto-detecting tray type
2025-12-09 21:51:29,940 DEBUG [AWT-EventQueue-0]  d.s.SystemTray - Force GTK2: false
2025-12-09 21:51:29,940 DEBUG [AWT-EventQueue-0]  d.s.SystemTray - Prefer GTK3: true
2025-12-09 21:51:29,967 DEBUG [AWT-EventQueue-0]  d.s.SystemTray - Currently using the 'Gnome' desktop environment
NAME="Freedesktop SDK"
VERSION="25.08 (Flatpak runtime)"
VERSION_ID=25.08
ID=org.freedesktop.platform
PRETTY_NAME="Freedesktop SDK 25.08 (Flatpak runtime)"
BUG_REPORT_URL=https://gitlab.com/freedesktop-sdk/freedesktop-sdk/issues

2025-12-09 21:51:29,968 DEBUG [AWT-EventQueue-0]  d.s.SystemTray - Currently using the 'gnome' session type
2025-12-09 21:51:29,976 ERROR [AWT-EventQueue-0]  d.s.SystemTray - GNOME shell detected, but UNDEFINED shell version. This should never happen. Falling back to GtkStatusIcon. Please create an issue with as many details as possible.
2025-12-09 21:51:29,988 DEBUG [AWT-EventQueue-0]  d.j.l.GtkLoader - GTK: libgtk-3.so.0
2025-12-09 21:51:29,988 DEBUG [GTK Native Event Loop] d.j.l.GtkEventDispatch - Running GTK Native Event Loop
2025-12-09 21:51:30,094 DEBUG [AWT-EventQueue-0]  d.s.SystemTray - GTK Version: 3.24.51
2025-12-09 21:51:30,096 DEBUG [AWT-EventQueue-0]  d.s.SystemTray - Is the system already running GTK? false
2025-12-09 21:51:30,173 WARN  [AWT-EventQueue-0]  d.j.l.GtkTheme - Unable to get tray image size. Using fallback: 24
2025-12-09 21:51:30,182 DEBUG [AWT-EventQueue-0]  d.s.SystemTray - Tray indicator image size: 24
2025-12-09 21:51:30,182 DEBUG [AWT-EventQueue-0]  d.s.SystemTray - Tray menu image size: 16
2025-12-09 21:51:30,188 DEBUG [AWT-EventQueue-0]  d.s.SystemTray - Using Fake CheckMark: false
2025-12-09 21:51:30,293 INFO  [AWT-EventQueue-0]  d.s.SystemTray - Successfully loaded type: _GtkStatusIconNativeTray
2025-12-09 21:51:30,319 DEBUG [AWT-EventQueue-0]  d.s.SystemTray - Resizing image to 24
```

### Dependencies

* Google Guice. A DI lightweight container for IoC. No more `new ClassX()` across the code.
* Google Guava. As it's a Guice dependency why not use it for class scanning!
* JCommander. A great, small and easy to use command line argument parser.
* SLF4J and Logback for logging.
* Apache Commons Lang 3
* OSHI-core. Operating System and Hardware Information
  library. [https://github.com/oshi/oshi](https://github.com/oshi/oshi)
* Lombok.
  At the moment of writing this, december 2025, it has been only tested under Windows 10 and 11.

### Still pending... The TO DO list

When I have the time, I'll install an Ubuntu distribution with VirtualBox to test if it works on Linux.
It will also, most likely, work correctly on macOS, but who knows. I don't own any Apple device.

And yes, as you probably have noticed, not a single unit test has been written... yet!
I'm an old school developer 🤣

### Bugs

if you find a bug or want to contact me just drop me a line to [kike.g.garcia@gmail.com](mailto:kike.g.garcia@gmail.com)

## Acknowledges / Inspiration

This project is based loosely on the 'Remote Shutdown Server' by Isah Rikovic (rikovicisah @ gmail.com) which I
discovered some years ago while looking for a remote shutdown app for my Android device.

Original JAR file can be
found @ [https://github.com/rikovicisah/remoteshutdownpc](https://github.com/rikovicisah/remoteshutdownpc)

## License

This software is distributed under the __GNU General Public License version 2 (GPLv2)__

* You may use the application free of charge.
* You may study and modify the source code.
* You may redistribute it or publish modified versions under the same GPLv2 license.
* You may not distribute modified versions as proprietary software or sell the software without complying with GPLv2.
* You must include credits and the original license notice when publishing modifications.

## Note for developers

Copy/paste this script as `PROJECT_HOME/.git/hooks/pre-commit` to get automated version change when commiting changes
if you want to keep the project's versioning pattern.

```bash
#!/bin/bash

set -e

echo "[pre-commit] Updating version..."

# Current date
DATE=$(date +"%Y.%m.%d")

# Get current version from pom.xml
CURRENT=$(mvn help:evaluate -Dexpression=project.version -q -DforceStdout)

# if current version starts with current date, increment "build"
if [[ "$CURRENT" == "$DATE."* ]]; then
    BUILD=${CURRENT##*.}
    BUILD=$((BUILD + 1))
else
    BUILD=1
fi

NEW_VERSION="$DATE.$BUILD"

echo "[Version Hook] New version: $NEW_VERSION"

# set new version
mvn -q versions:set -DnewVersion=$NEW_VERSION -DgenerateBackupPoms=false
mvn -q versions:commit

# write version file
ROOT=$(git rev-parse --show-toplevel)
VERSION_FILE="$ROOT/src/main/resources/version.txt"
echo $NEW_VERSION > $VERSION_FILE

# Add modified pom.xml to the commit
git add pom.xml
git add $VERSION_FILE

echo "[Version Hook] pom.xml updated. Done."
```

