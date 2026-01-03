#!/bin/bash

cd ..

echo "🚀 Iniciando despliegue desde rama MAIN..."

git checkout main
git pull origin main

echo "📦 Compilando microservicios..."
mvn -f Metamapa/M-Agregador-Service/pom.xml clean package -DskipTests
mvn -f Metamapa/M-FuenteDinamica-Service/pom.xml clean package -DskipTests
mvn -f Metamapa/M-FuenteEstatica-Service/pom.xml clean package -DskipTests

# 3. Volver a la carpeta infra para el despliegue
cd infra

echo "🏗️ Reconstruyendo contenedores en Docker..."
sudo docker compose down
sudo docker compose up -d --build

sudo docker image prune -f

echo "✅ Despliegue completado."
echo "------------------------------------------------"
echo "🔍 Monitoreando arranque del Agregador..."
sudo docker compose logs -f agregador | grep -m 1 "Carga completada"