console.log("ui.js cargado correctamente");
const cont = document.getElementById("contenido");

async function mostrar(seccion) {
    cont.innerHTML = ""; // limpiar contenido

    if (seccion === "hechos") await mostrarHechosView();
    else if (seccion === "colecciones") await mostrarColeccionesView();
    else if (seccion === "fuentes") await mostrarFuentesView();
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
    setTimeout(() => inicializarMapa(), 100);
    const hechos = await obtenerHechos();
    setTimeout(() => mostrarHechosEnMapa(hechos), 200);
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
    setTimeout(() => inicializarMapa("mapaColeccion"), 100);
    await mostrarColecciones();
}

async function mostrarFuentesView() {
    cont.innerHTML = "<p>Cargando fuentes...</p>";
    const fuentes = await obtenerFuentes();
    // Convertir el objeto { url: tipoFuente } en pares [url, tipo]
    const lista = Object.entries(fuentes)
        .map(([url, tipo]) => `
      <li class="list-group-item">
        <strong>${tipo}</strong><br>
        <a href="${url}" target="_blank">${url}</a>
      </li>
    `)
        .join("");
    cont.innerHTML = `
    <h3>Fuentes registradas (${Object.keys(fuentes).length})</h3>
    <ul class="list-group">${lista}</ul>
  `;
}

// Mostrar detalle
function mostrarDetalleHecho(h) {
    const modal = new bootstrap.Modal(document.getElementById("modalDetalle"));

    // Armar HTML detallado con todos los campos
    document.getElementById("detalleHecho").innerHTML = `
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
    </div>
  `;
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

// Limpiar marcador cuando se cierra el modal
modalHecho.addEventListener("hidden.bs.modal", limpiarMapaSeleccion);

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
        const resp = await fetch(`${window.METAMAPA.API_COLECCIONES}`);
        const colecciones = await resp.json();
        cont.innerHTML = colecciones.map(c => `
      <div class="card mb-2 p-2">
        <div class="d-flex justify-content-between align-items-center">
          <div>
            <h6 class="fw-bold mb-1">${c.titulo}</h6>
            <p class="small mb-0">${c.descripcion}</p>
          </div>
          <button class="btn btn-sm btn-outline-primary" onclick="verHechosColeccion('${c.handle}')">Ver hechos</button>
        </div>
      </div>
    `).join("");
    } catch (e) {
        cont.innerHTML = `<div class="alert alert-danger">Error al cargar colecciones</div>`;
        console.error("Error al cargar colecciones:", e);
    }
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
    const params = new URLSearchParams();

    const tituloNP = document.getElementById("tituloNP").value.trim();
    const categoriaP = document.getElementById("categoriaP").value.trim();
    if (tituloNP) params.append("tituloNP", tituloNP);
    if (categoriaP) params.append("categoriaP", categoriaP);
    params.append("modoNav", modo);

    const url = `${window.METAMAPA.API_COLECCIONES}/${coleccionSeleccionada}/hechos?${params.toString()}`;
    console.log("📡 Aplicando filtros:", url);

    try {
        const resp = await fetch(url);
        if (!resp.ok) throw new Error("Respuesta no OK del servidor");
        const hechos = await resp.json();

        setTimeout(() => mostrarHechosEnMapa(hechos), 100);
        setTimeout(() => inicializarMapa("mapaColeccion"), 300);
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