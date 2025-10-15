const cont = document.getElementById("contenido");

async function mostrar(seccion) {
    if (seccion === "hechos") {
        cont.innerHTML = `
      <h3>Hechos curados</h3>
      <div id="mapa" class="mapa"></div>
      <div id="tablaHechos" class="mt-3"></div>
    `;
        inicializarMapa("mapa");
        const hechos = await obtenerHechos();
        mostrarHechosEnMapa(hechos);
        document.getElementById("tablaHechos").innerHTML = renderTablaHechos("Hechos curados", hechos);
    }

    if (seccion === "colecciones") {
        cont.innerHTML = `
      <h3>Colecciones</h3>
      <div id="mapa" class="mapa"></div>
      <div id="listaColecciones" class="mt-3"></div>
      <div id="tablaColeccion" class="mt-3"></div>
    `;
        inicializarMapa("mapa");

        const colecciones = await obtenerColecciones();
        if (!colecciones.length) {
            cont.innerHTML += `<div class="alert alert-warning mt-3">No hay colecciones registradas.</div>`;
            return;
        }

        const lista = document.getElementById("listaColecciones");
        lista.innerHTML = `
      <ul class="list-group">
        ${colecciones.map(c => `
          <li class="list-group-item d-flex justify-content-between align-items-center">
            <span><b>${c.titulo}</b><br><small>${c.descripcion || ""}</small></span>
            <button class="btn btn-sm btn-outline-primary" onclick="verHechosColeccion('${c.id}')">Ver Hechos</button>
          </li>
        `).join("")}
      </ul>`;
    }

    if (seccion === "fuentes") {
        cont.innerHTML = "<p>Cargando fuentes...</p>";
        const fuentes = await obtenerFuentes();
        cont.innerHTML = `
      <h3>Fuentes registradas (${fuentes.length})</h3>
      <ul class="list-group">
        ${fuentes.map(u => `<li class="list-group-item">${u}</li>`).join("")}
      </ul>
    `;
    }
}

// Ver hechos de colección
async function verHechosColeccion(id) {
    const hechos = await obtenerHechosDeColeccion(id);
    mostrarHechosEnMapa(hechos);
    document.getElementById("tablaColeccion").innerHTML = renderTablaHechos("Hechos de la colección", hechos);
}

// Mostrar detalle
function mostrarDetalleHecho(h) {
    const modal = new bootstrap.Modal(document.getElementById("modalDetalle"));

    // Armar HTML detallado con todos los campos
    const detalle = `
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
      <h6>📅 Fechas</h6>
      <p><b>Fecha del hecho:</b> ${h.fechaHecho || "-"}</p>
      <p><b>Fecha de carga:</b> ${h.fechaCarga || "-"}</p>
      <p><b>Fecha de modificación:</b> ${h.fechaModificacion || "-"}</p>

      <hr>
      <h6>🧠 Datos internos</h6>
      <p><b>Perfil:</b> ${h.perfil || "<i>Sin perfil</i>"}</p>
      <p><b>Consensos:</b> ${h.consensos && h.consensos.length ? h.consensos.join(", ") : "<i>Ninguno</i>"}</p>
      <pre class="bg-light p-2 rounded"><b>Metadata:</b>\n${JSON.stringify(h.metadata || {}, null, 2)}</pre>

      <hr>
      <h6>📸 Multimedia</h6>
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

    document.getElementById("detalleHecho").innerHTML = detalle;
    modal.show();
}


// Render tabla
function renderTablaHechos(titulo, hechos) {
    if (!hechos.length) return `<div class="alert alert-info">No hay hechos disponibles.</div>`;
    return `
    <h4>${titulo} (${hechos.length})</h4>
    <table class="table table-striped table-sm">
      <thead><tr><th>Título</th><th>Categoría</th><th>Fuente</th><th>id</th><th>Fecha</th><th></th></tr></thead>
      <tbody>
        ${hechos.map(h => `
          <tr>
            <td>${h.titulo}</td>
            <td><span class="badge" style="background:${colorPorCategoria(h.categoria)}">${h.categoria || "-"}</span></td>
            <td>${h.idFuente ?? "-"}</td>
            <td>${h.id ?? "-"}</td>
            <td>${h.fechaHecho || "-"}</td>
            <td><button class="btn btn-sm btn-outline-secondary" onclick='mostrarDetalleHecho(${JSON.stringify(h)})'>Ver</button></td>
          </tr>
        `).join("")}
      </tbody>
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
function limpiarFormularioColeccion() {
    const form = document.getElementById("formColeccion");
    if (!form) return;
    form.reset();
    document.getElementById("criteriosContainer").innerHTML = "";
    const res = document.getElementById("resultadoColeccion");
    if (res) res.innerHTML = "";
}
const modalColeccion = document.getElementById("modalColeccion");
modalColeccion.addEventListener("hidden.bs.modal", limpiarFormularioColeccion);
async function editarColeccion(id) {
    const resp = await fetch(`${window.METAMAPA.API_COLECCIONES}/${id}`);
    if (!resp.ok) {
        alert("Error al obtener la colección.");
        return;
    }

    const c = await resp.json();
    console.log("✏️ Editando colección:", c);

    // Abrir modal con datos
    const modal = new bootstrap.Modal(document.getElementById("modalColeccion"));
    const form = document.getElementById("formColeccion");
    form.idColeccion.value = id;
    form.titulo.value = c.titulo;
    form.descripcion.value = c.descripcion;
    form.consenso.value = c.consenso || "mayoria";
    document.getElementById("criteriosContainer").innerHTML = "";
    (c.criterios || []).forEach(cr => agregarCriterio(cr));

    document.getElementById("modalColeccionTitle").innerText = "Editar Colección";
    modal.show();
}
const modalColeccion = document.getElementById("modalColeccion");
modalColeccion.addEventListener("hidden.bs.modal", limpiarFormularioColeccion);
function limpiarFormularioColeccion() {
    const form = document.getElementById("formColeccion");
    if (!form) return;
    form.reset();
    document.getElementById("criteriosContainer").innerHTML = "";
    document.getElementById("modalColeccionTitle").innerText = "Nueva Colección";
    const res = document.getElementById("resultadoColeccion");
    if (res) res.innerHTML = "";
}
