# Proyecto cliente

Configuracion: `src/co/edu/uptc/client/communication/ClientConfig.java` (HOST y PORT).
Para el servidor remoto: HOST = 10.4.73.15 y PORT = el mismo del servidor.

Compilar y generar el jar (Java 11+):

    mkdir out
    javac --release 11 -encoding UTF-8 -d out $(find src -name "*.java")
    jar cfe court-reservations-client.jar co.edu.uptc.client.ClientApp -C out .

Ejecutar: `java -jar court-reservations-client.jar`
