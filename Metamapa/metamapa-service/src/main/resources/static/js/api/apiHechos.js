// Crear un nuevo hecho en la fuente dinámica
async function crearHecho(e) {
    e.preventDefault();
    const f = e.target;
    const btn = document.getElementById("btnGuardar");
    const texto = document.getElementById("btnGuardarTexto");
    const loader = document.getElementById("btnGuardarLoader");
    const res = document.getElementById("resultadoHecho");
    // === Mostrar loader y desactivar botón ===
    btn.disabled = true;
    loader.classList.remove("d-none");
    texto.textContent = "Guardando...";
    res.textContent = ""; // limpiar mensaje anterior
    try {
        // === Armar objeto JSON idéntico al usado en Postman ===
        const data = {
            titulo: f.titulo.value.trim(),
            descripcion: f.descripcion.value.trim(),
            categoria: f.categoria.value.trim(),
            latitud: parseFloat(f.latitud.value),
            longitud: parseFloat(f.longitud.value),
            fechaHecho: f.fechaHecho?.value || new Date().toISOString().slice(0, 16),
            idUsuario: parseInt(f.idUsuario.value), //TODO: obtener del login
            fuenteId: parseInt(f.idFuente.value),
            anonimo: f.anonimo.checked
        };
        console.log("Enviando hecho:", data);
        // === Validar campos obligatorios ===
        if (!data.titulo || !data.descripcion || isNaN(data.fuenteId)) {
            alert("Debes completar al menos título, descripción y fuente.");
            return;
        }
        // === Crear FormData ===
        const formData = new FormData();
        formData.append("hecho", JSON.stringify(data));
        // Agregar archivos seleccionados
        const input = document.getElementById("inputMultimedia");
        for (let i = 0; i < input.files.length; i++) {
            formData.append("archivos", input.files[i]);
        }
        // === Enviar al backend ===
        const resp = await fetch(`${window.METAMAPA.API_FUENTE_DINAMICA}/${data.fuenteId}/hechos`,
            { method: "POST", body: formData }
        );
        if (resp.ok) {
            const json = await resp.json();
            res.innerHTML = `✅ Hecho creado correctamente (ID: ${json.id || "sin id"})`;
            res.className = "text-success";
            // Cerrar modal
            const modal = bootstrap.Modal.getInstance(document.getElementById("modalHecho"));
            modal.hide();
            limpiarFormularioHecho();
            mostrarModal(`✅ Hecho creado correctamente (ID: ${json.id || "sin id"})`, "Hecho creado");
        } else {
            const errorTxt = await resp.text();
            res.innerHTML = `❌ Error al crear el hecho: ${errorTxt}`;
            res.className = "text-danger";
        }
    } catch (error) {
        console.error("Error al crear hecho:", error);
        res.innerHTML = "❌ Error inesperado al crear el hecho.";
        res.className = "text-danger";
    } finally {
        // === Restaurar botón ===
        btn.disabled = false;
        loader.classList.add("d-none");
        texto.textContent = "Guardar";
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

// Pedir al agregador que actualice los hechos desde las fuentes
async function actualizarHechos() {
    try {
        const resp = await fetch(`${window.METAMAPA.API_AGREGADOR}/actualizarHechos`, { method: "POST" });
        if (resp.ok) {
            mostrarModal("Hechos actualizados desde las fuentes.", "✅ Actualización completa", true);
        } else {
            const errorTxt = await resp.text();
            mostrarModal(`⚠️ Error al actualizar hechos: ${errorTxt}`, "Error");
        }
    } catch (err) {
        mostrarModal(`⚠️ Error al actualizar hechos: ${err.message}`, "Error");
    }
}

// Ejecutar curado/consenso de hechos en el agregador
async function curarHechos() {
    const resp = await fetch(`${window.METAMAPA.API_AGREGADOR}/consensuarHechos`, {method: "POST"});
    alert(resp.ok ? "Curado completado correctamente." : "⚠️ Error al curar hechos.");
}