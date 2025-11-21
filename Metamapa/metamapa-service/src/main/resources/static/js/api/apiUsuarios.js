// Registrar un nuevo usuario
async function registrarUsuario(usuario) {
    // 💡 CORRECCIÓN: Usar la variable global y el endpoint correcto (/api-auth/register)
    const url = `${window.METAMAPA.API_USUARIOS}/api-auth/register`;

    const resp = await fetch(url, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(usuario)
    });

    // Devolvemos un objeto con éxito y posible mensaje de error
    if (!resp.ok) {
        // La deserialización del error debe ser cuidadosa
        const error = await resp.json().catch(() => ({}));
        return { ok: false, mensaje: error.mensaje || resp.statusText };
    }
    return { ok: true };
}