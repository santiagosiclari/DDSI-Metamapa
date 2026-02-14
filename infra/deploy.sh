#!/bin/bash
set -e

echo "🚀 Iniciando despliegue de METAMAPA..."

echo "🧹 Deteniendo contenedores..."
docker compose down --remove-orphans || true

docker rmi metamapa-gateway infra-gateway 2>/dev/null || true

echo "🏗️ Construyendo y levantando servicios..."
docker compose up -d --build

echo "✅ Despliegue completado"
docker compose ps