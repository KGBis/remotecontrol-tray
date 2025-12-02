# Remote Shutdown Tray

## Acknowledges
This project is based on the 'RemoteShutdownPCServer' by Isah Rikovic (rikovicisah @ gmail.com)

Original JAR file found @ https://github.com/rikovicisah

## Features
* Runs in the OS' system tray. No ugly cmd/shell window.<br>
* Tray icon's right click command `Show computer IP(s)` to view all (non-loopback) interfaces' IPs<br>
* Can be installed easily as a start-up application both in Windows and Linux (probably in iOS too but who knows!)
* Compiled for Java 8 so runnable from almost any computer

### Note
Copy/paste this script as `PROJECT_HOME/.git/hooks/pre-commit` to get automated version change when commiting changes

```bash
#!/bin/bash

set -e

echo el 

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

# Add modified pom.xml to the commit
git add pom.xml

echo "[Version Hook] pom.xml updated. Done."
```

