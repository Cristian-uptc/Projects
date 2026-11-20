# Proyecto servidor

Configuracion: ServerConfig.DEFAULT_PORT (1234). Para otro puerto: java -jar court-reservations-server.jar 6000

Compilar y generar el jar (Java 11+):

    mkdir out
    javac --release 11 -encoding UTF-8 -d out $(find src -name "*.java")
    jar cfe court-reservations-server.jar co.edu.uptc.server.Server -C out .

Ejecutar: `java -jar court-reservations-server.jar` (crea `data/system.dat` junto al jar).
Administrador predefinido: admin / admin123.
