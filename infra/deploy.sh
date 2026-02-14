#!/bin/bash
set -e

echo "🚀 Iniciando despliegue de METAMAPA..."

echo "🧹 Limpiando ambiente anterior..."
docker compose -f docker-compose.yml down --remove-orphans

echo "🏗️ Levantando base y servicios..."
docker compose up -d gateway

docker compose up -d --build

echo "🧹 Borrando imágenes viejas para ahorrar disco..."
docker image prune -f

echo "✅ Metamapa está en línea."