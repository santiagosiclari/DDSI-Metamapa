# --- Etapa 1: Construcción (Build) ---
FROM maven:3.9.6-eclipse-temurin-21-jammy AS build
WORKDIR /app

# 1. Copiar el pom.xml y descargar dependencias (esto optimiza el cache de Docker)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# 2. Copiar el código fuente y compilar
COPY src ./src
RUN mvn clean package -DskipTests

# --- Etapa 2: Ejecución (Runtime) ---
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

# Copiar el archivo JAR generado desde la etapa de construcción
# Maven guarda el resultado en la carpeta 'target'
COPY --from=build /app/target/*.jar app.jar

# Exponer el puerto (se define en el docker-compose)
ENTRYPOINT ["java", "-jar", "app.jar"]