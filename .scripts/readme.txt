Example file to simulate the CI/CD workflow for the Linux self-executable installer

To run:

1. Open a terminal in the project root directory.
2. Build the project with: mvn clean package
3. Make the test script executable (if needed):
   chmod +x ./.scripts/jpackage_linux_test.sh
4. Run the script:
   ./.scripts/jpackage_linux_test.sh

This will generate:
- remotecontrol-tray-linux.run in the project root
Execute with sudo.
Remove it after testing to keep your workspace clean.

---------

Archivo de ejemplo para simular el flujo de trabajo de CI/CD para el instalador autoejecutable de Linux

Para ejecutar:

1. Abra un terminal en el directorio raíz del proyecto.
2. Compilar el proyecto con: mvn clean package
3. Hacer ejecutable el script de prueba (si es necesario):
   chmod +x ./.scripts/jpackage_linux_test.sh
4. Ejecutar el script:
   ./.scripts/jpackage_linux_test.sh

Se generará:
- remotecontrol-tray-linux.run en el directorio raíz del proyecto
Ejecutar con sudo.
Eliminar después de la prueba para mantener limpio el workspace.