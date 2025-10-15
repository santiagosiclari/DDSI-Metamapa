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

document.getElementById("formHecho").addEventListener("submit", crearHecho);
document.getElementById("formColeccion").addEventListener("submit", crearColeccion);
