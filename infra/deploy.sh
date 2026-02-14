#!/bin/bash
set -e

echo "🚀 Iniciando despliegue de METAMAPA..."

# -----------------------------------------------
# LIMPIEZA DE ZOMBIES (antes de docker compose down)
# -----------------------------------------------

if [ -d "nginx.conf" ]; then
    echo "⚠️ Se detectó carpeta 'nginx.conf' (Zombie). Eliminando..."
    sudo rm -rf nginx.conf
fi

if [ -d "prometheus.yml" ]; then
    echo "⚠️ Se detectó carpeta 'prometheus.yml' (Zombie). Eliminando..."
    sudo rm -rf prometheus.yml
fi

# -----------------------------------------------
# VALIDACIÓN DE ARCHIVOS REQUERIDOS
# -----------------------------------------------

if [ ! -f "nginx.conf" ]; then
    echo "❌ ERROR: nginx.conf no existe como archivo"
    echo "   Ubicación esperada: $(pwd)/nginx.conf"
    exit 1
fi

echo "✅ nginx.conf encontrado correctamente"

# -----------------------------------------------
# DESPLIEGUE
# -----------------------------------------------

echo "🧹 Bajando contenedores viejos..."
docker compose down --remove-orphans || true

echo "🏗️ Levantando servicios..."
docker compose up -d --build

echo "✅ Despliegue finalizado."