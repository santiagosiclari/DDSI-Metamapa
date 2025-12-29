# Etapa 1: Runtime (usamos una imagen ligera de Java 21)
FROM eclipse-temurin:21-jre-alpine

# Directorio de trabajo dentro del contenedor
WORKDIR /app

# Copiamos el JAR que generaste con Maven desde tu carpeta target/
# Asegúrate de que el nombre coincida con el de tu pom.xml
COPY target/Metamapa-Service-1.0.0.jar app.jar

# Exponemos el puerto 9000 que es el que usa tu app
EXPOSE 9000

# Comando para ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]