// Obtener todas las fuentes registradas en el agregador
async function obtenerFuentes() {
    const resp = await fetch(`${window.METAMAPA.API_AGREGADOR}/fuenteDeDatos`);
    return resp.ok ? resp.json() : [];
}

// Registrar una nueva fuente de datos en el agregador
async function registrarFuente(url) {
    const resp = await fetch(`${window.METAMAPA.API_AGREGADOR}/fuenteDeDatos`, {
        method: "POST",
        headers: {"Content-Type": "application/json"},
        body: JSON.stringify({url})
    });
    return resp.ok;
}