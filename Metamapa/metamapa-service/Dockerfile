# ETAPA 1: Compilación (Usamos Maven para armar el JAR con el código nuevo)
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app

# Copiamos el pom.xml y el código fuente
COPY pom.xml .
COPY src ./src

# Compilamos el proyecto (esto mete la carpeta static adentro del JAR)
RUN mvn clean package -DskipTests

# ETAPA 2: Runtime (Imagen ligera para correr la app)
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Copiamos el JAR que acabamos de buildear en la etapa anterior
# Usamos un comodín (*) por si cambia la versión en el pom.xml
COPY --from=build /app/target/Metamapa-Service-1.0.0.jar app.jar
EXPOSE 9000

ENTRYPOINT ["java", "-jar", "app.jar"]