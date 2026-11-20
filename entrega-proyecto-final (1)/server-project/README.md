# Proyecto servidor

Configuracion: `src/co/edu/uptc/server/ServerConfig.java` (PORT y DATA_FILE).
Para el servidor remoto: PORT = ultimas 4 cifras del codigo estudiantil (si empieza en 0, anteponer un 1: 0123 -> 10123).

Compilar y generar el jar (Java 11+):

    mkdir out
    javac --release 11 -encoding UTF-8 -d out $(find src -name "*.java")
    jar cfe court-reservations-server.jar co.edu.uptc.server.Server -C out .

Ejecutar: `java -jar court-reservations-server.jar` (crea `data/system.dat` junto al jar).
Administrador predefinido: admin / admin123.
