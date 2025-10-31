console.log("api.js cargado correctamente");
// Crear un nuevo hecho en la fuente dinámica
async function crearHecho(e) {
    e.preventDefault();
    const f = e.target;
    // === Armar el objeto idéntico al JSON de Postman ===
    const data = {
        titulo: f.titulo.value.trim(),
        descripcion: f.descripcion.value.trim(),
        categoria: f.categoria.value.trim(),
        latitud: parseFloat(f.latitud.value),
        longitud: parseFloat(f.longitud.value),
        fechaHecho: f.fechaHecho?.value ||  new Date().toISOString().slice(0, 16),
        idUsuario: parseInt(f.idUsuario.value), //TODO: obtener del login
        fuenteId: parseInt(f.idFuente.value),
        anonimo: f.anonimo.checked
    };

    console.log("Enviando hecho:", data);

    // === Validar que todos los obligatorios estén presentes ===
    if (!data.titulo || !data.descripcion || isNaN(data.fuenteId)) {
        alert("Debes completar al menos título, descripción y fuente.");
        return;
    }

    // === Enviar al backend exactamente como Postman ===
    const formData = new FormData();
    formData.append("hecho", JSON.stringify(data));

    // ✅ Agregar todos los archivos seleccionados
    const input = document.getElementById("inputMultimedia");
    for (let i = 0; i < input.files.length; i++) {
        formData.append("archivos", input.files[i]);
    }

    // Enviar al backend
    const resp = await fetch(`http://localhost:9001/api-fuentesDeDatos/${data.fuenteId}/hechos`, {
        method: "POST",
        body: formData // NO JSON.stringify, NO headers
    });
    const res = document.getElementById("resultadoHecho");
    if (resp.ok) {
        const json = await resp.json();
        res.innerHTML = `✅ Hecho creado correctamente (ID: ${json.id || "sin id"})`;
        res.className = "text-success";

        // Cerrar modal y limpiar
        const modal = bootstrap.Modal.getInstance(document.getElementById("modalHecho"));
        modal.hide();
        limpiarFormularioHecho();
    } else {
        const errorTxt = await resp.text();
        res.innerHTML = `Error al crear el hecho: ${errorTxt}`;
        res.className = "text-danger";
    }
}

// Obtener todos los hechos curados del agregador
async function obtenerHechos() {
    const resp = await fetch(`${window.METAMAPA.API_AGREGADOR}/hechos`);
    return resp.ok ? resp.json() : [];
}

// Obtener hechos específicos de una colección
async function obtenerHechosDeColeccion(id) {
    const resp = await fetch(`${window.METAMAPA.API_COLECCIONES}/${id}/hechos`);
    return resp.ok ? resp.json() : [];
}

// Helper para armar un criterio desde un div
function armarCriterio(div) {
    const get = n => div.querySelector(`[name="${n}"]`)?.value?.trim() || null;
    const num = n => parseFloat(get(n));
    return {
        tipo: get("tipo"),
        valor: get("valor"),
        inclusion: get("inclusion") === "true",
        ...(get("fechaDesde") && { fechaDesde: get("fechaDesde") }),
        ...(get("fechaHasta") && { fechaHasta: get("fechaHasta") }),
        ...(get("idFuenteDeDatos") && { idFuenteDeDatos: parseInt(get("idFuenteDeDatos")) }),
        ...(num("latitud") && { latitud: num("latitud") }),
        ...(num("longitud") && { longitud: num("longitud") }),
        ...(num("radio") && { radio: num("radio") }),
        ...(get("tipoMultimedia") && { tipoMultimedia: get("tipoMultimedia") })
    };
}

// Crear o actualizar colección
async function crearColeccion(e) {
    e.preventDefault();
    const f = e.target;

    // --- recolectar criterios ---
    const criterios = [...document.querySelectorAll("#criteriosContainer .criterio-box")].map(armarCriterio);

    const data = {
        titulo: f.titulo.value.trim(),
        descripcion: f.descripcion.value.trim(),
        consenso: f.consenso.value,
        criterios
    };

    const id = f.idColeccion.value;
    const url = id
        ? `${window.METAMAPA.API_COLECCIONES}/${id}`
        : `${window.METAMAPA.API_COLECCIONES}/`;
    const method = id ? "PUT" : "POST";

    console.log("Enviando colección:", data);

    const resp = await fetch(url, {
        method,
        headers: {"Content-Type": "application/json"},
        body: JSON.stringify(data)
    });

    const res = document.getElementById("resultadoColeccion");
    if (resp.ok) {
        const json = await resp.json();
        res.innerHTML = `✅ Colección ${id ? "actualizada" : "creada"} (${json.handle || json.id})`;
        res.className = "text-success";
        const modal = bootstrap.Modal.getInstance(document.getElementById("modalColeccion"));
        modal.hide();
        limpiarFormularioColeccion();
        await mostrar("colecciones");
    } else {
        const txt = await resp.text();
        res.innerHTML = `Error: ${txt}`;
        res.className = "text-danger";
    }
}

// Obtener todas las colecciones
async function obtenerColecciones() {
    const resp = await fetch(`${window.METAMAPA.API_COLECCIONES}`);
    return resp.ok ? resp.json() : [];
}

// Modificar consenso de una colección
async function modificarConsensoColeccion(id, consenso) {
    const resp = await fetch(`${window.METAMAPA.API_COLECCIONES}/${id}`, {
        method: "PATCH",
        headers: {"Content-Type": "application/json"},
        body: JSON.stringify({consenso})
    });
    return resp.ok;
}

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

// Pedir al agregador que actualice los hechos desde las fuentes
async function actualizarHechos() {
    const resp = await fetch(`${window.METAMAPA.API_AGREGADOR}/actualizarHechos`, {method: "POST"});
    alert(resp.ok ? "Hechos actualizados desde las fuentes." : "⚠️ Error al actualizar hechos.");
}

// Ejecutar curado/consenso de hechos en el agregador
async function curarHechos() {
    const resp = await fetch(`${window.METAMAPA.API_AGREGADOR}/consensuarHechos`, {method: "POST"});
    alert(resp.ok ? "Curado completado correctamente." : "⚠️ Error al curar hechos.");
}

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