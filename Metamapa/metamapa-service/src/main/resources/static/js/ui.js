console.log("ui.js cargado correctamente");
const cont = document.getElementById("contenido");

const vistas = {
    hechos: mostrarHechosView,
    colecciones: mostrarColeccionesView,
    fuentes: mostrarFuentesView,
    solicitudes: mostrarSolicitudesView,
    estadisticas: mostrarEstadisticasView
};

async function mostrar(seccion) {
    cont.innerHTML = "";
    const fn = vistas[seccion];
    if (fn) await fn();
}

function renderTablaSolicitudes(titulo, solicitudes, columnas, onApprove, onReject) {
    if (!solicitudes.length) {
        const alerta = document.createElement("div");
        alerta.className = "alert alert-info";
        alerta.textContent = `No hay solicitudes de ${titulo.toLowerCase()} pendientes.`;
        return alerta;
    }

    const tableWrapper = document.createElement("div");
    tableWrapper.innerHTML = `
        <h3>Solicitudes de ${titulo} (${solicitudes.length})</h3>
        <div class="table-responsive mb-4">
          <table class="table table-striped table-hover align-middle">
            <thead>
              <tr>${columnas.map(c => `<th>${c.label}</th>`).join("")}<th>Acciones</th></tr>
            </thead>
            <tbody>
              ${solicitudes.map(s => `
                <tr data-id="${s.id}" data-estado="${s.estado}">
                  ${columnas.map(c => `<td>${s[c.key] ?? "-"}</td>`).join("")}
                  <td>
                    <button class="btn btn-sm btn-success me-1 aprobar">Aprobar</button>
                    <button class="btn btn-sm btn-danger rechazar">Rechazar</button>
                  </td>
                </tr>`).join("")}
            </tbody>
          </table>
        </div>
    `;

    // Delegación de eventos: un listener para todo el wrapper
    tableWrapper.addEventListener("click", (ev) => {
        const btn = ev.target.closest(".aprobar, .rechazar");
        if (!btn) return;
        const tr = btn.closest("tr");
        const id = tr?.dataset.id;
        const estado = tr?.dataset.estado;
        if (estado !== "PENDIENTE") {
            mostrarModal("Esta solicitud ya fue procesada y no se puede modificar.");
            return;
        }
        if (btn.classList.contains("aprobar")) onApprove(id);
        else if (btn.classList.contains("rechazar")) onReject(id);
    });
    return tableWrapper;
}

async function mostrarEstadisticasView() {
    cont.innerHTML = `
        <h2>📊 Estadísticas del sistema</h2>
        <div id="estadisticas-container" class="estadisticas">
            
            <div class="stat">
                <h3>📁 Provincia con más hechos por Colección</h3>
                <input type="text" id="coleccionInput" placeholder="Ingrese UUID de colección..." />
                <button id="btnBuscarProvinciaColeccion">Buscar</button>
                <p id="provinciaColeccion">—</p>
            </div>

            <div class="stat">
                <h3>🏷️ Categoría más reportada</h3>
                <p id="categoriaMasReportada">Cargando...</p>
            </div>

            <div class="stat">
                <h3>🌎 Provincia con más hechos de una categoría</h3>
                <input type="text" id="categoriaInput" placeholder="Ingrese una categoría..." />
                <button id="btnBuscarProvinciaCat">Buscar</button>
                <p id="provinciaCategoria">—</p>
            </div>

            <div class="stat">
                <h3>🕓 Hora del día con más hechos (por categoría)</h3>
                <input type="text" id="categoriaHoraInput" placeholder="Ingrese una categoría..." />
                <button id="btnBuscarHoraCat">Buscar</button>
                <p id="horaCategoria">—</p>
            </div>

            <div class="stat">
                <h3>🚫 Solicitudes de eliminación marcadas como spam</h3>
                <p id="cantidadSpam">Cargando...</p>
            </div>

            <hr>
            <div class="text-end mt-4">
                <button id="btnExportarCSV" class="btn btn-success">⬇️ Exportar CSV</button>
            </div>
        </div>
    `;

    // 🔹 Llamados iniciales (estadísticas generales)
    const categoriaMasReportada = await obtenerCategoriaMasReportada();
    document.getElementById("categoriaMasReportada").textContent =
        categoriaMasReportada || "No hay datos";

    const cantidadSpam = await obtenerCantidadSolicitudesSpam();
    document.getElementById("cantidadSpam").textContent = cantidadSpam;

    // 🔹 Eventos dinámicos
    document.getElementById("btnBuscarProvinciaColeccion").addEventListener("click", async () => {
        const uuid = document.getElementById("coleccionInput").value.trim();
        if (!uuid) return alert("Ingrese un UUID de colección");
        const provincia = await obtenerProvinciaMasReportadaColeccion(uuid);
        document.getElementById("provinciaColeccion").textContent =
            provincia || "No hay datos disponibles";
    });

    document.getElementById("btnBuscarProvinciaCat").addEventListener("click", async () => {
        const cat = document.getElementById("categoriaInput").value.trim();
        if (!cat) return alert("Ingrese una categoría");
        const prov = await obtenerProvinciaMasReportadaPorCategoria(cat);
        document.getElementById("provinciaCategoria").textContent =
            prov || "No hay datos disponibles";
    });

    document.getElementById("btnBuscarHoraCat").addEventListener("click", async () => {
        const cat = document.getElementById("categoriaHoraInput").value.trim();
        if (!cat) return alert("Ingrese una categoría");
        const hora = await obtenerHoraMasReportadaPorCategoria(cat);
        document.getElementById("horaCategoria").textContent =
            hora !== null ? `${hora}:00 hs` : "No hay datos disponibles";
    });

    // 🔹 Botón Exportar CSV
    document.getElementById("btnExportarCSV").addEventListener("click", () => {
        // Recolectar datos visibles
        const datos = [
            ["Estadistica", "Valor"],
            ["Provincia con mas hechos por Coleccion", document.getElementById("provinciaColeccion").textContent.trim()],
            ["Categoria más reportada", document.getElementById("categoriaMasReportada").textContent.trim()],
            ["Provincia con mas hechos de una categoria", document.getElementById("provinciaCategoria").textContent.trim()],
            ["Hora del dia con más hechos (por categoria)", document.getElementById("horaCategoria").textContent.trim()],
            ["Solicitudes de eliminacion marcadas como spam", document.getElementById("cantidadSpam").textContent.trim()]
        ];

        // Convertir a CSV
// Usar punto y coma para compatibilidad regional con Excel
        const csv = datos.map(fila => fila.map(v => `"${v.replace(/"/g, '""')}"`).join(";")).join("\r\n");
        // Crear blob y disparar descarga
        const blob = new Blob([csv], { type: "text/csv;charset=utf-8;" });
        const url = URL.createObjectURL(blob);
        const a = document.createElement("a");
        a.href = url;
        a.download = `estadisticas_${new Date().toISOString().split("T")[0]}.csv`;
        document.body.appendChild(a);
        a.click();
        document.body.removeChild(a);
        URL.revokeObjectURL(url);
    });
}

async function mostrarSolicitudesView() {
    cont.innerHTML = "<p>Cargando solicitudes...</p>";
    const [elim, edic] = await Promise.all([
        obtenerSolicitudesEliminacion(),
        obtenerSolicitudesEdicion()
    ]);
    cont.innerHTML = "";
    cont.appendChild(renderTablaSolicitudes(
        "Eliminación",
        elim,
        [
            {key: "id", label: "ID"},
            {key: "hechoAfectado", label: "Hecho afectado"},
            {key: "motivo", label: "Motivo"},
            {key: "estado", label: "Estado"}
        ],
        procesarSolicitudEliminacion.bind(null, true),
        procesarSolicitudEliminacion.bind(null, false)
    ));
    cont.appendChild(renderTablaSolicitudes(
        "Edición",
        edic,
        [
            {key: "id", label: "ID"},
            {key: "hechoAfectado", label: "Hecho afectado"},
            {key: "tituloMod", label: "Título"},
            {key: "descMod", label: "Descripción"},
            {key: "categoriaMod", label: "Categoría"},
            {key: "latitudMod", label: "Latitud"},
            {key: "longitudMod", label: "Longitud"},
            {key: "fechaHechoMod", label: "Fecha"},
            {key: "sugerencia", label: "Sugerencia"},
            {key: "estado", label: "Estado"}
        ],
        procesarSolicitudEdicion.bind(null, true),
        procesarSolicitudEdicion.bind(null, false)
    ));
}

async function mostrarHechosView() {
    cont.innerHTML = `
      <h3>Hechos curados</h3>
      <div id="panelFiltrosHechos" class="border p-3 rounded bg-light mb-3">
        <div class="d-flex justify-content-between align-items-center mb-2">
          <h6 class="mb-0">Filtros dinámicos</h6>
          <button class="btn btn-sm btn-outline-secondary" onclick="agregarFiltro('panelFiltrosHechos')">+ Agregar filtro</button>
        </div>
        <div id="filtrosContainerHechos"></div>
        <button class="btn btn-sm btn-success mt-2" onclick="aplicarFiltrosHechos()">Aplicar filtros</button>
      </div>
      <div id="mapa" class="mapa"></div>
      <div id="tablaHechos" class="mt-3"></div>
    `;
    const hechos = await obtenerHechos();
    await ensureMapaInit("mapa");
    mostrarHechosEnMapa(hechos);
    document.getElementById("tablaHechos").innerHTML = renderTablaHechos("Hechos curados", hechos);
}

async function mostrarColeccionesView() {
    cont.innerHTML = `
      <div id="coleccionesView">
        <div class="d-flex justify-content-between align-items-center mb-3">
          <h4>Colecciones</h4>
          <button class="btn btn-primary" data-bs-toggle="modal" data-bs-target="#modalColeccion">+ Nueva Colección</button>
        </div>
        <div class="mb-3">
          <label for="modoNav" class="form-label">Modo de navegación:</label>
          <select id="modoNav" class="form-select form-select-sm" style="width:auto; display:inline-block;">
            <option value="IRRESTRICTA">Irrestricta</option>
            <option value="CURADA">Curada</option>
          </select>
        </div>
        <div id="panelFiltrosColeccion" class="border p-3 rounded bg-light mb-3">
          <div class="d-flex justify-content-between align-items-center mb-2">
            <h6 class="mb-0">Filtros dinámicos</h6>
            <button class="btn btn-sm btn-outline-secondary" onclick="agregarFiltro('panelFiltrosColeccion')">+ Agregar filtro</button>
          </div>
          <div id="filtrosContainerColeccion"></div>
          <button class="btn btn-sm btn-success mt-2" onclick="aplicarFiltrosColeccion()">Aplicar filtros</button>
        </div>
        <div id="listaColecciones" class="mb-3"></div>
        <div id="mapaColeccion" class="mapa"></div>
      </div>
    `;
    await ensureMapaInit("mapaColeccion");
    await mostrarColecciones();
}

async function mostrarFuentesView() {
    cont.innerHTML = "<p>Cargando fuentes...</p>";
    const fuentes = await obtenerFuentes();
    const lista = Object.entries(fuentes || {})
        .map(([url, tipo]) => `
            <li class="list-group-item d-flex justify-content-between align-items-start">
                <div>
                    <strong>${tipo}</strong><br>
                    <a href="${url}" target="_blank">${url}</a>
                </div>
                ${tipo === "Fuente Estatica" ? `
                    <button class="btn btn-sm btn-primary ms-3" onclick="cargarCSV('${url}')">
                        Cargar CSV
                    </button>
                ` : ""}
            </li>
        `)
        .join("");
    cont.innerHTML = `
        <h3>Fuentes registradas (${Object.keys(fuentes || {}).length})</h3>
        <ul class="list-group">${lista}</ul>
    `;
}

// Mostrar detalle
function mostrarDetalleHecho(h) {
    const modalEl = document.getElementById("modalDetalle");
    const modal = new bootstrap.Modal(modalEl);
    const contenedor = document.getElementById("detalleHecho");
    // Función para renderizar el contenido principal del hecho
    function renderDetalle() {
        contenedor.innerHTML = `
        <div class="container-fluid">
          <h4 class="mb-3">${h.titulo}</h4>
          <div class="row">
            <div class="col-md-6">
              <p><b>Descripción:</b> ${h.descripcion || "-"}</p>
              <p><b>Categoría:</b> ${h.categoria || "-"}</p>
              <p><b>Anonimo:</b> ${h.anonimo ? "Sí" : "No"}</p>
              <p><b>Eliminado:</b> ${h.eliminado ? "Sí" : "No"}</p>
            </div>
            <div class="col-md-6">
              <p><b>Latitud:</b> ${h.latitud ?? "-"}</p>
              <p><b>Longitud:</b> ${h.longitud ?? "-"}</p>
              <p><b>ID Fuente:</b> ${h.idFuente ?? "-"}</p>
              <p><b>ID:</b> ${h.id}</p>
            </div>
          </div>
          <hr>
          <h6>Fechas</h6>
          <p><b>Fecha del hecho:</b> ${h.fechaHecho || "-"}</p>
          <p><b>Fecha de carga:</b> ${h.fechaCarga || "-"}</p>
          <p><b>Fecha de modificación:</b> ${h.fechaModificacion || "-"}</p>
          <hr>
          <h6>Datos internos</h6>
          <p><b>Perfil:</b> ${h.perfil || "<i>Sin perfil</i>"}</p>
          <p><b>Consensos:</b> ${h.consensos && h.consensos.length ? h.consensos.join(", ") : "<i>Ninguno</i>"}</p>
          <pre class="bg-light p-2 rounded"><b>Metadata:</b>\n${JSON.stringify(h.metadata || {}, null, 2)}</pre>
          <hr>
          <h6>Multimedia</h6>
          ${!h.multimedia?.length
            ? "<p><i>Sin archivos multimedia</i></p>"
            : h.multimedia.map(m =>
                `<div class="mb-2">
                  <b>${m.tipoMultimedia || "Archivo"}</b>: 
                  <a href="${m.path}" target="_blank">${m.path}</a>
                </div>`
            ).join("")}
          <hr>
          <div class="d-flex justify-content-end gap-2 mt-3">
            <button id="btnSolicitarEdicion" class="btn btn-warning">Solicitar edición</button>
            <button id="btnSolicitarEliminacion" class="btn btn-danger">Solicitar eliminación</button>
          </div>
        </div>
        `;

        // Eventos de los botones
        document.getElementById("btnSolicitarEliminacion").addEventListener("click", mostrarFormularioEliminacion);
        document.getElementById("btnSolicitarEdicion").addEventListener("click", mostrarFormularioEdicion);
    }

    // --- FORMULARIO ELIMINACIÓN ---
    function mostrarFormularioEliminacion() {
        contenedor.innerHTML = `
        <div class="container-fluid">
          <h5 class="text-danger mb-3">Solicitud de eliminación del hecho #${h.id}</h5>
          <div class="mb-3">
            <label class="form-label"><b>Motivo de la solicitud</b></label>
            <textarea id="motivoEliminacion" class="form-control" rows="3" placeholder="Explique brevemente el motivo..."></textarea>
          </div>
          <div class="d-flex justify-content-end gap-2">
            <button id="btnCancelar" class="btn btn-secondary">Cancelar</button>
            <button id="btnEnviarEliminacion" class="btn btn-danger">Enviar solicitud</button>
          </div>
        </div>
        `;
        document.getElementById("btnCancelar").addEventListener("click", renderDetalle);
        document.getElementById("btnEnviarEliminacion").addEventListener("click", async () => {
            const motivo = document.getElementById("motivoEliminacion").value.trim();
            if (!motivo) return alert("Debe ingresar un motivo.");
            const solicitud = {
                motivo,
                hechoAfectado: h.id
            };
            const ok = await enviarSolicitudEliminacion(solicitud);
            alert(ok ? "✅ Solicitud de eliminación enviada con éxito." : "❌ Error al enviar la solicitud.");
            renderDetalle();
        });
    }
    // --- FORMULARIO EDICIÓN ---
    function mostrarFormularioEdicion() {
        contenedor.innerHTML = `
        <div class="container-fluid">
          <h5 class="text-warning mb-3">Solicitud de edición del hecho #${h.id}</h5>
          <div class="mb-2">
            <label class="form-label"><b>Título</b></label>
            <input id="tituloMod" class="form-control" value="${h.titulo || ''}">
          </div>
          <div class="mb-2">
            <label class="form-label"><b>Descripción</b></label>
            <textarea id="descMod" class="form-control" rows="3">${h.descripcion || ''}</textarea>
          </div>
          <div class="mb-2">
            <label class="form-label"><b>Categoría</b></label>
            <input id="categoriaMod" class="form-control" value="${h.categoria || ''}">
          </div>
          <div class="row">
            <div class="col-md-6 mb-2">
              <label class="form-label"><b>Latitud</b></label>
              <input id="latitudMod" type="number" step="any" class="form-control" value="${h.latitud ?? ''}">
            </div>
            <div class="col-md-6 mb-2">
              <label class="form-label"><b>Longitud</b></label>
              <input id="longitudMod" type="number" step="any" class="form-control" value="${h.longitud ?? ''}">
            </div>
          </div>
          <div class="mb-2">
            <label class="form-label"><b>Fecha del hecho</b></label>
            <input id="fechaHechoMod" type="datetime-local" class="form-control" value="${h.fechaHecho ? h.fechaHecho.substring(0,16) : ''}">
          </div>
          <div class="mb-2">
            <label class="form-label"><b>Sugerencia adicional</b></label>
            <textarea id="sugerencia" class="form-control" rows="2" placeholder="Opcional"></textarea>
          </div>
          <div class="d-flex justify-content-end gap-2">
            <button id="btnCancelar" class="btn btn-secondary">Cancelar</button>
            <button id="btnEnviarEdicion" class="btn btn-warning">Enviar solicitud</button>
          </div>
        </div>
        `;
        document.getElementById("btnCancelar").addEventListener("click", renderDetalle);
        document.getElementById("btnEnviarEdicion").addEventListener("click", async () => {
            const solicitud = {
                tituloMod: document.getElementById("tituloMod").value.trim(),
                descMod: document.getElementById("descMod").value.trim(),
                categoriaMod: document.getElementById("categoriaMod").value.trim(),
                latitudMod: parseFloat(document.getElementById("latitudMod").value) || null,
                longitudMod: parseFloat(document.getElementById("longitudMod").value) || null,
                fechaHechoMod: document.getElementById("fechaHechoMod").value || null,
                multimediaMod: h.multimedia || [],
                sugerencia: document.getElementById("sugerencia").value.trim(),
                hechoAfectado: h.id
            };
            const ok = await enviarSolicitudEdicion(solicitud);
            alert(ok ? "✅ Solicitud de edición enviada con éxito." : "❌ Error al enviar la solicitud.");
            renderDetalle();
        });
    }
    // Render inicial
    renderDetalle();
    modal.show();
}

// Render tabla
function renderTablaHechos(titulo, hechos) {
    if (!hechos.length) return `<div class="alert alert-info">No hay hechos disponibles.</div>`;
    const filas = hechos.map(h => `
        <tr>
            <td>${h.titulo}</td>
            <td><span class="badge" style="background:${colorPorCategoria(h.categoria)}">${h.categoria || "-"}</span></td>
            <td>${h.idFuente ?? "-"}</td>
            <td>${h.id ?? "-"}</td>
            <td>${h.fechaHecho || "-"}</td>
            <td><button class="btn btn-sm btn-outline-secondary" onclick='mostrarDetalleHecho(${JSON.stringify(h)})'>Ver</button></td>
        </tr>
    `).join("");
    return `
        <h4>${titulo} (${hechos.length})</h4>
        <table class="table table-striped table-sm">
            <thead><tr><th>Título</th><th>Categoría</th><th>Fuente</th><th>id</th><th>Fecha</th><th></th></tr></thead>
            <tbody>${filas}</tbody>
        </table>`;
}

// Formulario Hecho
function agregarMultimedia() {
    const cont = document.getElementById("multimediaContainer");
    const row = document.createElement("div");
    row.className = "row mb-2";
    row.innerHTML = `
    <div class="col">
      <input type="text" name="tipoMultimedia" placeholder="FOTO o VIDEO" class="form-control">
    </div>
    <div class="col">
      <input type="url" name="path" placeholder="https://..." class="form-control">
    </div>`;
    cont.appendChild(row);
}

function agregarCriterio(criterioExistente = null) {
    const container = document.getElementById("criteriosContainer");
    const div = document.createElement("div");
    div.className = "criterio-box p-2 border rounded mb-2";

    div.innerHTML = `
    <div class="row mb-2">
      <div class="col-md-4">
        <label class="form-label">Tipo</label>
        <select name="tipo" class="form-select tipo-criterio">
          <option value="titulo">Título</option>
          <option value="descripcion">Descripción</option>
          <option value="categoria">Categoría</option>
          <option value="fecha">Fecha</option>
          <option value="fechareportaje">Fecha Reportaje</option>
          <option value="fuente">Fuente</option>
          <option value="ubicacion">Ubicación</option>
          <option value="multimedia">Multimedia</option>
        </select>
      </div>
      <div class="col-md-4">
        <label class="form-label">Valor</label>
        <input type="text" name="valor" class="form-control" placeholder="Valor o texto">
      </div>
      <div class="col-md-3">
        <label class="form-label">Incluir</label>
        <select name="inclusion" class="form-select">
          <option value="true" selected>Incluir</option>
          <option value="false">Excluir</option>
        </select>
      </div>
      <div class="col-md-1 d-flex align-items-end">
        <button type="button" class="btn btn-outline-danger btn-sm" onclick="this.closest('.criterio-box').remove()">✕</button>
      </div>
    </div>

    <!-- Campos específicos por tipo -->
    <div class="row mb-2 campos-fecha d-none">
      <div class="col">
        <label>Desde</label>
        <input type="date" name="fechaDesde" class="form-control">
      </div>
      <div class="col">
        <label>Hasta</label>
        <input type="date" name="fechaHasta" class="form-control">
      </div>
    </div>

    <div class="row mb-2 campos-fuente d-none">
      <div class="col">
        <label>ID Fuente</label>
        <input type="number" name="idFuenteDeDatos" class="form-control" placeholder="1">
      </div>
    </div>

    <div class="row mb-2 campos-ubicacion d-none">
      <div class="col">
        <label>Latitud</label>
        <input type="number" step="any" name="latitud" class="form-control" readonly>
      </div>
      <div class="col">
        <label>Longitud</label>
        <input type="number" step="any" name="longitud" class="form-control" readonly>
      </div>
      <div class="col">
        <label>Radio (km)</label>
        <input type="number" step="0.1" name="radio" class="form-control" readonly>
      </div>
      <div class="col d-flex align-items-end">
        <button type="button" class="btn btn-outline-success w-100" onclick="abrirMapaUbicacion(this)">Seleccionar en mapa</button>
      </div>
    </div>

    <div class="row mb-2 campos-multimedia d-none">
      <div class="col">
        <label>Tipo de Multimedia</label>
        <select name="tipoMultimedia" class="form-select">
          <option value="FOTO">FOTO</option>
          <option value="VIDEO">VIDEO</option>
          <option value="AUDIO">AUDIO</option>
        </select>
      </div>
    </div>
  `;

    const tipoSelect = div.querySelector(".tipo-criterio");
    tipoSelect.addEventListener("change", () => actualizarCamposCriterio(div, tipoSelect.value));

    // Si es un criterio cargado desde una colección existente
    if (criterioExistente) {
        tipoSelect.value = criterioExistente.tipo;
        div.querySelector('[name="valor"]').value = criterioExistente.valor || "";
        div.querySelector('[name="inclusion"]').value = criterioExistente.inclusion ? "true" : "false";
        if (criterioExistente.fechaDesde) div.querySelector('[name="fechaDesde"]').value = criterioExistente.fechaDesde;
        if (criterioExistente.fechaHasta) div.querySelector('[name="fechaHasta"]').value = criterioExistente.fechaHasta;
        if (criterioExistente.idFuenteDeDatos) div.querySelector('[name="idFuenteDeDatos"]').value = criterioExistente.idFuenteDeDatos;
        if (criterioExistente.latitud) div.querySelector('[name="latitud"]').value = criterioExistente.latitud;
        if (criterioExistente.longitud) div.querySelector('[name="longitud"]').value = criterioExistente.longitud;
        if (criterioExistente.tipoMultimedia) div.querySelector('[name="tipoMultimedia"]').value = criterioExistente.tipoMultimedia;
        actualizarCamposCriterio(div, criterioExistente.tipo);
    }
    container.appendChild(div);
}

function actualizarCamposCriterio(div, tipo) {
    div.querySelectorAll(".campos-fecha, .campos-fuente, .campos-ubicacion, .campos-multimedia")
        .forEach(el => el.classList.add("d-none"));

    if (tipo === "fecha" || tipo === "fechareportaje")
        div.querySelector(".campos-fecha").classList.remove("d-none");
    if (tipo === "fuente")
        div.querySelector(".campos-fuente").classList.remove("d-none");
    if (tipo === "ubicacion")
        div.querySelector(".campos-ubicacion").classList.remove("d-none");
    if (tipo === "multimedia")
        div.querySelector(".campos-multimedia").classList.remove("d-none");
}


document.addEventListener("DOMContentLoaded", () => {
    const formHecho = document.getElementById("formHecho");
    const formColeccion = document.getElementById("formColeccion");
    const modalHecho = document.getElementById("modalHecho");

    // Enlazar formularios
    if (formHecho) formHecho.addEventListener("submit", crearHecho);
    if (formColeccion) formColeccion.addEventListener("submit", crearColeccion);

    // Iniciar mapa cuando se abre el modal
    modalHecho.addEventListener("shown.bs.modal", () => {
        setTimeout(inicializarMapaSeleccion, 300);
    });

    // Limpiar marcador y campos cuando se cierra el modal
    modalHecho.addEventListener("hidden.bs.modal", () => {
        limpiarMapaSeleccion();
        limpiarFormularioHecho();
    });
});

function limpiarFormularioHecho() {
    const form = document.getElementById("formHecho");
    if (!form) return;

    form.reset(); // limpia los inputs normales

    // limpia campos manuales de lat/long
    document.getElementById("latitud").value = "";
    document.getElementById("longitud").value = "";

    // limpiar contenedor de multimedia
    const cont = document.getElementById("multimediaContainer");
    if (cont) cont.innerHTML = "";

    // limpiar resultado de estado
    const res = document.getElementById("resultadoHecho");
    if (res) res.innerHTML = "";
}
document.addEventListener("DOMContentLoaded", () => {
    const modalColeccion = document.getElementById("modalColeccion");
    if (modalColeccion) {
        modalColeccion.addEventListener("hidden.bs.modal", limpiarFormularioColeccion);
    }
});

function limpiarFormularioColeccion() {
    const form = document.getElementById("formColeccion");
    if (!form) return;
    form.reset();
    document.getElementById("criteriosContainer").innerHTML = "";
    document.getElementById("modalColeccionTitle").innerText = "Nueva Colección";
    const res = document.getElementById("resultadoColeccion");
    if (res) res.innerHTML = "";
}

// ==========================
// Inicialización al cargar
// ==========================
document.addEventListener("DOMContentLoaded", async () => {
    console.log("Iniciando MetaMapa...");
    await mostrar("hechos");
});

// ==================================================
// Gestión dinámica de categorías
// ==================================================
let categoriasDisponibles = new Set();

// Obtener categorías únicas desde los hechos actuales
async function cargarCategoriasExistentes() {
    try {
        const guardadas = JSON.parse(localStorage.getItem("categoriasMetaMapa") || "[]");
        guardadas.forEach(c => categoriasDisponibles.add(c));

        const hechos = await obtenerHechos();
        categoriasDisponibles.clear();
        hechos.forEach(h => {
            if (h.categoria) categoriasDisponibles.add(h.categoria);
        });

        const select = document.getElementById("categoriaSelect");
        select.innerHTML = "";
        categoriasDisponibles.forEach(cat => {
            const opt = document.createElement("option");
            opt.value = cat;
            opt.textContent = cat;
            select.appendChild(opt);
        });

        // Si no hay categorías, agregamos una por defecto
        if (categoriasDisponibles.size === 0) {
            const opt = document.createElement("option");
            opt.value = "";
            opt.textContent = "— Sin categorías cargadas —";
            opt.disabled = true;
            opt.selected = true;
            select.appendChild(opt);
        }
    } catch (e) {
        console.error("Error al cargar categorías:", e);
    }
}

// Abrir modal para agregar nueva
function agregarNuevaCategoria() {
    document.getElementById("nuevaCategoriaInput").value = "";
    const modal = new bootstrap.Modal(document.getElementById("modalCategoria"));
    modal.show();
}

// Guardar nueva categoría
function guardarNuevaCategoria() {
    const input = document.getElementById("nuevaCategoriaInput");
    const nueva = input.value.trim();
    if (!nueva) return alert("Debe escribir una categoría válida.");

    categoriasDisponibles.add(nueva);

    const select = document.getElementById("categoriaSelect");
    const opt = document.createElement("option");
    opt.value = nueva;
    opt.textContent = nueva;
    select.appendChild(opt);
    select.value = nueva; // seleccionarla automáticamente

    localStorage.setItem("categoriasMetaMapa", JSON.stringify([...categoriasDisponibles]));

    bootstrap.Modal.getInstance(document.getElementById("modalCategoria")).hide();
}
let coleccionSeleccionada = null;

// Cargar todas las colecciones
async function mostrarColecciones() {
    const cont = document.getElementById("listaColecciones");
    cont.innerHTML = "<p class='text-muted'>Cargando colecciones...</p>";
    try {
        const colecciones = await obtenerColecciones();
        cont.innerHTML = colecciones.map(c => `
            <div class="card mb-2 p-2">
                <div class="d-flex justify-content-between align-items-center">
                    <div>
                        <h6 class="fw-bold mb-1">${c.titulo}</h6>
                        <p class="small mb-0">${c.descripcion}</p>
                        <p class="small mb-0"><b>Consenso:</b> ${c.consenso}</p>
                    </div>
                    <div class="btn-group">
                        <button class="btn btn-sm btn-outline-primary" onclick="verHechosColeccion('${c.handle}')">Ver hechos</button>
                        <button class="btn btn-sm btn-outline-primary" onclick="cambiarConsenso('${c.handle}')">Cambiar consenso</button>
                    </div>
                </div>
            </div>
        `).join("");
    } catch (e) {
        cont.innerHTML = `<div class="alert alert-danger">Error al cargar colecciones</div>`;
        console.error("Error al cargar colecciones:", e);
    }
}

async function cambiarConsenso(id) {
    // Crear un modal sencillo con un selector
    const opciones = [
        { label: "Mayoría simple", value: "MayoriaSimple" },
        { label: "Absoluto", value: "Absoluto" },
        { label: "Múltiples menciones", value: "MultiplesMenciones" }
    ];
    // Crear contenedor temporal del selector
    const div = document.createElement("div");
    div.innerHTML = `
        <div class="p-3 bg-white rounded shadow-sm" style="max-width: 320px; margin: 40px auto;">
            <h6 class="mb-3 text-center">Seleccioná un nuevo consenso</h6>
            <select id="selectorConsenso" class="form-select mb-3">
                ${opciones.map(o => `<option value="${o.value}">${o.label}</option>`).join("")}
            </select>
            <div class="d-flex justify-content-between">
                <button class="btn btn-secondary btn-sm" id="cancelarCambio">Cancelar</button>
                <button class="btn btn-primary btn-sm" id="confirmarCambio">Guardar</button>
            </div>
        </div>
    `;
    // Mostrarlo como modal flotante
    const overlay = document.createElement("div");
    overlay.style.cssText = `
        position: fixed; top: 0; left: 0; width: 100%; height: 100%;
        background: rgba(0,0,0,0.4); display: flex; justify-content: center; align-items: center;
        z-index: 2000;
    `;
    overlay.appendChild(div);
    document.body.appendChild(overlay);
    // Manejo de eventos
    document.getElementById("cancelarCambio").onclick = () => overlay.remove();
    document.getElementById("confirmarCambio").onclick = async () => {
        const nuevoConsenso = document.getElementById("selectorConsenso").value;
        overlay.remove();
        try {
            const ok = await modificarConsensoColeccion(id, nuevoConsenso);
            if (ok) {
                alert("✅ Consenso actualizado correctamente");
                await mostrarColecciones();
            } else {
                alert("❌ Error al cambiar el consenso en el servidor");
            }
        } catch (err) {
            console.error(err);
            alert("❌ Error de red al intentar cambiar el consenso");
        }
    };
}

// Ver hechos de una colección seleccionada
async function verHechosColeccion(idColeccion) {
    coleccionSeleccionada = idColeccion;
    const modo = document.getElementById("modoNav").value;
    const url = `${window.METAMAPA.API_COLECCIONES}/${idColeccion}/hechos?modoNavegacion=${modo}`;
    console.log("📡 Cargando hechos:", url);
    try {
        const resp = await fetch(url);
        if (!resp.ok) throw new Error("Respuesta no OK del servidor");
        const hechos = await resp.json();

        // Mostrar en mapa
        setTimeout(() => inicializarMapa("mapaColeccion"), 100);
        setTimeout(() => mostrarHechosEnMapa(hechos), 300);
    } catch (e) {
        alert("Error al obtener hechos de la colección");
        console.error(e);
    }
}

// Aplicar filtros temporales sin guardar en BD
async function aplicarFiltrosColeccion() {
    if (!coleccionSeleccionada) return alert("Seleccioná una colección primero.");

    const modo = document.getElementById("modoNav").value;
    const params = construirParametrosFiltros("panelFiltrosColeccion");

    params.append("modoNavegacion", modo);

    const url = `${window.METAMAPA.API_COLECCIONES}/${coleccionSeleccionada}/hechos?${params.toString()}`;
    console.log("📡 Aplicando filtros:", url);

    try {
        const resp = await fetch(url);
        if (!resp.ok) throw new Error("Respuesta no OK del servidor");
        const hechos = await resp.json();

        inicializarMapa();
        mostrarHechosEnMapa(hechos);
        //document.getElementById("tablaHechos").innerHTML = renderTablaHechos("Hechos filtrados", hechos);
    } catch (e) {
        alert("Error al aplicar filtros");
        console.error(e);
    }
}
// ==================================================
// 🎛️ Gestión de filtros dinámicos
// ==================================================
function agregarFiltro(contexto) {
    const containerId = contexto === "panelFiltrosHechos" ? "filtrosContainerHechos" : "filtrosContainerColeccion";
    const container = document.getElementById(containerId);
    const div = document.createElement("div");
    div.className = "p-2 border rounded mb-2";

    div.innerHTML = `
    <div class="row g-2 align-items-end">
      <div class="col-md-3">
        <label class="form-label">Campo</label>
        <select class="form-select campoFiltro">
          <option value="titulo">Título</option>
          <option value="descripcion">Descripción</option>
          <option value="categoria">Categoría</option>
          <option value="fechaAcontecimientoDesde">Fecha desde</option>
          <option value="fechaAcontecimientoHasta">Fecha hasta</option>
          <option value="ubicacion">Ubicación</option>
          <option value="tipoMultimedia">Multimedia</option>
        </select>
      </div>

      <div class="col-md-4 valorFiltroCol">
        <label class="form-label">Valor</label>
        <input type="text" class="form-control valorFiltro" placeholder="Texto o número">
      </div>

      <div class="col-md-3">
        <label class="form-label">Tipo</label>
        <select class="form-select tipoFiltro">
          <option value="P">Incluir</option>
          <option value="NP">Excluir</option>
        </select>
      </div>

      <div class="col-md-2">
        <button class="btn btn-outline-danger btn-sm" onclick="this.closest('div.p-2').remove()">✕</button>
      </div>
    </div>

    <!-- Campos extra para ubicación -->
    <div class="row mt-2 g-2 d-none camposUbicacion">
      <div class="col-md-3"><input type="number" step="any" class="form-control latitud" placeholder="Latitud" readonly></div>
      <div class="col-md-3"><input type="number" step="any" class="form-control longitud" placeholder="Longitud" readonly></div>
      <div class="col-md-3"><input type="number" step="0.1" class="form-control radio" placeholder="Radio (km)" readonly></div>
      <div class="col-md-3"><button class="btn btn-sm btn-outline-success w-100" onclick="abrirMapaUbicacion(this)">Seleccionar</button></div>
    </div>
  `;

    const campo = div.querySelector(".campoFiltro");
    campo.addEventListener("change", () => {
        const camposUbicacion = div.querySelector(".camposUbicacion");
        const valorCol = div.querySelector(".valorFiltroCol");
        if (campo.value === "ubicacion") {
            camposUbicacion.classList.remove("d-none");
            valorCol.classList.add("d-none");
        } else {
            camposUbicacion.classList.add("d-none");
            valorCol.classList.remove("d-none");
        }
    });
    container.appendChild(div);
}

// Construir parámetros GET para filtros activos
function construirParametrosFiltros(contexto) {
    const containerId = contexto === "panelFiltrosHechos" ? "filtrosContainerHechos" : "filtrosContainerColeccion";
    const filtros = document.querySelectorAll(`#${containerId} > div`);
    const params = new URLSearchParams();

    filtros.forEach(f => {
        const campo = f.querySelector(".campoFiltro").value;
        const tipo = f.querySelector(".tipoFiltro").value;
        const valor = f.querySelector(".valorFiltro")?.value || null;
        const lat = f.querySelector(".latitud")?.value;
        const lon = f.querySelector(".longitud")?.value;
        const radio = f.querySelector(".radio")?.value;

        if (campo === "ubicacion" && lat && lon) {
            params.append(`latitud${tipo}`, lat);
            params.append(`longitud${tipo}`, lon);
            if (radio) params.append(`radio${tipo}`, radio);
        } else if (valor) {
            params.append(`${campo}${tipo}`, valor);
        }
    });
    return params;
}

// Aplicar filtros a hechos
async function aplicarFiltrosHechos() {
    const params = construirParametrosFiltros("panelFiltrosHechos");
    const url = `${window.METAMAPA.API_AGREGADOR}/hechos?${params.toString()}`;
    console.log("📡 Aplicando filtros hechos:", url);

    try {
        const resp = await fetch(url);
        if (!resp.ok) throw new Error("Error al aplicar filtros");
        const hechos = await resp.json();
        inicializarMapa();
        mostrarHechosEnMapa(hechos);
        document.getElementById("tablaHechos").innerHTML = renderTablaHechos("Hechos filtrados", hechos);
    } catch (e) {
        alert("Error al aplicar filtros");
        console.error(e);
    }
}
function mostrarAlerta(mensaje, tipo = "warning", duracion = 3000) {
    // Revisar si ya existe el contenedor, si no, crearlo
    let cont = document.getElementById("alert-container");
    if (!cont) {
        cont = document.createElement("div");
        cont.id = "alert-container";
        cont.style.position = "fixed";
        cont.style.top = "20px";
        cont.style.right = "20px";
        cont.style.zIndex = 1050;
        document.body.appendChild(cont);
    }

    const alerta = document.createElement("div");
    alerta.className = `alert alert-${tipo} alert-dismissible fade show`;
    alerta.role = "alert";
    alerta.style.minWidth = "250px"; // opcional para que no se vea muy estrecho
    alerta.innerHTML = `
        ${mensaje}
        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
    `;
    cont.appendChild(alerta);

    setTimeout(() => {
        alerta.classList.remove("show");
        alerta.classList.add("hide");
        setTimeout(() => alerta.remove(), 300);
    }, duracion);
}

function mostrarModal(mensaje, titulo = "Atención") {
    // Crear el modal
    const modal = document.createElement("div");
    modal.className = "modal fade";
    modal.tabIndex = -1;
    modal.innerHTML = `
    <div class="modal-dialog">
      <div class="modal-content">
        <div class="modal-header">
          <h5 class="modal-title">${titulo}</h5>
          <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Cerrar"></button>
        </div>
        <div class="modal-body">
          <p>${mensaje}</p>
        </div>
        <div class="modal-footer">
          <button type="button" class="btn btn-primary" data-bs-dismiss="modal">Cerrar</button>
        </div>
      </div>
    </div>
    `;
    document.body.appendChild(modal);

    // Inicializar el modal con Bootstrap
    const bootstrapModal = new bootstrap.Modal(modal);
    bootstrapModal.show();

    // Eliminar el modal del DOM cuando se cierre
    modal.addEventListener("hidden.bs.modal", () => modal.remove());
}
// helper que reemplaza setTimeouts dispersos para inicializar mapa
function ensureMapaInit(mapId = "mapa", delay = 100) {
    return new Promise((resolve) => {
        // si `inicializarMapa` acepta un id, se le pasa; si no, la llamada sigue funcionando
        setTimeout(() => {
            try { inicializarMapa(mapId); } catch (e) { /* no bloquear */ }
            resolve();
        }, delay);
    });
}
// Aplicar filtros en colecciones
/*
async function aplicarFiltrosColeccion() {
    if (!coleccionSeleccionada) return alert("Seleccioná una colección primero.");
    const params = construirParametrosFiltros("panelFiltrosColeccion");
    const modo = document.getElementById("modoNav").value;
    params.append("modoNav", modo);

    const url = `${window.METAMAPA.API_COLECCIONES}/${coleccionSeleccionada}/hechos?${params.toString()}`;
    console.log("📡 Aplicando filtros colección:", url);

    try {
        const resp = await fetch(url);
        if (!resp.ok) throw new Error("Error al aplicar filtros");
        const hechos = await resp.json();
        inicializarMapa("mapaColeccion");
        mostrarHechosEnMapa(hechos);
    } catch (e) {
        alert("Error al aplicar filtros de colección");
        console.error(e);
    }
}*/