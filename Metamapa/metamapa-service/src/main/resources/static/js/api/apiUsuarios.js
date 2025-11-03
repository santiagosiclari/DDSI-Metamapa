// Registrar un nuevo usuario
async function registrarUsuario(usuario) {
    const resp = await fetch('http://localhost:9001/register', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(usuario)
    });
    // Devolvemos un objeto con éxito y posible mensaje de error
    if (!resp.ok) {
        const error = await resp.json().catch(() => ({}));
        return { ok: false, mensaje: error.mensaje || resp.statusText };
    }
    return { ok: true };
}