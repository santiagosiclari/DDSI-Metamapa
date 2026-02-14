#!/bin/bash
set -e

echo "🚀 Iniciando despliegue de METAMAPA..."

if [ -d "nginx.conf" ]; then
    echo "⚠️ Se detectó carpeta 'nginx.conf' (Zombie). Eliminando con sudo..."
    sudo rm -rf nginx.conf
fi

if [ -d "prometheus.yml" ]; then
    echo "⚠️ Se detectó carpeta 'prometheus.yml' (Zombie). Eliminando con sudo..."
    sudo rm -rf prometheus.yml
fi
# -----------------------------------------------

echo "🧹 Bajando contenedores viejos..."
# Usamos el docker-compose para bajar todo ordenadamente
docker compose down --remove-orphans || true

echo "🏗️ Levantando servicios..."
docker compose up -d --build

echo "✅ Despliegue finalizado."