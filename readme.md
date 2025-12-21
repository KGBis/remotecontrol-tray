# Remote PC Control Tray

Remote PC Control Tray is the *server-side* component of an Android application that allows you to **wake up** and
**shut down** a computer remotely over the local network.

It is designed to be simple, lightweight and reliable, without unnecessary background services or complex configuration.

---

## How does it work?

The application opens a server socket on port **6800** and listens for requests sent by its Android counterpart.

Supported commands are:

- `INFO` — Retrieve basic information about the target computer (hostname, MAC addresses).
- `SHUTDOWN` — Shut down the computer remotely.

On the target computer, a **system tray icon** is available to:

- Display detected network interfaces (IP and MAC)
- Exit the application

That’s all — simple, explicit and enough for the intended use case.

---

## Tested platforms and results

### Fully working

- Windows 10 / 11 ✅
- Lubuntu 24.04.3 (Xfce 1.4.x & 2.2.x) ✅
- Linux Mint 22.2 (LXQt) ✅
- Linux Mint 22.2 (MATE) ✅
- Ubuntu MATE ✅

### Partially working / limitations

- Windows 7 ⚠️ <sup>see section below</sup>
- Manjaro Linux 25.0.10 (GNOME 49) ⚠️<sup>1</sup>
- Manjaro Linux 25.0.10 (KDE Plasma) ⚠️<sup>2</sup>
- Linux Mint 22.2 (Cinnamon) ⚠️<sup>2</sup>
- Fedora 43 Workstation (GNOME 49) ⚠️<sup>3</sup>

<sup>1</sup> System tray is shown, but the **Exit** action does not receive events. Exit button removed.  
<sup>2</sup> System tray is shown, but **mouse events are not received**. Information screen only.  
<sup>3</sup> System tray is not supported by the desktop environment. Information screen only.

---

## Windows 7 support

Remote PC Control Tray relies on **mDNS (multicast networking)** for device discovery.

Due to long-standing issues in the **Windows 7 networking stack** and JVM multicast implementations,  
mDNS is unreliable or non-functional on this platform.

As a result, **Windows 7 is not supported**.

---

## Security and network behavior

Remote PC Control Tray is intentionally designed to operate **only within a trusted local network**.

### Network exposure

- The application listens on **TCP port 6800**.
- It does **not** expose any HTTP endpoints or web interfaces.
- No ports are opened automatically or modified via firewall rules.
- All network communication is **local-network only** and unencrypted.

### Device discovery

- Device discovery is performed using **mDNS (multicast DNS)**.
- mDNS traffic is limited to the local subnet and is **not routable** across networks.
- No centralized servers, relays or external services are used.

### Authentication and authorization

- There is **no authentication mechanism** built into the protocol.
- Any device on the same local network capable of connecting to port 6800 can send commands.

This is a **deliberate design decision** to keep the application simple and dependency-free.
It assumes the local network is trusted.

### Intended usage model

This software is intended to be used in:

- Home networks
- Private LANs
- Isolated or controlled environments

It is **not designed** for:

- Public or shared networks
- Internet-facing deployments
- Untrusted Wi-Fi environments

### Recommendations

If additional security is required, consider:

- Restricting access to port 6800 via firewall rules
- Running the application only on private network interfaces
- Using VLANs or network segmentation
- Adding an application-level authentication layer (future work)

### Wake-on-LAN considerations

Wake-on-LAN packets are broadcast-based by nature and do not carry authentication.
Ensure that your network is configured accordingly and that only trusted devices can
send broadcast packets.

---

**In short:**  
Remote PC Control Tray favors **simplicity and transparency** over complex security layers.
You are fully in control of where and how it is deployed.

---

## Bugs / Contact

If you find a bug or have suggestions, feel free to:

- Open an issue on GitHub:  
  https://github.com/KGBis/remotecontrol-tray/issues
- Or contact me directly at:  
  [kike.g.garcia@gmail.com](mailto:kike.g.garcia@gmail.com)

---

## License

RemoteControlTray is licensed under the **GNU Lesser General Public License v3.0 (LGPL-3.0)**
(or later). See the `LICENSE` file for details.

---

## Technical notes

The application is written entirely in **Java** and currently targets **Java 11**.

> A **Java 11+ JRE/JDK** must be installed on the target computer to run this program.

A future goal is to provide a standalone executable with an embedded JRE.

---

## Dependencies

- **Google Guice** — Lightweight DI container for IoC
- **Google Guava** — Utility libraries and classpath scanning
- **JCommander** — Command-line argument parsing
- **SLF4J + Logback** — Logging
- **Apache Commons Lang 3**
- **OSHI-core** — OS and hardware information
- **Dorkbox SystemTray**
- **Lombok**

---

## Note for developers

You can enable automatic versioning by installing the following pre-commit hook.

Copy the script below to:

> PROJECT_HOME/.git/hooks/pre-commit

On Linux/macOS systems, remember to make it executable:

```bash
chmod 775 pre-commit
```

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

