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
    cont.innerHTML = `
    <div class="loading">
      <span class="spinner"></span>
      <span>Cargando ${seccion}...</span>
    </div>
  `;
    const fn = vistas[seccion];
    try {
        if (fn) await fn();
        sessionStorage.setItem("vistaActual", seccion);
        verificarSesionYActualizarUI();
    } catch (e) {
        cont.innerHTML = `<div class="alert alert-danger">Error al cargar la vista: ${e?.message || e}</div>`;
        console.error(e);
    } //Agrego esta funcion aca creo que va esta en vez de la otra
//    if (window.actualizarVisibilidadPorRoles) {
//        // Usamos los roles que ya tenga guardados auth.js en su variable global
//        window.actualizarVisibilidadPorRoles();
//    }
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
    // Referencias
    const pCategoria = document.getElementById("categoriaMasReportada");
    const pSpam = document.getElementById("cantidadSpam");

    // ✅ Loading inicial (estadísticas generales)
    setLoadingUI({ container: pCategoria, message: "Cargando categoría más reportada..." });
    setLoadingUI({ container: pSpam, message: "Cargando cantidad de spam..." });

    try {
        const categoriaMasReportada = await obtenerCategoriaMasReportada();
        setText(pCategoria, categoriaMasReportada || "No hay datos");
    } catch (e) {
        setText(pCategoria, "Error al cargar");
    }

    try {
        const spam = await obtenerCantidadSolicitudesSpam();
        setText(pSpam, String(spam));
    } catch (e) {
        setText(pSpam, "Error al cargar");
    }

    // 🔹 Eventos dinámicos
    document.getElementById("btnBuscarProvinciaColeccion").addEventListener("click", async (e) => {
        const btn = e.currentTarget;
        const uuid = document.getElementById("coleccionInput").value.trim();
        if (!uuid) return alert("Ingrese un UUID de colección");

        const p = document.getElementById("provinciaColeccion");
        setLoadingUI({ container: p, message: "Buscando...", button: btn });

        try {
            const provincia = await obtenerProvinciaMasReportadaColeccion(uuid);
            setText(p, provincia || "No hay datos disponibles");
        } catch (err) {
            setText(p, "Error al buscar");
        } finally {
            setDoneUI(btn);
        }
    });

    document.getElementById("btnBuscarProvinciaCat").addEventListener("click", async (e) => {
        const btn = e.currentTarget;
        const cat = document.getElementById("categoriaInput").value.trim();
        if (!cat) return alert("Ingrese una categoría");

        const p = document.getElementById("provinciaCategoria");
        setLoadingUI({ container: p, message: "Buscando...", button: btn });

        try {
            const prov = await obtenerProvinciaMasReportadaPorCategoria(cat);
            setText(p, prov || "No hay datos disponibles");
        } catch (err) {
            setText(p, "Error al buscar");
        } finally {
            setDoneUI(btn);
        }
    });

    document.getElementById("btnBuscarHoraCat").addEventListener("click", async (e) => {
        const btn = e.currentTarget;
        const cat = document.getElementById("categoriaHoraInput").value.trim();
        if (!cat) return alert("Ingrese una categoría");

        const p = document.getElementById("horaCategoria");
        setLoadingUI({ container: p, message: "Buscando...", button: btn });

        try {
            const hora = await obtenerHoraMasReportadaPorCategoria(cat);
            setText(p, hora !== null ? `${hora}:00 hs` : "No hay datos disponibles");
        } catch (err) {
            setText(p, "Error al buscar");
        } finally {
            setDoneUI(btn);
        }
    });

    // 🔹 Botón Exportar CSV
    document.getElementById("btnExportarCSV").addEventListener("click", (e) => {
        const btn = e.currentTarget;
        btn.disabled = true;
        btn.dataset.originalText ??= btn.textContent;
        btn.textContent = "Generando…";

        try {
            const datos = [
                ["ESTADISTICA", "VALOR"],
                ["Provincia con mas hechos por Coleccion", document.getElementById("provinciaColeccion").textContent.trim()],
                ["Categoria mas reportada", document.getElementById("categoriaMasReportada").textContent.trim()],
                ["Provincia con mas hechos de una categoria", document.getElementById("provinciaCategoria").textContent.trim()],
                ["Hora del dia con mas hechos (por categoria)", document.getElementById("horaCategoria").textContent.trim()],
                ["Solicitudes de eliminacion marcadas como spam", document.getElementById("cantidadSpam").textContent.trim()]
            ];

            const csv = datos
                .map(fila => fila.map(v => `"${String(v).replace(/"/g, '""')}"`).join(";"))
                .join("\r\n");

            const blob = new Blob([csv], { type: "text/csv;charset=utf-8;" });
            const url = URL.createObjectURL(blob);

            const a = document.createElement("a");
            a.href = url;
            a.download = `estadisticas_${new Date().toISOString().split("T")[0]}.csv`;
            document.body.appendChild(a);
            a.click();
            document.body.removeChild(a);
            URL.revokeObjectURL(url);
        } finally {
            btn.disabled = false;
            btn.textContent = btn.dataset.originalText || "⬇️ Exportar CSV";
        }
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
        <button class="btn btn-sm btn-outline-secondary" id="btnAgregarFiltro" onclick="agregarFiltro('panelFiltrosHechos')">+ Agregar filtro</button>
      </div>
      <div id="filtrosContainerHechos"></div>
      <button id="btnAplicarFiltrosHechos" class="btn btn-sm btn-success mt-2" onclick="aplicarFiltrosHechos()" disabled>
        Aplicar filtros
      </button>
    </div>
    <div id="mapa" class="mapa"></div>
    <div id="tablaHechos" class="mt-3">
      ${crearSkeletonTablaHechos(8)} <!-- 8 filas de skeleton -->
    </div>
    `;
    // Forzar repintado para que el skeleton se anime antes de cargar
    await new Promise(r => setTimeout(r, 0));
    // Cargar hechos
    const hechos = await obtenerHechos();
    // Iniciar mapa (sin skeleton)
    await ensureMapaInit("mapa");
    mostrarHechosEnMapa(hechos);
    // Reemplazar skeleton por la tabla real
    document.getElementById("tablaHechos").innerHTML = renderTablaHechos("Hechos curados", hechos);
    // Observador de filtros (para habilitar botón)
    const contFiltros = document.getElementById("filtrosContainerHechos");
    const btnAplicar = document.getElementById("btnAplicarFiltrosHechos");
    const observer = new MutationObserver(() => {
        btnAplicar.disabled = contFiltros.children.length === 0;
    });
    observer.observe(contFiltros, { childList: true });
}

// --- Skeleton con la misma estructura que la tabla ---
function crearSkeletonTablaHechos(filas = 6) {
    return `
    <h4>Hechos curados</h4>
    <table class="table table-striped table-sm">
      <thead>
        <tr>
          <th>Título</th>
          <th>Categoría</th>
          <th>Fuente</th>
          <th>id</th>
          <th>Fecha</th>
          <th></th>
        </tr>
      </thead>
      <tbody>
        ${'<tr>' + '<td><div class="skeleton-cell"></div></td>'.repeat(6) + '</tr>'.repeat(filas)}
      </tbody>
    </table>
    `;
}

async function mostrarColeccionesView() {
    cont.innerHTML = `
      <div id="coleccionesView">
        <div class="d-flex justify-content-between align-items-center mb-3">
          <h4>Colecciones</h4>
          <button class="btn btn-primary admin-only d-none" data-bs-toggle="modal" data-bs-target="#modalColeccion">+ Nueva Colección </button>
        </div>
        <div class="mb-3">
          <label for="modoNav" class="form-label">Modo de navegación:</label>
          <select id="modoNav" class="form-select form-select-sm">
            <option value="IRRESTRICTA">Irrestricta</option>
            <option value="CURADA">Curada</option>
          </select>
        </div>
        <div class="mb-3">
          <label for="busquedaColeccion" class="form-label">Búsqueda (título o descripción)</label>
          <div class="input-group">
            <input id="busquedaColeccion" class="form-control form-control-sm"
                   placeholder="Escribí y presioná Enter..."
                   autocomplete="off" />
            <button class="btn btn-outline-primary btn-sm" type="button" onclick="buscarColecciones()">Buscar</button>
            <button class="btn btn-outline-secondary btn-sm" type="button" onclick="limpiarBusquedaColeccion()">Limpiar</button>
          </div>
        </div>
        <div id="panelFiltrosColeccion" class="border p-3 rounded bg-light mb-3">
          <div class="d-flex justify-content-between align-items-center mb-2">
            <h6 class="mb-0">Filtros dinámicos</h6>
            <button id="btnAgregarFiltroColeccion" class="btn btn-sm btn-outline-secondary" onclick="agregarFiltro('panelFiltrosColeccion')">
              + Agregar filtro
            </button>
          </div>
          <div id="filtrosContainerColeccion"></div>
          <button id="btnAplicarFiltrosColeccion" class="btn btn-sm btn-success mt-2" onclick="aplicarFiltrosColeccion()" disabled>
            Aplicar filtros
          </button>
        </div>
        <div id="listaColecciones" class="mb-3"></div>
        <div id="mapaColeccion" class="mapa"></div>
      </div>
    `;
    await ensureMapaInit("mapaColeccion");
    initBusquedaColecciones();
    await mostrarColecciones();
    // Habilitar / deshabilitar botón según haya filtros
    const contFiltros = document.getElementById("filtrosContainerColeccion");
    const btnAplicar = document.getElementById("btnAplicarFiltrosColeccion");
    // Observa los cambios en los hijos del contenedor
    const observer = new MutationObserver(() => {
        const tieneFiltros = contFiltros.children.length > 0;
        btnAplicar.disabled = !tieneFiltros;
    });
    observer.observe(contFiltros, { childList: true });
}

async function mostrarFuentesView() {
    cont.innerHTML = "<p>Cargando fuentes...</p>";
    const [
        estaticas,
        dinamicas,
        demo,
        metamapa
    ] = await Promise.all([
        obtenerFuentesEstaticas(),
        obtenerFuentesDinamicas(),
        obtenerFuentesDemo(),
        obtenerFuentesMetamapa()
    ]);
    let html = "";
    if (estaticas.disponible) {
        html += `
            <h3>Fuentes Estáticas (${estaticas.fuentes.length})</h3>
            ${renderFuentesEstaticas(estaticas.fuentes)}
            <button class="btn btn-success mt-2"
                    onclick="crearFuenteEstaticaView()">
                + Crear fuente estática
            </button>
        `;
    }
    if (dinamicas.disponible) {
        html += `
            <h3 class="mt-4">Fuentes Dinámicas (${dinamicas.fuentes.length})</h3>
            ${renderFuentesDinamicas(dinamicas.fuentes)}
            <button class="btn btn-success mt-2"
                    onclick="crearFuenteDinamicaView()">
                + Crear fuente dinámica
            </button>
        `;
    }
    if (demo.disponible) {
        html += `
            <h3 class="mt-4">Fuentes Demo (${demo.fuentes.length})</h3>
            ${renderFuentesDemo(demo.fuentes)}
            <button class="btn btn-success mt-2"
                    onclick="crearFuenteDemoView()">
                + Crear fuente demo
            </button>
        `;
    }
    if (metamapa.disponible) {
        html += `
            <h3 class="mt-4">Fuentes Metamapa (${metamapa.fuentes.length})</h3>
            ${renderFuentesMetamapa(metamapa.fuentes)}
            <button class="btn btn-success mt-2"
                    onclick="crearFuenteMetamapaView()">
                + Crear fuente metamapa
            </button>
        `;
    }
    cont.innerHTML = html || `<p class="text-muted">No hay servicios disponibles</p>`;
}

async function crearFuenteEstaticaView() {
    const nombre = prompt("Ingrese el nombre de la fuente estática:");
    if (!nombre) return;
    const fuente = await crearFuenteEstatica(nombre);
    if (!fuente) {
        alert("No se pudo crear la fuente estática");
        return;
    }
    mostrarModal("Fuente estática creada correctamente", "Éxito", true);
}

async function crearFuenteDinamicaView() {
    const nombre = prompt("Ingrese el nombre de la fuente dinámica:");
    if (!nombre) return;
    const fuente = await crearFuenteDinamica(nombre);
    if (!fuente) {
        alert("No se pudo crear la fuente dinámica");
        return;
    }
    mostrarModal("Fuente dinámica creada correctamente", "Éxito", true);
}

async function crearFuenteDemoView() {
    const nombre = prompt("Ingrese el nombre de la fuente demo:");
    if (!nombre) return;
    const url = prompt("Ingrese la URL de la fuente demo:");
    if (!url) return;
    const fuente = await crearFuenteDemo(nombre, url);
    if (!fuente) {
        alert("No se pudo crear la fuente demo");
        return;
    }
    mostrarModal("Fuente demo creada correctamente", "Éxito", true);
}

async function crearFuenteMetamapaView() {
    const nombre = prompt("Ingrese el nombre de la fuente Metamapa:");
    if (!nombre) return;
    const endpoint = prompt("Ingrese el endpoint de la fuente Metamapa:");
    if (!endpoint) return;
    const fuente = await crearFuenteMetamapa(nombre, endpoint);
    if (!fuente) {
        alert("No se pudo crear la fuente Metamapa");
        return;
    }
    mostrarModal("Fuente Metamapa creada correctamente", "Éxito", true);
}

function renderFuentesEstaticas(fuentes) {
    if (!fuentes.length) {
        return `<p class="text-muted">No hay fuentes estáticas disponibles</p>`;
    }
    const items = fuentes.map(f => `
        <li class="list-group-item d-flex justify-content-between align-items-start">
            <div>
                <strong>Nombre: ${f.nombre}</strong><br>
                <span>ID: ${f.fuenteId}</span><br>
            </div>
            <button class="btn btn-sm btn-primary"
                    onclick="cargarCSV(${f.fuenteId})">
                Cargar CSV
            </button>
        </li>
    `).join("");
    return `<ul class="list-group">${items}</ul>`;
}

function renderFuentesDinamicas(fuentes) {
    if (!fuentes.length) {
        return `<p class="text-muted">No hay fuentes dinámicas disponibles</p>`;
    }
    const items = fuentes.map(f => `
        <li class="list-group-item">
            <strong>Nombre: ${f.nombre}</strong><br>
            <span>ID: ${f.id}</span><br>
        </li>
    `).join("");
    return `<ul class="list-group">${items}</ul>`;
}

function renderFuentesDemo(fuentes) {
    if (!fuentes.length) {
        return `<p class="text-muted">No hay fuentes demo disponibles</p>`;
    }
    return `<ul class="list-group">
        ${fuentes.map(f => `
            <li class="list-group-item">
                <strong>Nombre: ${f.nombre}</strong><br>
                <span>ID: ${f.id}</span><br>
            </li>
        `).join("")}
    </ul>`;
}

function renderFuentesMetamapa(fuentes) {
    if (!fuentes.length) {
        return `<p class="text-muted">No hay fuentes Metamapa disponibles</p>`;
    }
    return `<ul class="list-group">
        ${fuentes.map(f => `
            <li class="list-group-item">
                <strong>Nombre: ${f.nombre}</strong><br>
                <span>ID: ${f.id}</span><br>
            </li>
        `).join("")}
    </ul>`;
}

// Mostrar detalle
function mostrarDetalleHecho(h) {
    const modalEl = document.getElementById("modalDetalle");
    const modal = bootstrap.Modal.getOrCreateInstance(modalEl);
    const contenedor = document.getElementById("detalleHecho");
    renderDetalleHechoView(h, contenedor);
    modal.show();
}

function renderDetalleHechoView(h, contenedor) {
    const media = h.multimedia?.[0];
    let mediaHTML = "";
    if (media) {
        const fileUrl = `${window.METAMAPA.API_FUENTE_DINAMICA}/archivos/${encodeURIComponent(media.path)}`;
        if (media.tipoMultimedia === "FOTO") {
            mediaHTML = `<img src="${fileUrl}" alt="Imagen principal" class="img-fluid mb-3 rounded">`;
        } else if (media.tipoMultimedia === "VIDEO") {
            mediaHTML = `
                <video controls class="img-fluid mb-3 rounded">
                  <source src="${fileUrl}" type="video/mp4">
                  Tu navegador no soporta la reproducción de video.
                </video>`;
        } else
            mediaHTML = `<a href="${fileUrl}" target="_blank">Ver archivo</a>`;
    }
    contenedor.innerHTML = `
    <div class="container-fluid">
      <h4 class="mb-3">${h.titulo}</h4>
      <div class="row">
        <div class="col-md-6">
          ${mediaHTML}
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
      <p><b>Consensos:</b> ${h.consensos?.length ? h.consensos.join(", ") : "<i>Ninguno</i>"}</p>
      <pre class="bg-light p-2 rounded"><b>Metadata:</b>\n${JSON.stringify(h.metadata || {}, null, 2)}</pre>
      <hr>
      <h6>Multimedia</h6>
      ${!h.multimedia?.length
        ? "<p><i>Sin archivos multimedia</i></p>"
        : h.multimedia.map(m =>
            `<div class="mb-2">
              <b>${m.tipoMultimedia || "Archivo"}</b>: 
              <a href="${window.METAMAPA.API_FUENTE_DINAMICA}/archivos/${encodeURIComponent(m.path)}" target="_blank">${m.path}</a>
            </div>`).join("")}
      <hr>
      <div class="d-flex justify-content-end gap-2 mt-3">
        <button id="btnSolicitarEdicion" class="btn btn-warning">Solicitar edición</button>
        <button id="btnSolicitarEliminacion" class="btn btn-danger">Solicitar eliminación</button>
      </div>
    </div>
    `;
    contenedor.querySelector("#btnSolicitarEliminacion").addEventListener("click", () => {
        mostrarFormularioEliminacion(h, contenedor);
    });
    contenedor.querySelector("#btnSolicitarEdicion").addEventListener("click", () => {
        mostrarFormularioEdicion(h, contenedor);
    });
}

function mostrarFormularioEliminacion(h, contenedor) {
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
    contenedor.querySelector("#btnCancelar").addEventListener("click", () => {
        renderDetalleHechoView(h, contenedor);
    });
    contenedor.querySelector("#btnEnviarEliminacion").addEventListener("click", async () => {
        const motivo = contenedor.querySelector("#motivoEliminacion").value.trim();
        if (!motivo) return alert("Debe ingresar un motivo.");
        const solicitud = { motivo, hechoAfectado: h.id };
        const ok = await enviarSolicitudEliminacion(solicitud);
        alert(ok ? "✅ Solicitud de eliminación enviada con éxito." : "❌ Error al enviar la solicitud.");
        renderDetalleHechoView(h, contenedor);
    });
}

function mostrarFormularioEdicion(h, contenedor) {
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
    contenedor.querySelector("#btnCancelar").addEventListener("click", () => {
        renderDetalleHechoView(h, contenedor);
    });
    contenedor.querySelector("#btnEnviarEdicion").addEventListener("click", async () => {
        const solicitud = {
            tituloMod: contenedor.querySelector("#tituloMod").value.trim(),
            descMod: contenedor.querySelector("#descMod").value.trim(),
            categoriaMod: contenedor.querySelector("#categoriaMod").value.trim(),
            latitudMod: parseFloat(contenedor.querySelector("#latitudMod").value) || null,
            longitudMod: parseFloat(contenedor.querySelector("#longitudMod").value) || null,
            fechaHechoMod: contenedor.querySelector("#fechaHechoMod").value || null,
            multimediaMod: h.multimedia || [],
            sugerencia: contenedor.querySelector("#sugerencia").value.trim(),
            hechoAfectado: h.id
        };
        const ok = await enviarSolicitudEdicion(solicitud);
        alert(ok ? "✅ Solicitud de edición enviada con éxito." : "❌ Error al enviar la solicitud.");
        renderDetalleHechoView(h, contenedor);
    });
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
    const camposMap = {
        'fecha': '.campos-fecha', 'fechareportaje': '.campos-fecha', 'fuente': '.campos-fuente',
        'ubicacion': '.campos-ubicacion', 'multimedia': '.campos-multimedia'
    };
    // Oculta todos los campos específicos
    Object.values(camposMap).forEach(selector => {
        const el = div.querySelector(selector);
        if (el) el.classList.add('d-none');
    });
    // Muestra el campo correcto
    const selectorAMostrar = camposMap[tipo];
    if (selectorAMostrar) div.querySelector(selectorAMostrar).classList.remove('d-none');
}

// === LISTA GLOBAL DE CATEGORÍAS ===
const CATEGORIAS = [
    "Incendio",
    "Incendio forestal",
    "Explosión",
    "Accidente industrial",
    "Fuga o emanación de gas",
    "Accidente químico",
    "Derrame / Fuga de sustancias",
    "Accidente ferroviario",
    "Accidente aéreo",
    "Accidente de transporte",
    "Siniestro vial",
    "Viento fuerte",
    "Viento huracanado",
    "Tormenta",
    "Granizo",
    "Lluvia",
    "Tormenta / Granizo",
    "Tormenta de nieve",
    "Inundación",
    "Emergencia sanitaria",
    "Intoxicacion masiva",
    "Contaminacion",
    "Sequia",
    "Escasez de agua",
    "Material volcanico",
    "Temperatura extrema",
    "Protesta",
    "Delito"
];

// === FUNCIONES AUXILIARES ===
function cargarCategorias() {
    const dataList = document.getElementById("categoriasList");
    if (!dataList) return;
    dataList.innerHTML = "";
    CATEGORIAS.forEach(cat => {
        const opt = document.createElement("option");
        opt.value = cat;
        dataList.appendChild(opt);
    });
}

window.agregarNuevaCategoria = function () {
    const nueva = prompt("Ingresá el nombre de la nueva categoría:");
    if (nueva && !CATEGORIAS.includes(nueva)) {
        CATEGORIAS.push(nueva);
        cargarCategorias();
        document.getElementById("categoriaSelect").value = nueva;
    }
};

// === INICIALIZACIÓN PRINCIPAL ===
document.addEventListener("DOMContentLoaded", async () => {
    console.log("Iniciando MetaMapa...");
    const formHecho = document.getElementById("formHecho");
    const formColeccion = document.getElementById("formColeccion");
    const modalHecho = document.getElementById("modalHecho");
    const modalColeccion = document.getElementById("modalColeccion");
    const categoriaInput = document.getElementById("categoriaSelect");
    categoriaInput.addEventListener("input", () => {
        const valor = categoriaInput.value.trim();
        if (valor === "" || CATEGORIAS.includes(valor)) {
            categoriaInput.classList.remove("is-invalid");
            categoriaInput.classList.add("is-valid");
        } else {
            categoriaInput.classList.remove("is-valid");
            categoriaInput.classList.add("is-invalid");
        }
    });
    if (formHecho)
        formHecho.addEventListener("submit", crearHecho);
    if (formColeccion)
        formColeccion.addEventListener("submit", crearColeccion);
    if (modalHecho) {
        modalHecho.addEventListener("shown.bs.modal", () => {
            setTimeout(inicializarMapaSeleccion, 300);
        });
        modalHecho.addEventListener("hidden.bs.modal", () => {
            limpiarMapaSeleccion();
            limpiarFormularioHecho();
        });
        modalHecho.addEventListener("shown.bs.modal", cargarCategorias);
    }
    if (modalColeccion)
        modalColeccion.addEventListener("hidden.bs.modal", limpiarFormularioColeccion);
    const vista = sessionStorage.getItem("vistaActual") || "hechos";
    await mostrar(vista);
});

function limpiarFormularioHecho() {
    const form = document.getElementById("formHecho");
    if (!form) return;
    form.reset(); // limpia los inputs normales
    // limpia campos manuales de lat/long
    document.getElementById("latitud").value = "";
    document.getElementById("longitud").value = "";
    // limpiar input de archivos, sin borrar el contenedor
    const input = document.getElementById("inputMultimedia");
    if (input) input.value = "";
    // limpiar resultado de estado
    const res = document.getElementById("resultadoHecho");
    if (res) res.innerHTML = "";
}

function limpiarFormularioColeccion() {
    const form = document.getElementById("formColeccion");
    if (!form) return;
    form.reset();
    document.getElementById("criteriosContainer").innerHTML = "";
    document.getElementById("modalColeccionTitle").innerText = "Nueva Colección";
    const res = document.getElementById("resultadoColeccion");
    if (res) res.innerHTML = "";
}

// ==================================================
// Gestión dinámica de categorías
// ==================================================
let categoriasDisponibles = new Set();

// Obtener categorías únicas desde los hechos actuales
/*async function cargarCategoriasExistentes() {
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
}*/

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
        const q = document.getElementById("busquedaColeccion")?.value?.trim() || "";

        const colecciones = await obtenerColecciones(q);

        if (!colecciones.length) {
            cont.innerHTML = q
                ? `<p class="text-muted">No se encontraron colecciones para: <b>${q}</b></p>`
                : `<p class="text-muted">No hay colecciones para mostrar.</p>`;
            return;
        }

        cont.innerHTML = colecciones.map(c => `
            <div class="card mb-2 p-2">
                <div class="d-flex justify-content-between align-items-center">
                    <div>
                        <h6 class="fw-bold mb-1">Titulo: ${c.titulo}</h6>
                        <p class="small mb-0">Descripcion: ${c.descripcion}</p>
                        <p class="small mb-0"><b>Consenso: </b> ${c.consenso}</p>
                        <p class="small mb-0"><b>ID: </b> ${c.handle}</p>
                    </div>
                    <div class="btn-group">
                        <button class="btn btn-sm btn-outline-primary" onclick="verHechosColeccion('${c.handle}')">Ver hechos</button>
                        <button class="btn btn-sm admin-only btn-outline-primary" onclick="cambiarConsenso('${c.handle}')">Cambiar consenso</button>
                    </div>
                </div>
            </div>
        `).join("");

        /*if (window.actualizarVisibilidadPorRoles) {
            window.actualizarVisibilidadPorRoles();
        }*/
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
                mostrarModal("✅ Consenso actualizado con éxito");
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

// Mostrar los hechos en el mapa
async function verHechosColeccion(idColeccion) {
    coleccionSeleccionada = idColeccion;
    const modo = document.getElementById("modoNav").value;
    const params = new URLSearchParams({ modoNavegacion: modo });
    try {
        const hechos = await obtenerHechosColeccionFiltrados(idColeccion, params);
        inicializarMapa("mapaColeccion");
        mostrarHechosEnMapa(hechos);
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
    console.log("📡 Aplicando filtros:", params.toString());
    try {
        const hechos = await obtenerHechosColeccionFiltrados(coleccionSeleccionada, params);
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
    console.log("📡 Aplicando filtros hechos:", params.toString());
    try {
        const hechos = await obtenerHechos(params);
        inicializarMapa();
        mostrarHechosEnMapa(hechos);
        document.getElementById("tablaHechos").innerHTML = renderTablaHechos("Hechos filtrados", hechos);
    } catch (e) {
        alert("Error al aplicar filtros");
        console.error(e);
    }
}

function mostrarModal(mensaje, titulo = "Atención", recargar = false) {
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
    // Cuando se cierre, limpiar y (si aplica) recargar
    modal.addEventListener("hidden.bs.modal", () => {
        modal.remove();
        if (recargar) location.reload();
    });
}

function initBusquedaColecciones() {
    const input = document.getElementById("busquedaColeccion");
    if (!input) return;
    input.addEventListener("keydown", (e) => {
        if (e.key === "Enter") {
            e.preventDefault(); // Evita el envío del formulario si existiera
            buscarColecciones(); // Solo cuando se presiona Enter
        }
    });
}

function buscarColecciones() {
    // Dispara la búsqueda al presionar el botón o Enter
    mostrarColecciones();
}

function limpiarBusquedaColeccion() {
    const input = document.getElementById("busquedaColeccion");
    if (input) input.value = "";
    mostrarColecciones();
}

function setLoadingUI({ container, message = "Cargando...", button } = {}) {
    if (button) {
        button.disabled = true;
        button.dataset.originalText ??= button.textContent;
        button.textContent = "Cargando…";
    }

    if (container) {
        container.innerHTML = `
      <span class="spinner" style="display:inline-block; vertical-align:middle;"></span>
      <span style="margin-left:8px;">${message}</span>
    `;
    }
}

function setDoneUI(button) {
    if (!button) return;
    button.disabled = false;
    button.textContent = button.dataset.originalText || "Buscar";
}

function setText(el, text) {
    if (!el) return;
    el.textContent = text;
}