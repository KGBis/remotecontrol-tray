# Remote PC Control Tray

This is the 'server' side of an Android App which can be used to wake-on-LAN and shutdown a computer remotely.

## How does it works?

This program basically opens a server socket on port 6800 and listens to messages sent by the App counterpart.
The app can send `INFO` requests to gather target computer hostname and MAC address and `SHUTDOWN` requests to turn off
the computer. That's it.

In the target computer where this program is installed a tray icon will be available so you can see the network
interfaces IPs and their MACs or exit the program. Simple but enough.

### Where has been tested and results

- Windows 7/10/11 ️✅
- Lubuntu Xfce 1.4.x & 2.2.x ✅
- Linux Mint LXQt ✅
- Ubuntu MATE ✅

- Manjaro Linux 25.0.10 with Gnome 49 ⚠️<sup>1</sup>
- Manjaro Linux 25.0.10 with KDE Plasma ⚠️<sup>2</sup>
- Fedora 43 Workstation with Gnome 49 ⚠️<sup>3</sup>

<sup>1</sup> System tray is shown but `exit` button event is ignored or not received. Button removed.<br>
<sup>2</sup> System tray is shown but it receives no events from mouse, so just information screen available.<br>
<sup>3</sup> System tray not supported at all, so just information screen available.

### Bugs

if you find a bug or want to contact me just drop me a line to [kike.g.garcia@gmail.com](mailto:kike.g.garcia@gmail.com)
or [open an issue](https://github.com/KGBis/remotecontrol-tray/issues) in Github.

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

## Technical Stuff

This program is written entirely in Java and it's compiled for Java 11, so at the moment, a JRE 11+ is needed in the
target computer.

Currently __A Java 11+ JRE/JDK is needed to be installed in the target computer to run this program__. The idea is to be
able to build an
executable with embedded dependencies and JRE.

### The Tray Icon

- AWT system tray supported in Windows 10/11 and Linux running Xfce and Qt desktop
- Dorkbox System Tray supported on Linux Gnome and/or Wayland

### Dependencies

* Google Guice. A DI lightweight container for IoC. No more `new ClassX()` across the code.
* Google Guava. As it's a Guice dependency why not use it for class scanning!
* JCommander. A great, small and easy to use command line argument parser.
* SLF4J and Logback for logging.
* Apache Commons Lang 3.
* OSHI-core. Operating System and Hardware Information
  library. [https://github.com/oshi/oshi](https://github.com/oshi/oshi)
* Dorkbox SystemTray.
* Lombok.

### Still pending... The TO DO list

When I have the time, I'll install an Ubuntu distribution with VirtualBox to test if it works on Linux.
It will also, most likely, work correctly on macOS, but who knows. I don't own any Apple device.

And yes, as you probably have noticed, not a single unit test has been written... yet!
I'm an old school developer 🤣

### Note for developers

Copy/paste this script as `PROJECT_HOME/.git/hooks/pre-commit` to get automated version change when commiting changes
if you want to keep the project's versioning pattern.

On Linux/MAC systems, do not forget to set this file executable with `chmod 775 pre-commit
`

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

