#!/bin/bash

# 1. Ir a la raíz del proyecto
cd ..

echo "🚀 Iniciando despliegue desde rama MAIN..."
git checkout main
git pull origin main

echo "📦 Compilando microservicios..."

# Compilamos TODOS los servicios que tienen cambios
# Agregamos Estadística (fundamental por el error que arreglamos) y Usuarios
services=(
    "Metamapa/M-Agregador-Service"
    "Metamapa/M-FuenteDinamica-Service"
    "Metamapa/M-FuenteEstatica-Service"
    "Metamapa/M-Estadistica-Service"
    "Metamapa/M-Usuarios-Service"
    "Metamapa/metamapa-service"
)

for service in "${services[@]}"; do
    echo "🛠️ Compilando $service..."
    if ! mvn -f "$service/pom.xml" clean package -DskipTests; then
        echo "❌ Error al compilar $service. Abortando."
        exit 1
    fi
done

# 3. Volver a la carpeta infra para el despliegue
cd infra

echo "🏗️ Reconstruyendo contenedores en Docker..."
# Usamos down -v si queremos limpiar volúmenes, pero con down normal está bien
sudo docker compose down

# Levantamos todo. El build es necesario para que tome los nuevos JARs
sudo docker compose up -d --build

# Limpieza de imágenes huérfanas para no llenar el disco de la Acer
sudo docker image prune -f

echo "✅ Despliegue completado."
echo "------------------------------------------------"
echo "🔍 Monitoreando arranque del Gateway (el último en subir)..."
# Monitoreamos el gateway porque ahora con el 'restart: always'
# es el que nos indica cuando todo el sistema está ruteando bien.
sudo docker compose logs -f gateway --tail 20