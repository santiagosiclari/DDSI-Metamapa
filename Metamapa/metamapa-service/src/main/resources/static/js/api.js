// --- CRUD Hechos ---
async function crearHecho(e) { /* versión completa anterior, la mantenés igual */ }

async function obtenerHechos() {
    const resp = await fetch(`${window.METAMAPA.API_AGREGADOR}/hechos`);
    return resp.ok ? resp.json() : [];
}

// --- Colecciones ---
async function obtenerColecciones() {
    const resp = await fetch(`${window.METAMAPA.API_AGREGADOR.replace("/api-agregador", "/api-colecciones")}`);
    return resp.ok ? resp.json() : [];
}

async function obtenerHechosDeColeccion(id) {
    const resp = await fetch(`${window.METAMAPA.API_AGREGADOR.replace("/api-agregador", `/api-colecciones/${id}/hechos`)}`);
    return resp.ok ? resp.json() : [];
}

// --- Fuentes ---
async function obtenerFuentes() {
    const resp = await fetch(`${window.METAMAPA.API_AGREGADOR}/fuenteDeDatos`);
    return resp.ok ? resp.json() : [];
}

// --- Acciones ---
async function actualizarHechos() {
    const resp = await fetch(`${window.METAMAPA.API_AGREGADOR}/actualizarHechos`, { method: "POST" });
    alert(resp.ok ? "✅ Hechos actualizados desde las fuentes" : "⚠️ Error al actualizar");
}

async function curarHechos() {
    const resp = await fetch(`${window.METAMAPA.API_AGREGADOR}/consensuarHechos`, { method: "POST" });
    alert(resp.ok ? "🧠 Curado completado correctamente" : "⚠️ Error al curar hechos");
}
