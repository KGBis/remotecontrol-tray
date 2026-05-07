$ErrorActionPreference = "Stop"

# Maven
mvn clean package

# Artifact name and version from pom.xml
$pom = Get-Content "./pom.xml"

$ARTIFACT = ($pom | Select-String "<artifactId>" | Select-Object -First 1).Line `
        -replace ".*<artifactId>(.*)</artifactId>.*", '$1'

$APP_VERSION = ($pom | Select-String "<version>" | Select-Object -First 1).Line `
        -replace ".*<version>(.*)</version>.*", '$1'

Write-Host "Program name  -> $ARTIFACT"
Write-Host "Program version -> $APP_VERSION"

# Paths
$TARGET_PATH = "./target"
$DIST_PATH = "$TARGET_PATH/dist/app"
$INSTALLERS_PATH = "$TARGET_PATH/installer"

# Clean directories
Remove-Item $DIST_PATH -Recurse -Force -ErrorAction SilentlyContinue
Remove-Item $INSTALLERS_PATH -Recurse -Force -ErrorAction SilentlyContinue

New-Item -ItemType Directory -Force -Path $DIST_PATH | Out-Null
New-Item -ItemType Directory -Force -Path $INSTALLERS_PATH | Out-Null

$JAR_NAME = "$ARTIFACT.jar"

# Copy jar
Copy-Item "$TARGET_PATH/$JAR_NAME" "$DIST_PATH"

# Flags
$ICON_FLAG = "--icon", ".packaging/icons/computer.ico"

$NON_LINUX_FLAGS = @(
    "--about-url", "https://github.com/KGBis/remotecontrol-tray",
    "--license-file", "LICENSE"
)

$WIN_FLAGS = @(
    "--win-menu",
    "--win-shortcut",
    "--win-per-user-install"
)

$MAIN_FLAGS = @(
    "--name", $ARTIFACT,
    "--input", $DIST_PATH,
    "--main-jar", $JAR_NAME,
    "--dest", "$INSTALLERS_PATH/app",
    "--app-version", $APP_VERSION,
    "--copyright", "Copyright © 2026 Enrique García (KGBis)",
    "--vendor", "KGBis"
)

Write-Host "Running jpackage msi..."

jpackage `
    --type msi `
    @MAIN_FLAGS `
    @NON_LINUX_FLAGS `
    @ICON_FLAG `
    @WIN_FLAGS

Copy-Item "$INSTALLERS_PATH/app/*.*" .
Remove-Item "$INSTALLERS_PATH/app" -Recurse -Force -ErrorAction SilentlyContinue
Write-Host "Done! Installer generated."