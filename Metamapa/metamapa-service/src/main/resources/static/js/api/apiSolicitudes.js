//Obtener solicitudes de eliminación
async function obtenerSolicitudesEliminacion() {
    const resp = await fetch(`${window.METAMAPA.API_SOLICITUDES}/solicitudesEliminacion`);
    return resp.ok ? resp.json() : [];
}

//Enviar solicitud de eliminación
async function enviarSolicitudEliminacion(solicitud) {
    const resp = await fetch(`${window.METAMAPA.API_SOLICITUDES}/solicitudesEliminacion`, {
        method: "POST",
        headers: {"Content-Type": "application/json"},
        body: JSON.stringify(solicitud)
    });
    return resp.ok;
}

//Procesar solicitud de eliminación (aprobar/rechazar)
async function procesarSolicitudEliminacion(aprobada, id) {
    const accion = aprobada ? "APROBAR" : "RECHAZAR";
    const resp = await fetch(`${window.METAMAPA.API_SOLICITUDES}/solicitudesEliminacion/${id}`, {
        method: "PATCH",
        headers: {"Content-Type": "application/json"},
        body: JSON.stringify({ accion })
    });
    if (resp.ok) {
        alert(`Solicitud ${accion} procesada correctamente ✅`);
        await mostrarSolicitudesView(); // refresca la vista
    } else {
        const err = await resp.text();
        alert(`❌ Error al procesar solicitud: ${err}`);
    }
}

//Enviar solicitud de edicion
async function enviarSolicitudEdicion(solicitud) {
    const resp = await fetch(`${window.METAMAPA.API_SOLICITUDES}/solicitudesEdicion`, {
        method: "POST",
        headers: {"Content-Type": "application/json"},
        body: JSON.stringify(solicitud)
    });
    return resp.ok;
}

// Procesar solicitud de edición (aprobar/rechazar)
async function procesarSolicitudEdicion(aprobada, id) {
    // Definir el nuevo estado según la acción
    const estado = aprobada ? "APROBADA" : "RECHAZADA";
    try {
        const resp = await fetch(`${window.METAMAPA.API_SOLICITUDES}/solicitudesEdicion/${id}`, {
            method: "PATCH",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ estado })
        });
        if (resp.ok) {
            alert(`Solicitud de edición ${estado.toLowerCase()} correctamente ✅`);
            await mostrarSolicitudesView(); // refresca la lista
        } else {
            const err = await resp.text();
            alert(`❌ Error al actualizar el estado de la solicitud:\n${err}`);
        }
    } catch (error) {
        console.error("Error de red al procesar solicitud de edición:", error);
        alert("❌ Error de red al procesar solicitud de edición");
    }
}

// Obtener solicitudes de edición
async function obtenerSolicitudesEdicion() {
    const resp = await fetch(`${window.METAMAPA.API_SOLICITUDES}/solicitudesEdicion`);
    return resp.ok ? resp.json() : [];
}