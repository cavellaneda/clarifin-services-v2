# Usa Amazon Corretto 17 como imagen base
FROM amazoncorretto:17-alpine

# Copia el JAR construido al contenedor
COPY build/libs/services-0.0.2-SNAPSHOT.jar app.jar

# Define el comando para ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "/app.jar"]

# Expón el puerto en el que la aplicación se ejecutará
EXPOSE 8080
