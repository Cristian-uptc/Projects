# Proyecto cliente

Al iniciar, el cliente pide IP y puerto (por defecto 127.0.0.1:1234).

Compilar y generar el jar (Java 11+):

    mkdir out
    javac --release 11 -encoding UTF-8 -d out $(find src -name "*.java")
    jar cfe court-reservations-client.jar co.edu.uptc.client.ClientApp -C out .

Ejecutar: `java -jar court-reservations-client.jar`
