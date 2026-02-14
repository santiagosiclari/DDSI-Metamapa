#!/bin/bash
set -e

echo "🚀 Iniciando despliegue de METAMAPA..."

# Limpieza de zombies
if [ -d "nginx.conf" ]; then
    echo "⚠️ Eliminando zombie nginx.conf..."
    sudo rm -rf nginx.conf
fi

if [ -d "prometheus.yml" ]; then
    echo "⚠️ Eliminando zombie prometheus.yml..."
    sudo rm -rf prometheus.yml
fi

# Validar archivo
if [ ! -f "nginx.conf" ]; then
    echo "❌ ERROR: nginx.conf no existe"
    ls -la
    exit 1
fi

echo "✅ nginx.conf validado"

# Deploy
echo "🧹 Deteniendo contenedores..."
docker compose down --remove-orphans || true

echo "🏗️ Iniciando servicios..."
docker compose up -d --build

echo "✅ Despliegue completado"
docker compose ps