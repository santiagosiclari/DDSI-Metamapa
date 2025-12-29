const currentHost = window.location.hostname;
window.METAMAPA = window.METAMAPA || {
    API_AGREGADOR: `http://${currentHost}:9004/api-agregador`,
    API_COLECCIONES: `http://${currentHost}:9004/api-colecciones`,
    API_FUENTE_DINAMICA: `http://${currentHost}:9001/api-fuentesDeDatos`,
    API_FUENTE_ESTATICA: `http://${currentHost}:9002/api-fuentesDeDatos`,
    API_FUENTE_DEMO: `http://${currentHost}:9006/api-fuentesDeDatos`,
    API_FUENTE_METAMAPA: `http://${currentHost}:9007/api-fuentesDeDatos`,
    API_SOLICITUDES: `http://${currentHost}:9004/api-solicitudes`,
    API_USUARIOS: `http://${currentHost}:9005/usuarios`,
    API_ESTADISTICA: `http://${currentHost}:9008/estadistica`
};

/* =========================================================
   Estado global
   ========================================================= */
const cont = document.getElementById("contenido");
let usuarioActual = null;
let coleccionSeleccionada = null;

// Refresca la vista actual (usa tu router "mostrar")
async function refrescarVistaActual() {
    const vista = sessionStorage.getItem("vistaActual") || "colecciones";
    try { await mostrar(vista); } catch (e) { console.error(e); }
}

// Intercepta fetch: si es una modificación (POST/PUT/PATCH/DELETE) y salió OK, refresca la vista actual
(function instalarAutoRefreshEnFetch() {
    const _fetch = window.fetch.bind(window);

    // Evita refrescos múltiples cuando un flujo hace varios fetch seguidos
    let refreshPendiente = null;

    window.fetch = async (...args) => {
        const resp = await _fetch(...args);

        try {
            const url = String(args?.[0] ?? "");
            const init = args?.[1] || {};
            const method = String(init.method || "GET").toUpperCase();

            const esMutacion = ["POST", "PUT", "PATCH", "DELETE"].includes(method);

            // Opcional: evitá refrescar por ciertos endpoints (login/logout, etc.)
            const ignorar = url.includes("/api-auth/") || url.includes("/login") || url.includes("/logout");

            if (esMutacion && !ignorar && resp.ok) {
                // refresco "debounced": si llegan 3 mutaciones seguidas, refresca 1 vez
                clearTimeout(refreshPendiente);
                refreshPendiente = setTimeout(() => {
                    refrescarVistaActual();
                }, 1000);
            }
        } catch (e) {
            console.error("Auto-refresh fetch error:", e);
        }

        return resp;
    };
})();

/* =========================================================
   Helpers UI
   ========================================================= */
function setLoadingUI({ container, message = "Cargando...", button } = {}) {
    if (button) {
        button.disabled = true;
        button.dataset.originalText ??= button.textContent;
        button.textContent = "Cargando...";
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
    button.textContent = button.dataset.originalText || "Listo";
}

function setText(el, text) {
    if (!el) return;
    el.textContent = text;
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
    const bootstrapModal = new bootstrap.Modal(modal);
    bootstrapModal.show();
    modal.addEventListener("hidden.bs.modal", () => {
        modal.remove();
        if (recargar) location.reload();
    });
}

/* =========================================================
   Fetch seguro (timeouts para servicios opcionales)
   ========================================================= */
async function fetchSeguro(url) {
    const controller = new AbortController();
    const timeout = setTimeout(() => controller.abort(), 1000);
    try {
        return await fetch(url, { signal: controller.signal });
    } catch {
        console.warn("Servicio no disponible:", url);
        return null;
    } finally {
        clearTimeout(timeout);
    }
}

/* =========================================================
   Mapas (Leaflet) - básico y estable
   ========================================================= */

// ==============================
// Colores por categoría (fallback a "Otro")
// ==============================


// ==============================
// Categorías visibles + colores persistentes
// ==============================



function _normCat(c) {
    return (c ?? "").toString().trim();
}

const LS_COLORS = "mm_categoria_colores";
let _CAT_COLORS = JSON.parse(localStorage.getItem(LS_COLORS) || "{}");

function _saveColors() {
    localStorage.setItem(LS_COLORS, JSON.stringify(_CAT_COLORS));
}

function _hash(str) {
    str = String(str || "");
    let h = 2166136261;
    for (let i = 0; i < str.length; i++) {
        h ^= str.charCodeAt(i);
        h = Math.imul(h, 16777619);
    }
    return (h >>> 0);
}

function _generarColorUnico(cat) {
    const used = new Set(Object.values(_CAT_COLORS || {}));
    const base = _hash(cat);

    for (let i = 0; i < 60; i++) {
        const hue = (base + i * 137.508) % 360;
        const color = `hsl(${hue} 70% 42%)`;
        if (!used.has(color)) return color;
    }
    return `hsl(${base % 360} 70% 42%)`;
}

function colorPorCategoria(cat) {
    const c = (cat ?? "").toString().trim() || "Otro";
    if (_CAT_COLORS[c]) return _CAT_COLORS[c];

    const nuevo = _generarColorUnico(c);
    _CAT_COLORS[c] = nuevo;
    _saveColors();
    return nuevo;
}











const categoriaColores = {
    // Incendios / explosiones
    "Incendio": "#E53935",
    "Incendio forestal": "#C62828",
    "Explosión": "#FF6F00",

    // Accidentes / transporte
    "Accidente industrial": "#6D4C41",
    "Accidente químico": "#8E24AA",
    "Accidente ferroviario": "#5E35B1",
    "Accidente aéreo": "#3949AB",
    "Accidente de transporte": "#1E88E5",
    "Siniestro vial": "#1565C0",

    // Sustancias / ambiente
    "Derrame / Fuga de sustancias": "#00ACC1",
    "Fuga o emanación de gas": "#00897B",
    "Contaminación": "#2E7D32",
    "Material volcanico": "#4E342E",

    // Clima
    "Viento fuerte": "#546E7A",
    "Viento huracanado": "#455A64",
    "Tormenta": "#283593",
    "Granizo": "#1E88E5",
    "Lluvia": "#1976D2",
    "Tormenta / Granizo": "#5C6BC0",
    "Tormenta de nieve": "#90CAF9",
    "Inundación": "#039BE5",
    "Sequia": "#F9A825",
    "Escasez de agua": "#FDD835",
    "Temperatura extrema": "#D84315",

    // Salud
    "Emergencia sanitaria": "#D81B60",
    "Intoxicacion masiva": "#C2185B",

    // Social
    "Protesta": "#8D6E63",
    "Delito": "#424242",

    // Fallback
    "Otro": "#43A047"
};


const _maps = new Map(); // divId -> { map, markers, legend }

// Espera a que el div exista y tenga tamaño
async function waitForMapContainer(divId, { tries = 40, delay = 80 } = {}) {
    for (let i = 0; i < tries; i++) {
        const el = document.getElementById(divId);
        if (el && el.offsetHeight > 0 && el.offsetWidth > 0) return el;
        await new Promise((r) => setTimeout(r, delay));
    }
    return document.getElementById(divId); // último intento (puede ser null)
}

// ==============================
// MAPA PRINCIPAL / GENÉRICO
// ==============================
async function inicializarMapa(divId = "mapa") {
    const contEl = await waitForMapContainer(divId);
    if (!contEl) {
        console.warn("No existe contenedor de mapa:", divId);
        return null;
    }

    // Si ya existe en cache, validar que siga apuntando al mismo DIV real
    if (_maps.has(divId)) {
        const ctx = _maps.get(divId);
        const liveEl = document.getElementById(divId);

        const contenedorOk =
            ctx?.map?._container &&
            liveEl &&
            ctx.map._container === liveEl;

        // Si el DIV fue reemplazado por innerHTML, el mapa quedó atado al viejo: lo destruimos
        if (!contenedorOk) {
            try { ctx.map.remove(); } catch (e) {}
            _maps.delete(divId);
        } else {
            // Reusar mapa existente
            requestAnimationFrame(() => ctx.map.invalidateSize(true));
            return ctx.map;
        }
    }

    // Crear mapa nuevo
    const map = L.map(divId).setView([-34.61, -58.38], 11);

    L.tileLayer("https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png", {
        attribution: "&copy; OpenStreetMap contributors"
    }).addTo(map);

    const markers = L.layerGroup().addTo(map);

    const legend = L.control({ position: "bottomright" });
    legend.onAdd = function () {
        this._div = L.DomUtil.create("div", "mapa-leyenda");
        this._div.style.cssText = `
          background: rgba(255,255,255,0.92);
          padding: 10px 12px;
          border-radius: 10px;
          border: 1px solid rgba(0,0,0,0.12);
          box-shadow: 0 8px 18px rgba(0,0,0,0.08);
          font-size: 12px;
          line-height: 1.2;
          max-width: 220px;
        `;
        this._div.innerHTML = "<strong>Leyenda</strong><br><small>Sin datos</small>";
        return this._div;
    };
    legend.addTo(map);

    _maps.set(divId, { map, markers, legend });

    // Importante: invalidar tamaño cuando ya está pintado
    requestAnimationFrame(() => map.invalidateSize(true));

    console.log("Mapa inicializado:", divId);
    return map;
}

function limpiarMarcadores(divId = "mapa") {
    const ctx = _maps.get(divId);
    ctx?.markers?.clearLayers();
}

function agregarMarcador(divId, hecho) {
    const ctx = _maps.get(divId);
    if (!ctx) return;

    const { latitud, longitud, titulo, descripcion, fechaHecho } = hecho;
    if (latitud == null || longitud == null) return;

    const color = colorPorCategoria(hecho.categoria);

    const icono = L.divIcon({
        html: `<div style="background:${color};width:16px;height:16px;border-radius:50%;border:2px solid white"></div>`,
        className: ""
    });

    const popup = `
    <b>${titulo || "Sin título"}</b><br>
    ${descripcion || ""}<br>
    <small>${hecho.categoria || "Otro"} | ${fechaHecho || ""}</small>
  `;

    L.marker([latitud, longitud], { icon: icono })
        .bindPopup(popup)
        .on("click", () => {
            if (typeof mostrarDetalleHecho === "function") mostrarDetalleHecho(hecho);
        })
        .addTo(ctx.markers);
}

function actualizarLeyenda(divId, hechos) {
    const ctx = _maps.get(divId);
    if (!ctx?.legend?._div) return;

    const cats = [...new Set((hechos || []).map((h) => h.categoria || "Otro"))];

    ctx.legend._div.innerHTML = cats.length
        ? `<strong>Leyenda</strong><br>${cats
            .map(
                (c) => `
        <div style="display:flex;align-items:center;margin:4px 0">
          <div style="background:${colorPorCategoria(c)};width:12px;height:12px;border-radius:50%;margin-right:8px"></div>
          <span>${c}</span>
        </div>`
            )
            .join("")}`
        : "<strong>Leyenda</strong><br><small>Sin hechos</small>";
}

function mostrarHechosEnMapa(hechos, divId = "mapa") {
    const ctx = _maps.get(divId);
    if (!ctx) return;

    limpiarMarcadores(divId);

    const lista = Array.isArray(hechos) ? hechos : [];
    lista.forEach((h) => agregarMarcador(divId, h));
    actualizarLeyenda(divId, lista);

    // Si hay hechos, centrar en el primero; si no, dejar vista default
    const first = lista.find((h) => h.latitud != null && h.longitud != null);
    if (first) {
        ctx.map.setView([first.latitud, first.longitud], 11);
    } else {
        ctx.map.setView([-34.61, -58.38], 11);
    }

    // Recalcular tamaño por si el contenedor se montó recién
    setTimeout(() => ctx.map.invalidateSize(true), 0);
}

// Helper para inicializar mapa de forma segura antes de pintar
async function ensureMapaInit(mapId = "mapa") {
    const map = await inicializarMapa(mapId);
    const ctx = _maps.get(mapId);
    if (ctx?.map) {
        requestAnimationFrame(() => ctx.map.invalidateSize(true));
        requestAnimationFrame(() => ctx.map.invalidateSize(true));
    }
    return map;
}


// ==========================
// Mapa selector para crear hecho (modal)
// ==========================
let mapaSeleccion, marcadorSeleccion;

function inicializarMapaSeleccion() {
    const el = document.getElementById("mapaSeleccion");
    if (!el) return;

    if (mapaSeleccion) {
        setTimeout(() => mapaSeleccion.invalidateSize(true), 0);
        return;
    }

    mapaSeleccion = L.map("mapaSeleccion").setView([-34.61, -58.38], 11);

    L.tileLayer("https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png", {
        attribution: "&copy; OpenStreetMap contributors"
    }).addTo(mapaSeleccion);

    navigator.geolocation?.getCurrentPosition(({ coords }) => {
        mapaSeleccion.setView([coords.latitude, coords.longitude], 13);
    });

    mapaSeleccion.on("click", (e) => colocarMarcadorSeleccion(e.latlng.lat, e.latlng.lng));

    setTimeout(() => mapaSeleccion.invalidateSize(true), 0);
}

function colocarMarcadorSeleccion(lat, lng) {
    if (!marcadorSeleccion) {
        marcadorSeleccion = L.marker([lat, lng], { draggable: true })
            .addTo(mapaSeleccion)
            .on("drag dragend", (ev) => {
                const p = ev.target.getLatLng();
                actualizarInputsLatLng(p.lat, p.lng);
            });
    } else {
        marcadorSeleccion.setLatLng([lat, lng]);
    }
    actualizarInputsLatLng(lat, lng);
}

function actualizarInputsLatLng(lat, lng) {
    const inLat = document.getElementById("latitud");
    const inLng = document.getElementById("longitud");
    if (inLat) inLat.value = Number(lat).toFixed(6);
    if (inLng) inLng.value = Number(lng).toFixed(6);
}

function limpiarMapaSeleccion() {
    if (mapaSeleccion && marcadorSeleccion) {
        mapaSeleccion.removeLayer(marcadorSeleccion);
        marcadorSeleccion = null;
    }
}

// =============================
// MAPA DE UBICACIÓN (criterios con radio)
// =============================
let mapaUbicacion, marcadorUbicacion, circuloUbicacion;
let radioActual = 5; // km
let _mapaUbicacionContext = null;

function abrirMapaUbicacion(boton) {
    try {
        const parent =
            boton.closest(".criterio-box") ||
            boton.closest(".p-2") ||
            boton.closest(".row") ||
            document;

        const latInput = parent.querySelector("input[name='latitud']") || parent.querySelector(".latitud");
        const lonInput = parent.querySelector("input[name='longitud']") || parent.querySelector(".longitud");
        const radioInput = parent.querySelector("input[name='radio']") || parent.querySelector(".radio");

        _mapaUbicacionContext = { latInput, lonInput, radioInput };

        const modal = new bootstrap.Modal(document.getElementById("modalUbicacion"));
        modal.show();

        setTimeout(() => {
            if (!mapaUbicacion) inicializarMapaUbicacion();
            else mapaUbicacion.invalidateSize(true);
        }, 200);
    } catch (err) {
        console.error("Error al abrir mapa de ubicación:", err);
    }
}

function inicializarMapaUbicacion() {
    if (mapaUbicacion) {
        setTimeout(() => mapaUbicacion.invalidateSize(true), 0);
        return;
    }

    mapaUbicacion = L.map("mapaUbicacion").setView([-34.61, -58.38], 11);

    L.tileLayer("https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png", {
        attribution: "&copy; OpenStreetMap contributors"
    }).addTo(mapaUbicacion);

    mapaUbicacion.on("click", (e) => colocarMarcadorUbicacion(e.latlng.lat, e.latlng.lng));

    const slider = document.getElementById("radioSlider");
    if (slider) {
        radioActual = parseFloat(slider.value || "5");
        slider.addEventListener("input", (e) => {
            radioActual = parseFloat(e.target.value);
            const lab = document.getElementById("radioLabel");
            if (lab) lab.innerText = `${radioActual} km`;
            actualizarCirculoUbicacion();
        });
    }

    setTimeout(() => mapaUbicacion.invalidateSize(true), 0);
}

function colocarMarcadorUbicacion(lat, lng) {
    if (!marcadorUbicacion) {
        marcadorUbicacion = L.marker([lat, lng], { draggable: true }).addTo(mapaUbicacion);
        marcadorUbicacion.on("drag", (e) => {
            const p = e.target.getLatLng();
            actualizarCirculoUbicacion(p.lat, p.lng);
            const cont = document.getElementById("mapaUbicacion");
            if (cont) {
                cont.dataset.lat = p.lat;
                cont.dataset.lng = p.lng;
            }
        });
    } else {
        marcadorUbicacion.setLatLng([lat, lng]);
    }

    actualizarCirculoUbicacion(lat, lng);

    const cont = document.getElementById("mapaUbicacion");
    if (cont) {
        cont.dataset.lat = lat;
        cont.dataset.lng = lng;
    }
}

function actualizarCirculoUbicacion(lat, lng) {
    if ((lat == null || lng == null) && marcadorUbicacion) {
        const p = marcadorUbicacion.getLatLng();
        lat = p.lat;
        lng = p.lng;
    }
    if (lat == null || lng == null) return;

    const radiusMeters = radioActual * 1000;

    if (circuloUbicacion) circuloUbicacion.remove();
    circuloUbicacion = L.circle([lat, lng], {
        radius: radiusMeters,
        color: "green",
        fillOpacity: 0.18
    }).addTo(mapaUbicacion);

    mapaUbicacion.setView([lat, lng], 12);
}

function confirmarUbicacion() {
    const mapaCont = document.getElementById("mapaUbicacion");
    const lat = parseFloat(mapaCont?.dataset?.lat);
    const lng = parseFloat(mapaCont?.dataset?.lng);
    const radio = parseFloat(document.getElementById("radioSlider")?.value);

    if (isNaN(lat) || isNaN(lng)) {
        alert("Seleccioná una ubicación en el mapa.");
        return;
    }

    if (_mapaUbicacionContext) {
        const { latInput, lonInput, radioInput } = _mapaUbicacionContext;
        if (latInput) latInput.value = lat.toFixed(6);
        if (lonInput) lonInput.value = lng.toFixed(6);
        if (radioInput && !isNaN(radio)) radioInput.value = radio.toFixed(1);
    }

    bootstrap.Modal.getInstance(document.getElementById("modalUbicacion")).hide();
}

/* =========================================================
   Sanitización básica para HTML
   ========================================================= */
function escapeHtml(str) {
    return String(str ?? "")
        .replaceAll("&", "&amp;")
        .replaceAll("<", "&lt;")
        .replaceAll(">", "&gt;")
        .replaceAll('"', "&quot;")
        .replaceAll("'", "&#039;");
}

/* =========================================================
   Categorías
   ========================================================= */
// ==============================
// Categorías: solo desde hechos
// ==============================
let _CATS_FROM_HECHOS = new Set(); // estado en memoria (se reconstruye con cada fetch)

function categoriasDisponibles() {
    return [..._CATS_FROM_HECHOS]
        .filter(Boolean)
        .sort((a, b) => a.localeCompare(b, "es"));
}

function registrarCategoriasDesdeHechos(hechos) {
    (hechos || []).forEach(h => {
        const c = (h?.categoria || "").trim();
        if (c) _CATS_FROM_HECHOS.add(c);
    });
}

function cargarCategorias() {
    const dataList = document.getElementById("categoriasList");
    if (!dataList) return;
    const cats = categoriasDisponibles();
    dataList.innerHTML = "";
    cats.forEach(cat => {
        const opt = document.createElement("option");
        opt.value = cat;
        dataList.appendChild(opt);
    });
}



function agregarNuevaCategoriaModal() {
    document.getElementById("nuevaCategoriaInput").value = "";
    const modal = new bootstrap.Modal(document.getElementById("modalCategoria"));
    modal.show();
}

function guardarNuevaCategoria() {
    const input = document.getElementById("nuevaCategoriaInput");
    const nueva = (input?.value || "").trim();
    if (!nueva) return mostrarModal("Debe escribir una categoría válida.");

    // solo setea el valor en el input del formulario del hecho
    const categoriaSelect = document.getElementById("categoriaSelect");
    if (categoriaSelect) {
        categoriaSelect.value = nueva;

        // opcional: marcar validación visual
        categoriaSelect.classList.remove("is-invalid");
        categoriaSelect.classList.add("is-valid");
    }

    // opcional: asignar color si querés que el badge/mapa ya lo tome
    try { colorPorCategoria(nueva); } catch {}

    bootstrap.Modal.getInstance(document.getElementById("modalCategoria")).hide();
}



window.agregarNuevaCategoriaModal = agregarNuevaCategoriaModal;
window.guardarNuevaCategoria = guardarNuevaCategoria;

/* =========================================================
   API: Fuentes
   ========================================================= */
async function obtenerFuentesDinamicas() {
    const resp = await fetchSeguro(`${window.METAMAPA.API_FUENTE_DINAMICA}/`);
    if (!resp) return { disponible: false, fuentes: [] };
    if (!resp.ok) return { disponible: true, fuentes: [] };
    return { disponible: true, fuentes: await resp.json() };
}

async function obtenerFuentesEstaticas() {
    const resp = await fetchSeguro(`${window.METAMAPA.API_FUENTE_ESTATICA}/`);
    if (!resp) return { disponible: false, fuentes: [] };
    if (!resp.ok) return { disponible: true, fuentes: [] };
    return { disponible: true, fuentes: await resp.json() };
}

async function obtenerFuentesDemo() {
    const resp = await fetchSeguro(`${window.METAMAPA.API_FUENTE_DEMO}/`);
    if (!resp) return { disponible: false, fuentes: [] };
    if (!resp.ok) return { disponible: true, fuentes: [] };
    return { disponible: true, fuentes: await resp.json() };
}

async function obtenerFuentesMetamapa() {
    const resp = await fetchSeguro(`${window.METAMAPA.API_FUENTE_METAMAPA}/`);
    if (!resp) return { disponible: false, fuentes: [] };
    if (!resp.ok) return { disponible: true, fuentes: [] };
    return { disponible: true, fuentes: await resp.json() };
}

async function crearFuenteDinamica(nombre) {
    const resp = await fetch(`${window.METAMAPA.API_FUENTE_DINAMICA}/`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ nombre })
    });
    return resp.ok ? await resp.json() : null;
}

async function crearFuenteEstatica(nombre) {
    const resp = await fetch(`${window.METAMAPA.API_FUENTE_ESTATICA}/`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ nombre })
    });
    return resp.ok ? await resp.json() : null;
}

async function crearFuenteDemo(nombre, url) {
    const resp = await fetch(`${window.METAMAPA.API_FUENTE_DEMO}/`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ nombre, url })
    });
    return resp.ok ? await resp.json() : null;
}

async function crearFuenteMetamapa(nombre, endpoint) {
    const resp = await fetch(`${window.METAMAPA.API_FUENTE_METAMAPA}/`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ nombre, endpoint })
    });
    return resp.ok ? await resp.json() : null;
}
function badgeCategoria(cat) {
    const label = cat || "Otro";
    const color = colorPorCategoria(label);
    return `
    <span class="badge"
          style="background:${color}; color:#fff; font-weight:600;">
      ${escapeHtml(label)}
    </span>
  `;
}

async function cargarCSV(idFuenteDeDatos) {
    const input = document.createElement("input");
    input.type = "file";
    input.accept = ".csv";
    input.onchange = async () => {
        const file = input.files[0];
        if (!file) return;
        const formData = new FormData();
        formData.append("file", file);
        try {
            const resp = await fetch(`${window.METAMAPA.API_FUENTE_ESTATICA}/${idFuenteDeDatos}/csv`, {
                method: "POST",
                body: formData
            });
            if (!resp.ok) throw new Error(await resp.text());
            const mensaje = await resp.text();
            mostrarModal(mensaje, "CSV cargado");
        } catch (e) {
            console.error(e);
            mostrarModal("Error al cargar el CSV", "Error");
        }
    };
    input.click();
}

window.cargarCSV = cargarCSV;

/* Selector de fuentes dinámicas en modal hecho */
async function cargarSelectFuentesDinamicas() {
    const select = document.getElementById("idFuente");
    if (!select) return;

    select.disabled = true;
    select.innerHTML = `<option value="">Cargando fuentes...</option>`;

    try {
        const res = await obtenerFuentesDinamicas();

        if (!res || !res.disponible) {
            select.innerHTML = `<option value="">Servicio no disponible</option>`;
            return;
        }

        const fuentes = res.fuentes || [];
        if (!fuentes.length) {
            select.innerHTML = `<option value="">No hay fuentes dinámicas</option>`;
            return;
        }

        select.innerHTML =
            `<option value="" selected>Seleccioná una fuente...</option>` +
            fuentes.map(f => `<option value="${f.id}">${escapeHtml(f.nombre)}</option>`).join("");

    } catch (e) {
        console.error("Error cargando fuentes dinámicas:", e);
        select.innerHTML = `<option value="">Error al cargar fuentes</option>`;
    } finally {
        select.disabled = false;
    }
}

/* =========================================================
   API: Estadisticas
   ========================================================= */
async function actualizarEstadisticas() {
    try {
        const resp = await fetch(`${window.METAMAPA.API_ESTADISTICA}/actualizar`, { method: "POST" });
        if (resp.ok) mostrarModal("Estadisticas actualizadas.", "Actualización");
        else mostrarModal(await resp.text(), "Error");
    } catch (e) {
        mostrarModal(e.message || "Error de red", "Error");
    }
}

/* =========================================================
   API: Hechos
   ========================================================= */
async function obtenerHechos(params = new URLSearchParams()) {
    try {
        const url = `${window.METAMAPA.API_AGREGADOR}/hechos?${params.toString()}`;
        const resp = await fetch(url);
        if (!resp.ok) throw new Error("Error al obtener hechos");
        return await resp.json();
    } catch (e) {
        console.error("Error en obtenerHechos:", e);
        return [];
    }
}

async function actualizarHechos() {
    try {
        const resp = await fetch(`${window.METAMAPA.API_AGREGADOR}/actualizarHechos`, { method: "POST" });
        if (resp.ok) mostrarModal("Hechos actualizados desde las fuentes.", "Actualización");
        else mostrarModal(await resp.text(), "Error");
    } catch (e) {
        mostrarModal(e.message || "Error de red", "Error");
    }
}
window.actualizarHechos = actualizarHechos;

async function curarHechos() {
    const resp = await fetch(`${window.METAMAPA.API_AGREGADOR}/consensuarHechos`, { method: "POST" });
    mostrarModal(resp.ok ? "Curado completado." : "Error al curar hechos.", "Curado");
}
window.curarHechos = curarHechos;

async function crearHecho(e) {
    e.preventDefault();
    const f = e.target;

    const categoria = (f.categoria.value || "").trim();
    if (!categoria) {
        mostrarModal("Debés indicar una categoría.", "Validación");
        return;
    }



    const btn = document.getElementById("btnGuardar");
    const texto = document.getElementById("btnGuardarTexto");
    const loader = document.getElementById("btnGuardarLoader");
    const res = document.getElementById("resultadoHecho");

    btn.disabled = true;
    loader.classList.remove("d-none");
    texto.textContent = "Guardando...";
    res.textContent = "";

    try {
        const data = {
            titulo: (f.titulo.value || "").trim(),
            descripcion: (f.descripcion.value || "").trim(),
            categoria,
            latitud: parseFloat(f.latitud.value),
            longitud: parseFloat(f.longitud.value),
            fechaHecho: f.fechaHecho?.value || new Date().toISOString().slice(0, 16),
            idUsuario: usuarioActual?.id || null,
            fuenteId: parseInt(f.idFuente.value, 10),
            anonimo: !!f.anonimo.checked
        };

        if (!data.titulo || !data.descripcion || Number.isNaN(data.fuenteId)) {
            mostrarModal("Debés completar título, descripción y fuente.", "Validación");
            return;
        }

        const formData = new FormData();
        formData.append("hecho", JSON.stringify(data));

        const input = document.getElementById("inputMultimedia");
        for (let i = 0; i < (input?.files?.length || 0); i++) {
            formData.append("archivos", input.files[i]);
        }

        const resp = await fetch(`${window.METAMAPA.API_FUENTE_DINAMICA}/${data.fuenteId}/hechos`, {
            method: "POST",
            body: formData
        });

        if (resp.ok) {
            const json = await resp.json();
            res.textContent = `Hecho creado (ID: ${json.id ?? "sin id"})`;
            colorPorCategoria(categoria);
            registrarCategoriasDesdeHechos([{ categoria }]);
            cargarCategorias();
            const modal = bootstrap.Modal.getInstance(document.getElementById("modalHecho"));
            modal.hide();
            limpiarFormularioHecho();

            mostrarModal(`Hecho creado (ID: ${json.id ?? "sin id"})`, "Hecho");
        } else {
            const errorTxt = await resp.text();
            res.textContent = `Error al crear el hecho: ${errorTxt}`;
        }
    } catch (error) {
        console.error(error);
        res.textContent = "Error inesperado al crear el hecho.";
    } finally {
        btn.disabled = false;
        loader.classList.add("d-none");
        texto.textContent = "Guardar";
    }
}

/* =========================================================
   API: Colecciones
   ========================================================= */
async function obtenerColecciones(query = "") {
    const q = (query || "").trim();
    const url = q
        ? `${window.METAMAPA.API_COLECCIONES}?query=${encodeURIComponent(q)}`
        : `${window.METAMAPA.API_COLECCIONES}`;
    const resp = await fetch(url);
    return resp.ok ? resp.json() : [];
}

async function obtenerHechosColeccionFiltrados(idColeccion, params) {
    const query = params?.toString();
    const url = `${window.METAMAPA.API_COLECCIONES}/${idColeccion}/hechos${query ? "?" + query : ""}`;
    const resp = await fetch(url);
    if (!resp.ok) throw new Error("Respuesta no OK del servidor");
    return await resp.json();
}

async function modificarConsensoColeccion(id, consenso) {
    const resp = await fetch(`${window.METAMAPA.API_COLECCIONES}/${id}`, {
        method: "PATCH",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ consenso })
    });
    return resp.ok;
}

async function crearColeccion(e) {
    e.preventDefault();
    const f = e.target;

    const criterios = [...document.querySelectorAll("#criteriosContainer .criterio-box")].map(armarCriterio);
    const data = {
        titulo: (f.titulo.value || "").trim(),
        descripcion: (f.descripcion.value || "").trim(),
        consenso: f.consenso.value,
        criterios
    };

    const id = f.idColeccion.value;
    const url = id ? `${window.METAMAPA.API_COLECCIONES}/${id}` : `${window.METAMAPA.API_COLECCIONES}/`;
    const method = id ? "PUT" : "POST";

    try {
        const resp = await fetch(url, {
            method,
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(data)
        });

        if (resp.ok) {
            const json = await resp.json();
            const modal = bootstrap.Modal.getInstance(document.getElementById("modalColeccion"));
            modal.hide();
            limpiarFormularioColeccion();
            mostrarModal(`Colección guardada (${escapeHtml(json.handle || json.id)})`, "Colecciones");
            await mostrar("colecciones");
        } else {
            mostrarModal(await resp.text(), "Error");
        }
    } catch (err) {
        mostrarModal(err.message || "Error inesperado", "Error");
    }
}

/* =========================================================
   API: Solicitudes (simple)
   ========================================================= */
async function obtenerSolicitudesEliminacion() {
    const resp = await fetch(`${window.METAMAPA.API_SOLICITUDES}/solicitudesEliminacion`);
    return resp.ok ? resp.json() : [];
}

async function enviarSolicitudEliminacion(solicitud) {
    const resp = await fetch(`${window.METAMAPA.API_SOLICITUDES}/solicitudesEliminacion`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(solicitud)
    });
    return resp.ok;
}

async function procesarSolicitudEliminacion(aprobada, id) {
    const accion = aprobada ? "APROBAR" : "RECHAZAR";
    const resp = await fetch(`${window.METAMAPA.API_SOLICITUDES}/solicitudesEliminacion/${id}`, {
        method: "PATCH",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ accion })
    });
    if (resp.ok) {
        await mostrarSolicitudesView();
    } else {
        mostrarModal(await resp.text(), "Error");
    }
}

async function obtenerSolicitudesEdicion() {
    const resp = await fetch(`${window.METAMAPA.API_SOLICITUDES}/solicitudesEdicion`);
    return resp.ok ? resp.json() : [];
}

async function enviarSolicitudEdicion(solicitud) {
    const resp = await fetch(`${window.METAMAPA.API_SOLICITUDES}/solicitudesEdicion`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(solicitud)
    });
    return resp.ok;
}

async function procesarSolicitudEdicion(aprobada, id) {
    const estado = aprobada ? "APROBADA" : "RECHAZADA";
    const resp = await fetch(`${window.METAMAPA.API_SOLICITUDES}/solicitudesEdicion/${id}`, {
        method: "PATCH",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ estado })
    });
    if (resp.ok) {
        await mostrarSolicitudesView();
    } else {
        mostrarModal(await resp.text(), "Error");
    }
}

/* =========================================================
   Vistas
   ========================================================= */
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
      <span>Cargando ${escapeHtml(seccion)}...</span>
    </div>
  `;

    const fn = vistas[seccion];
    try {
        if (fn) await fn();
        sessionStorage.setItem("vistaActual", seccion);
        marcarNavActiva(seccion);              // <-- AGREGAR ESTO
        verificarSesionYActualizarUI();
    } catch (e) {
        cont.innerHTML = `<div class="alert alert-danger">Error al cargar la vista: ${escapeHtml(e?.message || e)}</div>`;
        console.error(e);
    }
}

window.mostrar = mostrar;

/* === Hechos === */
function crearSkeletonTablaHechos(filas = 6) {
    return `
    <h4>Hechos</h4>
    <table class="table table-striped table-sm">
      <thead>
        <tr>
          <th>Título</th>
          <th>Categoría</th>
          <th>Fuente</th>
          <th>ID</th>
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

function renderTablaHechos(titulo, hechos) {
    if (!hechos.length) return `<div class="alert alert-info">No hay hechos disponibles.</div>`;
    const filas = hechos.map(h => `
    <tr>
      <td>${escapeHtml(h.titulo)}</td>
      <td>${badgeCategoria(h.categoria)}</td>
      <td>${escapeHtml(h.idFuente ?? "-")}</td>
      <td>${escapeHtml(h.id ?? "-")}</td>
      <td>${escapeHtml(h.fechaHecho || "-")}</td>
      <td><button class="btn btn-sm btn-outline-secondary" onclick='mostrarDetalleHecho(${JSON.stringify(h)})'>Ver</button></td>
    </tr>
  `).join("");

    return `
    <h4>${escapeHtml(titulo)} (${hechos.length})</h4>
    <table class="table table-striped table-sm">
      <thead><tr><th>Título</th><th>Categoría</th><th>Fuente</th><th>ID</th><th>Fecha</th><th></th></tr></thead>
      <tbody>${filas}</tbody>
    </table>
  `;
}

async function mostrarHechosView() {
    cont.innerHTML = `
    <h3>Hechos curados</h3>

    <div id="panelFiltrosHechos" class="border p-3 rounded bg-light mb-3">
      <div class="d-flex justify-content-between align-items-center mb-2">
        <h6 class="mb-0">Filtros dinámicos</h6>
        <button class="btn btn-sm btn-outline-secondary" id="btnAgregarFiltro" onclick="agregarFiltro('panelFiltrosHechos')">
          Agregar filtro
        </button>
      </div>
      <div id="filtrosContainerHechos"></div>
      <button id="btnAplicarFiltrosHechos" class="btn btn-sm btn-success mt-2" onclick="aplicarFiltrosHechos()" disabled>
        Aplicar filtros
      </button>
    </div>

    <div id="mapa" class="mapa"></div>

    <div id="tablaHechos" class="mt-3">
      ${crearSkeletonTablaHechos(8)}
    </div>
  `;

    await new Promise(r => setTimeout(r, 0));

    const hechos = await obtenerHechos();
    registrarCategoriasDesdeHechos(hechos);
    cargarCategorias();
    await ensureMapaInit("mapa");
    mostrarHechosEnMapa(hechos, "mapa");

    document.getElementById("tablaHechos").innerHTML = renderTablaHechos("Hechos curados", hechos);

    const contFiltros = document.getElementById("filtrosContainerHechos");
    const btnAplicar = document.getElementById("btnAplicarFiltrosHechos");

    const observer = new MutationObserver(() => {
        btnAplicar.disabled = contFiltros.children.length === 0;
    });
    observer.observe(contFiltros, { childList: true });
}

/* === Colecciones === */
async function mostrarColeccionesView() {
    cont.innerHTML = `
    <div id="coleccionesView">
      <div class="d-flex justify-content-between align-items-center mb-3">
        <h4>Colecciones</h4>
        <button class="btn btn-primary admin-only d-none" data-bs-toggle="modal" data-bs-target="#modalColeccion">
          Nueva colección
        </button>
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
                 placeholder="Escribí y presioná Enter..." autocomplete="off" />
          <button id="btnBuscarColeccion" class="btn btn-outline-primary btn-sm" type="button">Buscar</button>
          <button id="btnLimpiarColeccion" class="btn btn-outline-secondary btn-sm" type="button">Limpiar</button>
        </div>
      </div>

      <div id="listaColecciones" class="mb-3"></div>
      
      <div id="panelFiltrosColeccion" class="border p-3 rounded bg-light mb-3 d-none">
        <div class="d-flex justify-content-between align-items-center mb-2">
          <h6 class="mb-0">Filtros dinámicos</h6>
          <button id="btnAgregarFiltroColeccion" class="btn btn-sm btn-outline-secondary" onclick="agregarFiltro('panelFiltrosColeccion')">
            Agregar filtro
          </button>
        </div>
        <div id="filtrosContainerColeccion"></div>
        <button id="btnAplicarFiltrosColeccion" class="btn btn-sm btn-success mt-2" disabled>
          Aplicar filtros
        </button>
      </div>
      
      <div id="mapaColeccion" class="mapa"></div> 
    </div>
  `;

    await ensureMapaInit("mapaColeccion");

    const lista = document.getElementById("listaColecciones");
    setLoadingUI({ container: lista, message: "Cargando colecciones..." });
    await mostrarColecciones();

    const contFiltros = document.getElementById("filtrosContainerColeccion");
    const btnAplicar = document.getElementById("btnAplicarFiltrosColeccion");

    const observer = new MutationObserver(() => {
        btnAplicar.disabled = contFiltros.children.length === 0;
    });
    observer.observe(contFiltros, { childList: true });

    const btnBuscar = document.getElementById("btnBuscarColeccion");
    const btnLimpiar = document.getElementById("btnLimpiarColeccion");
    const input = document.getElementById("busquedaColeccion");

    async function buscarColeccionesConLoading() {
        // CAMBIO: Si buscamos de nuevo, ocultamos el panel para "limpiar" la vista visualmente
        const panel = document.getElementById("panelFiltrosColeccion");
        if(panel) panel.classList.add("d-none");

        setLoadingUI({ container: lista, message: "Buscando colecciones..." });
        setLoadingUI({ button: btnBuscar });
        setLoadingUI({ button: btnLimpiar });
        try {
            await mostrarColecciones();
        } finally {
            setDoneUI(btnBuscar);
            setDoneUI(btnLimpiar);
            btnBuscar.textContent = "Buscar";
            btnLimpiar.textContent = "Limpiar";
        }
    }

    btnBuscar.addEventListener("click", buscarColeccionesConLoading);
    btnLimpiar.addEventListener("click", async () => {
        input.value = "";
        await buscarColeccionesConLoading();
    });
    input.addEventListener("keydown", (e) => {
        if (e.key === "Enter") {
            e.preventDefault();
            buscarColeccionesConLoading();
        }
    });

    btnAplicar.addEventListener("click", async (e) => {
        const btn = e.currentTarget;
        setLoadingUI({ button: btn });
        btn.textContent = "Aplicando filtros...";
        try {
            await aplicarFiltrosColeccion();
        } finally {
            const tieneFiltros = contFiltros.children.length > 0;
            btn.textContent = "Aplicar filtros";
            btn.disabled = !tieneFiltros;
        }
    });
}

let _cacheFuentesTodas = null;

async function obtenerTodasLasFuentes() {
    if (_cacheFuentesTodas) return _cacheFuentesTodas;

    const [est, din, demo, meta] = await Promise.all([
        obtenerFuentesEstaticas(),
        obtenerFuentesDinamicas(),
        obtenerFuentesDemo(),
        obtenerFuentesMetamapa()
    ]);

    const out = [];

    (din?.fuentes || []).forEach(f => out.push({
        value: String(f.id),
        label: `[Dinámica] ${f.nombre} (id: ${f.id})`
    }));

    (est?.fuentes || []).forEach(f => out.push({
        value: String(f.fuenteId),
        label: `[Estática] ${f.nombre} (id: ${f.fuenteId})`
    }));

    (demo?.fuentes || []).forEach(f => out.push({
        value: String(f.id),
        label: `[Demo] ${f.nombre} (id: ${f.id})`
    }));

    (meta?.fuentes || []).forEach(f => out.push({
        value: String(f.id),
        label: `[MetaMapa] ${f.nombre} (id: ${f.id})`
    }));

    // ordenar por label
    out.sort((a, b) => a.label.localeCompare(b.label));

    _cacheFuentesTodas = out;
    return out;
}

async function poblarSelectsFuentesColecciones(rootEl) {
    const selects = rootEl.querySelectorAll("select.fuenteSelect");
    if (!selects.length) return;

    // placeholder loading
    selects.forEach(sel => {
        sel.disabled = true;
        sel.innerHTML = `<option value="">Cargando fuentes...</option>`;
    });

    try {
        const fuentes = await obtenerTodasLasFuentes();

        const optionsHtml = [
            `<option value="">Seleccioná una fuente...</option>`,
            ...fuentes.map(o => `<option value="${escapeHtml(o.value)}">${escapeHtml(o.label)}</option>`)
        ].join("");

        selects.forEach(sel => {
            sel.innerHTML = optionsHtml;
            sel.disabled = false;
        });
    } catch (e) {
        console.error("Error cargando fuentes:", e);
        selects.forEach(sel => {
            sel.innerHTML = `<option value="">Error al cargar fuentes</option>`;
            sel.disabled = true;
        });
    }
}

async function mostrarColecciones() {
    const contLista = document.getElementById("listaColecciones");
    contLista.innerHTML = "<p class='text-muted'>Cargando colecciones...</p>";

    try {
        const esAdmin = usuarioActual &&
            Array.isArray(usuarioActual.roles) &&
            usuarioActual.roles.includes("ADMINISTRADOR");

        const q = document.getElementById("busquedaColeccion")?.value?.trim() || "";
        const colecciones = await obtenerColecciones(q);

        if (!colecciones.length) {
            contLista.innerHTML = q
                ? `<p class="text-muted">No se encontraron colecciones para: <b>${escapeHtml(q)}</b></p>`
                : `<p class="text-muted">No hay colecciones para mostrar.</p>`;
            return;
        }

        contLista.innerHTML = colecciones.map(c => {
            // Extraemos los IDs de las fuentes directamente de los criterios
            const idsFuentes = [...new Set(
                (c.criterios || [])
                    .filter(crit => crit.idFuenteDeDatos)
                    .map(crit => crit.idFuenteDeDatos)
            )];

            // Renderizamos los badges solo con el ID disponible
            const badgesFuentes = idsFuentes.map(id => {
                return `<span class="badge bg-info text-dark border me-1 my-1">Fuente ${id}</span>`;
            }).join("");

            const htmlFuentes = badgesFuentes
                ? `<div class="mt-2"><small class="text-muted me-1">Fuentes:</small>${badgesFuentes}</div>`
                : `<div class="mt-2"><small class="text-muted fst-italic">Sin fuentes asignadas</small></div>`;

            return `
              <div class="card mb-2 p-2">
                <div class="d-flex justify-content-between align-items-center">
                  <div class="flex-grow-1">
                    <h6 class="fw-bold mb-1">Título: ${escapeHtml(c.titulo)}</h6>
                    <p class="small mb-0">Descripción: ${escapeHtml(c.descripcion)}</p>
                    <p class="small mb-0"><b>Consenso:</b> ${escapeHtml(c.consenso)}</p>
                    <p class="small mb-0"><b>ID:</b> ${escapeHtml(c.handle)}</p>
                    
                    ${htmlFuentes}

                    <p id="estadoColeccion_${escapeHtml(c.handle)}" class="small text-muted mb-0 mt-1"></p>
                  </div>

                  <div class="d-flex align-items-center gap-2 flex-wrap ms-2" style="min-width: fit-content;">
                    <button class="btn btn-sm btn-outline-primary btnVerHechos" data-handle="${escapeHtml(c.handle)}">
                      Ver hechos
                    </button>

                    ${ esAdmin ? `
                    <div class="input-group input-group-sm" style="width: 250px;">
                      <select class="form-select fuenteSelect" data-handle="${escapeHtml(c.handle)}">
                        <option value="">Cargando...</option>
                      </select>
                      <button class="btn btn-outline-secondary btnAgregarFuente" data-handle="${escapeHtml(c.handle)}">
                        Agregar
                      </button>
                    </div>
                    ` : '' }

                    ${ esAdmin ? `
                    <button class="btn btn-sm admin-only btn-outline-primary"
                            onclick="cambiarConsenso('${escapeHtml(c.handle)}')">
                      Cambiar consenso
                    </button>
                    ` : '' }
                    
                  </div>
                </div>
              </div>
            `;
        }).join("");
        if (esAdmin) {
            await poblarSelectsFuentesColecciones(contLista);
        }

        // DELEGACIÓN DE EVENTOS
        contLista.onclick = async (e) => {
            const btnVer = e.target.closest(".btnVerHechos");
            const btnFuente = e.target.closest(".btnAgregarFuente");

            // --- BOTÓN VER HECHOS ---
            if (btnVer) {
                const handle = btnVer.dataset.handle;
                const estado = document.getElementById(`estadoColeccion_${handle}`);

                // -- MANEJO DEL PANEL DE FILTROS --
                const panel = document.getElementById("panelFiltrosColeccion");
                const filtrosDiv = document.getElementById("filtrosContainerColeccion");
                const btnApp = document.getElementById("btnAplicarFiltrosColeccion");

                if (panel) {
                    // 1. Resetear el contenido de filtros (inputs)
                    if (filtrosDiv) filtrosDiv.innerHTML = "";
                    // 2. Deshabilitar botón "Aplicar"
                    if (btnApp) btnApp.disabled = true;
                    // 3. Mostrar panel y scrollear
                    panel.classList.remove("d-none");
                    panel.scrollIntoView({ behavior: 'smooth', block: 'center' });
                }
                // ---------------------------------

                setLoadingUI({ button: btnVer });
                setLoadingUI({ container: estado, message: "Buscando hechos..." });

                try {
                    await verHechosColeccion(handle);
                    setText(estado, "");
                } catch (err) {
                    console.error(err);
                    setText(estado, `Error: ${err?.message || err}`);
                } finally {
                    btnVer.disabled = false;
                    btnVer.textContent = "Ver hechos";
                }
                return;
            }

            // --- BOTÓN AGREGAR FUENTE (Solo Admin) ---
            if (btnFuente) {
                const handle = btnFuente.dataset.handle;
                const estado = document.getElementById(`estadoColeccion_${handle}`);

                const card = btnFuente.closest(".card");
                const sel = card?.querySelector(`select.fuenteSelect[data-handle="${handle}"]`);
                const fuenteValue = (sel?.value || "").trim();

                if (!fuenteValue) return alert("Seleccioná una fuente");

                setLoadingUI({ button: btnFuente });
                setLoadingUI({ container: estado, message: "Agregando fuente..." });

                try {
                    const idFuente = Number.isFinite(Number(fuenteValue)) ? Number(fuenteValue) : fuenteValue;
                    await agregarFuenteAColeccion(handle, idFuente);
                    setText(estado, "✅Fuente agregada.");
                    if (sel) sel.value = "";
                } catch (err) {
                    console.error(err);
                    setText(estado, `Error: ${err?.message || err}`);
                } finally {
                    btnFuente.disabled = false;
                    btnFuente.textContent = "Agregar";
                }
            }
        };

    } catch (e) {
        contLista.innerHTML = `<div class="alert alert-danger">Error al cargar colecciones</div>`;
        console.error(e);
    }
}


async function verHechosColeccion(idColeccion) {
    coleccionSeleccionada = idColeccion;
    const modo = document.getElementById("modoNav").value;
    const params = new URLSearchParams({ modoNavegacion: modo });
    const hechos = await obtenerHechosColeccionFiltrados(idColeccion, params);
    registrarCategoriasDesdeHechos(hechos);
    cargarCategorias();
    await ensureMapaInit("mapaColeccion");
    mostrarHechosEnMapa(hechos, "mapaColeccion");
}

async function aplicarFiltrosColeccion() {
    if (!coleccionSeleccionada) return mostrarModal("Seleccioná una colección primero.");

    const modo = document.getElementById("modoNav").value;
    const params = construirParametrosFiltros("panelFiltrosColeccion");
    params.append("modoNavegacion", modo);

    try {
        const hechos = await obtenerHechosColeccionFiltrados(coleccionSeleccionada, params);
        registrarCategoriasDesdeHechos(hechos);
        cargarCategorias();
        await ensureMapaInit("mapaColeccion");
        mostrarHechosEnMapa(hechos, "mapaColeccion");
    } catch (e) {
        console.error(e);
        mostrarModal("Error al aplicar filtros", "Error");
    }
}
window.aplicarFiltrosColeccion = aplicarFiltrosColeccion;

async function cambiarConsenso(id) {
    const opciones = [
        { label: "Mayoría simple", value: "MayoriaSimple" },
        { label: "Absoluto", value: "Absoluto" },
        { label: "Múltiples menciones", value: "MultiplesMenciones" }
    ];

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

    const overlay = document.createElement("div");
    overlay.style.cssText = `
    position: fixed; top: 0; left: 0; width: 100%; height: 100%;
    background: rgba(0,0,0,0.4); display: flex; justify-content: center; align-items: center;
    z-index: 2000;
  `;
    overlay.appendChild(div);
    document.body.appendChild(overlay);

    document.getElementById("cancelarCambio").onclick = () => overlay.remove();
    document.getElementById("confirmarCambio").onclick = async () => {
        const nuevoConsenso = document.getElementById("selectorConsenso").value;
        overlay.remove();
        try {
            const ok = await modificarConsensoColeccion(id, nuevoConsenso);
            if (ok) {
                mostrarModal("Consenso actualizado.");
                await mostrarColecciones();
            } else {
                mostrarModal("Error al cambiar consenso.", "Error");
            }
        } catch (e) {
            console.error(e);
            mostrarModal("Error de red al intentar cambiar consenso.", "Error");
        }
    };
}
window.cambiarConsenso = cambiarConsenso;

/* === Fuentes === */
async function mostrarFuentesView() {
    cont.innerHTML = "<p>Cargando fuentes...</p>";

    const [estaticas, dinamicas, demo, metamapa] = await Promise.all([
        obtenerFuentesEstaticas(),
        obtenerFuentesDinamicas(),
        obtenerFuentesDemo(),
        obtenerFuentesMetamapa()
    ]);

    let html = "";

    if (estaticas.disponible) {
        html += `
      <h3>Fuentes estáticas (${estaticas.fuentes.length})</h3>
      ${renderFuentesEstaticas(estaticas.fuentes)}
      <button class="btn btn-success mt-2" onclick="abrirModalFuente('estatica')">Crear fuente estática</button>

    `;
    }
    if (dinamicas.disponible) {
        html += `
      <h3 class="mt-4">Fuentes dinámicas (${dinamicas.fuentes.length})</h3>
      ${renderFuentesDinamicas(dinamicas.fuentes)}
     
      <button class="btn btn-success mt-2" onclick="abrirModalFuente('dinamica')">Crear fuente dinámica</button>

    `;
    }
    if (demo.disponible) {
        html += `
      <h3 class="mt-4">Fuentes demo (${demo.fuentes.length})</h3>
      ${renderFuentesDemo(demo.fuentes)}
      <button class="btn btn-success mt-2" onclick="crearFuenteDemoView()">Crear fuente demo</button>
    `;
    }
    if (metamapa.disponible) {
        html += `
      <h3 class="mt-4">Fuentes metamapa (${metamapa.fuentes.length})</h3>
      ${renderFuentesMetamapa(metamapa.fuentes)}
      <button class="btn btn-success mt-2" onclick="crearFuenteMetamapaView()">Crear fuente metamapa</button>
    `;
    }

    cont.innerHTML = html || `<p class="text-muted">No hay servicios disponibles</p>`;
}

function renderFuentesEstaticas(fuentes) {
    if (!fuentes.length) return `<p class="text-muted">No hay fuentes estáticas disponibles</p>`;
    const items = fuentes.map(f => `
    <li class="list-group-item d-flex justify-content-between align-items-start">
      <div>
        <strong>Nombre: ${escapeHtml(f.nombre)}</strong><br>
        <span>ID: ${escapeHtml(f.fuenteId)}</span><br>
      </div>
      <button class="btn btn-sm btn-primary" onclick="cargarCSV(${f.fuenteId})">Cargar CSV</button>
    </li>
  `).join("");
    return `<ul class="list-group">${items}</ul>`;
}

function renderFuentesDinamicas(fuentes) {
    if (!fuentes.length) return `<p class="text-muted">No hay fuentes dinámicas disponibles</p>`;
    const items = fuentes.map(f => `
    <li class="list-group-item">
      <strong>Nombre: ${escapeHtml(f.nombre)}</strong><br>
      <span>ID: ${escapeHtml(f.id)}</span><br>
    </li>
  `).join("");
    return `<ul class="list-group">${items}</ul>`;
}

function renderFuentesDemo(fuentes) {
    if (!fuentes.length) return `<p class="text-muted">No hay fuentes demo disponibles</p>`;
    return `<ul class="list-group">
    ${fuentes.map(f => `
      <li class="list-group-item">
        <strong>Nombre: ${escapeHtml(f.nombre)}</strong><br>
        <span>ID: ${escapeHtml(f.id)}</span><br>
        <span>URL: ${escapeHtml(f.endpointBase)}</span><br>
      </li>
    `).join("")}
  </ul>`;
}

function renderFuentesMetamapa(fuentes) {
    if (!fuentes.length) return `<p class="text-muted">No hay fuentes metamapa disponibles</p>`;
    return `<ul class="list-group">
    ${fuentes.map(f => `
      <li class="list-group-item">
        <strong>Nombre: ${escapeHtml(f.nombre)}</strong><br>
        <span>ID: ${escapeHtml(f.id)}</span><br>
        <span>URL: ${escapeHtml(f.endpointBase)}</span><br>
      </li>
    `).join("")}
  </ul>`;
}

async function crearFuenteEstaticaView() {
    const nombre = prompt("Ingrese el nombre de la fuente estática:");
    if (!nombre) return;
    const fuente = await crearFuenteEstatica(nombre);
    if (!fuente) return mostrarModal("No se pudo crear la fuente estática", "Error");
    mostrarModal("Fuente estática creada.", "Fuentes");
}

let modalCrearFuenteDinamica;

function crearFuenteDinamicaView() {
    const modalEl = document.getElementById("modalCrearFuenteDinamica");
    modalCrearFuenteDinamica = new bootstrap.Modal(modalEl, {
        backdrop: "static", // no se cierra clickeando afuera
        keyboard: false     // no se cierra con ESC
    });

    document.getElementById("inputNombreFuenteDinamica").value = "";
    modalCrearFuenteDinamica.show();
}

async function crearFuenteDemoView() {
    const nombre = prompt("Ingrese el nombre de la fuente demo:");
    if (!nombre) return;
    const url = prompt("Ingrese la URL de la fuente demo:");
    if (!url) return;
    const fuente = await crearFuenteDemo(nombre, url);
    if (!fuente) return mostrarModal("No se pudo crear la fuente demo", "Error");
    mostrarModal("Fuente demo creada.", "Fuentes");
}

async function crearFuenteMetamapaView() {
    const nombre = prompt("Ingrese el nombre de la fuente metamapa:");
    if (!nombre) return;
    const endpoint = prompt("Ingrese el endpoint de la fuente metamapa:");
    if (!endpoint) return;
    const fuente = await crearFuenteMetamapa(nombre, endpoint);
    if (!fuente) return mostrarModal("No se pudo crear la fuente metamapa", "Error");
    mostrarModal("Fuente metamapa creada.", "Fuentes");
}

window.crearFuenteEstaticaView = crearFuenteEstaticaView;
window.crearFuenteDinamicaView = crearFuenteDinamicaView;
window.crearFuenteDemoView = crearFuenteDemoView;
window.crearFuenteMetamapaView = crearFuenteMetamapaView;

/* === Solicitudes === */
function renderTablaSolicitudes(titulo, solicitudes, columnas, onApprove, onReject) {
    if (!solicitudes.length) {
        const alerta = document.createElement("div");
        alerta.className = "alert alert-info";
        alerta.textContent = `No hay solicitudes de ${titulo.toLowerCase()} pendientes.`;
        return alerta;
    }

    const tableWrapper = document.createElement("div");
    tableWrapper.innerHTML = `
    <h3>Solicitudes de ${escapeHtml(titulo)} (${solicitudes.length})</h3>
    <div class="table-responsive mb-4">
      <table class="table table-striped table-hover align-middle">
        <thead>
          <tr>${columnas.map(c => `<th>${escapeHtml(c.label)}</th>`).join("")}<th>Acciones</th></tr>
        </thead>
        <tbody>
          ${solicitudes.map(s => `
            <tr data-id="${escapeHtml(s.id)}" data-estado="${escapeHtml(s.estado)}">
              ${columnas.map(c => `<td>${escapeHtml(s[c.key] ?? "-")}</td>`).join("")}
              <td>
                <button class="btn btn-sm btn-success me-1 aprobar">Aprobar</button>
                <button class="btn btn-sm btn-danger rechazar">Rechazar</button>
              </td>
            </tr>`).join("")}
        </tbody>
      </table>
    </div>
  `;

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
        else onReject(id);
    });

    return tableWrapper;
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
            { key: "id", label: "ID" },
            { key: "hechoAfectado", label: "Hecho afectado" },
            { key: "motivo", label: "Motivo" },
            { key: "estado", label: "Estado" }
        ],
        procesarSolicitudEliminacion.bind(null, true),
        procesarSolicitudEliminacion.bind(null, false)
    ));

    cont.appendChild(renderTablaSolicitudes(
        "Edición",
        edic,
        [
            { key: "id", label: "ID" },
            { key: "hechoAfectado", label: "Hecho afectado" },
            { key: "tituloMod", label: "Título" },
            { key: "descMod", label: "Descripción" },
            { key: "categoriaMod", label: "Categoría" },
            { key: "latitudMod", label: "Latitud" },
            { key: "longitudMod", label: "Longitud" },
            { key: "fechaHechoMod", label: "Fecha" },
            { key: "sugerencia", label: "Sugerencia" },
            { key: "estado", label: "Estado" }
        ],
        procesarSolicitudEdicion.bind(null, true),
        procesarSolicitudEdicion.bind(null, false)
    ));
}

/* === Estadísticas (placeholder seguro) === */
async function mostrarEstadisticasView() {
    cont.innerHTML = `
    <div class="d-flex justify-content-between align-items-center flex-wrap gap-2 mb-3">
      <div>
        <h3 class="mb-0">Estadísticas</h3>
        <div class="text-muted small">Consultas rápidas y exportación a CSV.</div>
      </div>
      <button id="btnExportarCSV" class="btn btn-success btn-sm">Exportar CSV</button>
    </div>

    <div id="estadisticas-container" class="row g-3">
      <!-- Provincia por colección -->
      <div class="col-12 col-lg-6">
        <div class="card shadow-sm">
          <div class="card-body">
            <h6 class="card-title mb-2">Provincia con más hechos por colección</h6>
            <div class="input-group input-group-sm mb-2">
              <select id="coleccionSelect" class="form-select">
                <option value="">Cargando colecciones...</option>
              </select>
              <button id="btnBuscarProvinciaColeccion" class="btn btn-primary">Buscar</button>
            </div>
            <div class="small text-muted mb-1">Resultado</div>
            <div id="provinciaColeccion" class="fw-semibold">—</div>
          </div>
        </div>
      </div>

      <!-- Categoría más reportada -->
      <div class="col-12 col-lg-6">
        <div class="card shadow-sm">
          <div class="card-body">
            <h6 class="card-title mb-2">Categoría más reportada</h6>
            <div class="small text-muted mb-1">Resultado</div>
            <div id="categoriaMasReportada" class="fw-semibold">Cargando...</div>
          </div>
        </div>
      </div>

      <!-- Provincia por categoría -->
      <div class="col-12 col-lg-6">
        <div class="card shadow-sm">
          <div class="card-body">
            <h6 class="card-title mb-2">Provincia con más hechos de una categoría</h6>
            <div class="input-group input-group-sm mb-2">
              <select id="categoriaSelectProv" class="form-select">
                <option value="">Seleccioná una categoría...</option>
              </select>
              <button id="btnBuscarProvinciaCat" class="btn btn-primary">Buscar</button>
            </div>
            <div class="small text-muted mb-1">Resultado</div>
            <div id="provinciaCategoria" class="fw-semibold">—</div>
          </div>
        </div>
      </div>

      <!-- Hora por categoría -->
      <div class="col-12 col-lg-6">
        <div class="card shadow-sm">
          <div class="card-body">
            <h6 class="card-title mb-2">Hora del día con más hechos (por categoría)</h6>
            <div class="input-group input-group-sm mb-2">
              <select id="categoriaSelectHora" class="form-select">
                <option value="">Seleccioná una categoría...</option>
              </select>
              <button id="btnBuscarHoraCat" class="btn btn-primary">Buscar</button>
            </div>
            <div class="small text-muted mb-1">Resultado</div>
            <div id="horaCategoria" class="fw-semibold">—</div>
          </div>
        </div>
      </div>

      <!-- Spam -->
      <div class="col-12">
        <div class="card shadow-sm">
          <div class="card-body d-flex justify-content-between align-items-center flex-wrap gap-2">
            <div>
              <h6 class="card-title mb-1">Solicitudes de eliminación marcadas como spam</h6>
              <div class="text-muted small">Cantidad total</div>
            </div>
            <div id="cantidadSpam" class="fs-4 fw-bold">Cargando...</div>
          </div>
        </div>
      </div>
    </div>
  `;

    // Referencias
    const pCategoria = document.getElementById("categoriaMasReportada");
    const pSpam = document.getElementById("cantidadSpam");
    const selColeccion = document.getElementById("coleccionSelect");
    const selCatProv = document.getElementById("categoriaSelectProv");
    const selCatHora = document.getElementById("categoriaSelectHora");
    // helpers de UI para selects
    function setSelectOptions(selectEl, options, placeholder = "Seleccioná...") {
        if (!selectEl) return;
        selectEl.innerHTML =
            `<option value="">${placeholder}</option>` +
            options.map(o => `<option value="${escapeHtml(o.value)}">${escapeHtml(o.label)}</option>`).join("");
    }

    async function cargarSelectColecciones() {
        if (!selColeccion) return;
        selColeccion.disabled = true;
        selColeccion.innerHTML = `<option value="">Cargando colecciones...</option>`;

        try {
            const colecciones = await obtenerColecciones(""); // tu función ya existente
            const opts = (colecciones || []).map(c => ({
                value: c.handle,
                label: `${c.titulo} (${c.handle})`
            }));

            if (!opts.length) {
                selColeccion.innerHTML = `<option value="">No hay colecciones</option>`;
                return;
            }

            setSelectOptions(selColeccion, opts, "Seleccioná una colección...");
        } catch (e) {
            console.error("Error cargando colecciones:", e);
            selColeccion.innerHTML = `<option value="">Error al cargar colecciones</option>`;
        } finally {
            selColeccion.disabled = false;
        }
    }

    function cargarSelectCategoriasDesdeHechos() {
        const cats = categoriasDisponibles(); // viene de tu set armado desde hechos
        const opts = cats.map(c => ({ value: c, label: c }));
        setSelectOptions(selCatProv, opts, "Seleccioná una categoría...");
        setSelectOptions(selCatHora, opts, "Seleccioná una categoría...");
    }

    async function refrescarEstadisticasBase() {
        // UI loading
        setLoadingUI({ container: pCategoria, message: "Cargando categoría más reportada..." });
        setLoadingUI({ container: pSpam, message: "Cargando cantidad de spam..." });

        try {
            const [categoriaMasReportada, spam] = await Promise.all([
                obtenerCategoriaMasReportada(),
                obtenerCantidadSolicitudesSpam()
            ]);

            setText(pCategoria, categoriaMasReportada || "No hay datos");
            setText(pSpam, String(spam));
        } catch (e) {
            console.error("Error refrescando estadísticas:", e);
            setText(pCategoria, "Error al cargar");
            setText(pSpam, "Error al cargar");
        }
    }

// 1) Traigo hechos para construir categorías reales
    try {
        const hechos = await obtenerHechos(); // tu función existente
        registrarCategoriasDesdeHechos(hechos);
    } catch (e) {
        console.warn("No se pudieron cargar hechos para categorías:", e);
    }

// 2) Cargo selects desde hechos
    cargarSelectCategoriasDesdeHechos();

// 3) resto
    await cargarSelectColecciones();
    await refrescarEstadisticasBase();




    // eventos
    document.getElementById("btnBuscarProvinciaColeccion").addEventListener("click", async (e) => {
        const btn = e.currentTarget;
        const uuid = (selColeccion?.value || "").trim();
        if (!uuid) return alert("Seleccioná una colección");

        const p = document.getElementById("provinciaColeccion");
        setLoadingUI({ container: p, message: "Buscando...", button: btn });

        try {
            const provincia = await obtenerProvinciaMasReportadaColeccion(uuid);
            setText(p, provincia || "No hay datos disponibles");
        } catch {
            setText(p, "Error al buscar");
        } finally {
            setDoneUI(btn);
        }
    });

    document.getElementById("btnBuscarProvinciaCat").addEventListener("click", async (e) => {
        const btn = e.currentTarget;
        const cat = (selCatProv?.value || "").trim();
        if (!cat) return alert("Seleccioná una categoría");

        const p = document.getElementById("provinciaCategoria");
        setLoadingUI({ container: p, message: "Buscando...", button: btn });

        try {
            const prov = await obtenerProvinciaMasReportadaPorCategoria(cat);
            setText(p, prov || "No hay datos disponibles");
        } catch {
            setText(p, "Error al buscar");
        } finally {
            setDoneUI(btn);
        }
    });

    document.getElementById("btnBuscarHoraCat").addEventListener("click", async (e) => {
        const btn = e.currentTarget;
        const cat = (selCatHora?.value || "").trim();
        if (!cat) return alert("Seleccioná una categoría");

        const p = document.getElementById("horaCategoria");
        setLoadingUI({ container: p, message: "Buscando...", button: btn });

        try {
            const hora = await obtenerHoraMasReportadaPorCategoria(cat);
            setText(p, hora !== null ? `${hora}:00 hs` : "No hay datos disponibles");
        } catch {
            setText(p, "Error al buscar");
        } finally {
            setDoneUI(btn);
        }
    });

    async function obtenerProvinciaMasReportadaColeccion(uuid) {
        try {
            const response = await fetch(`${window.METAMAPA.API_ESTADISTICA}/coleccion/${uuid}/provincia-mas-reportada`);
            if (response.status === 204) return null; // No hay datos
            const data = await response.json();
            return data.provincia;
        } catch (error) {
            console.error("Error al obtener la provincia más reportada de la colección:", error);
            return null;
        }
    }

// ¿Cuál es la categoría con mayor cantidad de hechos reportados?
    async function obtenerCategoriaMasReportada() {
        try {
            const response = await fetch(`${window.METAMAPA.API_ESTADISTICA}/categoria`);
            if (response.status === 204) return null;
            const data = await response.json();
            return data.categoria;
        } catch (error) {
            console.error("Error al obtener la categoría más reportada:", error);
            return null;
        }
    }

// ¿En qué provincia se presenta la mayor cantidad de hechos de una cierta categoría?
    async function obtenerProvinciaMasReportadaPorCategoria(categoria) {
        try {
            const params = new URLSearchParams({ categoria });
            const response = await fetch(`${window.METAMAPA.API_ESTADISTICA}/hechos/provincia-mas-reportada?${params}`);
            if (response.status === 204) return null; // No hay datos
            const data = await response.json();
            return data.provincia;
        } catch (error) {
            console.error("Error al obtener la provincia más reportada por categoría:", error);
            return null;
        }
    }

//  ¿A qué hora del día ocurren la mayor cantidad de hechos de una cierta categoría?
    async function obtenerHoraMasReportadaPorCategoria(categoria) {
        try {
            const params = new URLSearchParams({ categoria });
            const response = await fetch(`${window.METAMAPA.API_ESTADISTICA}/hechos/hora?${params}`);
            if (response.status === 204) return null;
            const data = await response.json();
            return parseInt(data.hora, 10);
        } catch (error) {
            console.error("Error al obtener la hora más reportada por categoría:", error);
            return null;
        }
    }

// ¿Cuántas solicitudes de eliminación son spam?
    async function obtenerCantidadSolicitudesSpam() {
        try {
            const response = await fetch(`${window.METAMAPA.API_ESTADISTICA}/spam`);
            if (!response.ok) throw new Error("Error al obtener solicitudes spam");
            const data = await response.json();
            return parseInt(data.cantidadSolicitudesSpam) || 0;
        } catch (error) {
            console.error("Error al contar solicitudes spam:", error);
            return 0;
        }
    }
    // Exportar CSV
    document.getElementById("btnExportarCSV").addEventListener("click", () => {
        const btn = document.getElementById("btnExportarCSV");
        btn.disabled = true;
        btn.dataset.originalText ??= btn.textContent;
        btn.textContent = "Generando…";

        try {
            const coleccionLabel = getSelectedText(selColeccion);
            const categoriaProvLabel = getSelectedText(selCatProv);
            const categoriaHoraLabel = getSelectedText(selCatHora);

            const datos = [
                ["ESTADÍSTICA", "CONTEXTO", "VALOR"],

                [
                    "Provincia con más hechos por colección",
                    coleccionLabel || "—",
                    document.getElementById("provinciaColeccion").textContent.trim()
                ],

                [
                    "Categoría más reportada",
                    "General",
                    document.getElementById("categoriaMasReportada").textContent.trim()
                ],

                [
                    "Provincia con más hechos de una categoría",
                    categoriaProvLabel || "—",
                    document.getElementById("provinciaCategoria").textContent.trim()
                ],

                [
                    "Hora del día con más hechos (por categoría)",
                    categoriaHoraLabel || "—",
                    document.getElementById("horaCategoria").textContent.trim()
                ],

                [
                    "Solicitudes de eliminación marcadas como spam",
                    "General",
                    document.getElementById("cantidadSpam").textContent.trim()
                ]
            ];

            const csv = datos
                .map(fila => fila.map(v => `"${String(v).replace(/"/g, '""')}"`).join(";"))
                .join("\r\n");

            const bom = "\uFEFF";
            const blob = new Blob([bom + csv], { type: "text/csv;charset=utf-8;" });
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
            btn.textContent = btn.dataset.originalText || "Exportar CSV";
        }
    });
}
function getSelectedText(selectEl) {
    if (!selectEl) return "";
    const opt = selectEl.options[selectEl.selectedIndex];
    return opt ? opt.text.trim() : "";
}

/* =========================================================
   Filtros dinámicos (hechos / colecciones)
   ========================================================= */
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
        <button class="btn btn-outline-danger btn-sm" type="button" onclick="this.closest('div.p-2').remove()">Quitar</button>
      </div>
    </div>

    <div class="row mt-2 g-2 d-none camposUbicacion">
      <div class="col-md-3"><input type="number" step="any" class="form-control latitud" placeholder="Latitud" readonly></div>
      <div class="col-md-3"><input type="number" step="any" class="form-control longitud" placeholder="Longitud" readonly></div>
      <div class="col-md-3"><input type="number" step="0.1" class="form-control radio" placeholder="Radio (km)" readonly></div>
      <div class="col-md-3"><button class="btn btn-sm btn-outline-success w-100" type="button" onclick="abrirMapaUbicacion(this)">Seleccionar</button></div>
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
window.agregarFiltro = agregarFiltro;

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

async function aplicarFiltrosHechos() {
    const params = construirParametrosFiltros("panelFiltrosHechos");
    const btn = document.getElementById("btnAplicarFiltrosHechos");
    const tabla = document.getElementById("tablaHechos");

    setLoadingUI({ button: btn });

    if (tabla) {
        tabla.innerHTML = `
      <div class="text-muted mb-2">
        <span class="spinner" style="display:inline-block; vertical-align:middle;"></span>
        <span style="margin-left:8px;">Aplicando filtros...</span>
      </div>
      ${crearSkeletonTablaHechos(8)}
    `;
        await new Promise(r => requestAnimationFrame(r));
    }

    try {
        const hechos = await obtenerHechos(params);
        registrarCategoriasDesdeHechos(hechos);
        cargarCategorias();
        await ensureMapaInit("mapa");
        mostrarHechosEnMapa(hechos, "mapa");
        tabla.innerHTML = renderTablaHechos("Hechos filtrados", hechos);
    } catch (e) {
        console.error(e);
        tabla.innerHTML = `<div class="alert alert-danger">Error al aplicar filtros: ${escapeHtml(e?.message || e)}</div>`;
    } finally {
        if (btn) {
            btn.disabled = false;
            btn.textContent = "Aplicar filtros";
        }
    }
}
window.aplicarFiltrosHechos = aplicarFiltrosHechos;

/* =========================================================
   Criterios (modal colección)
   ========================================================= */
function actualizarCamposCriterio(div, tipo) {
    const camposMap = {
        "fecha": ".campos-fecha",
        "fechareportaje": ".campos-fecha",
        "fuente": ".campos-fuente",
        "ubicacion": ".campos-ubicacion",
        "multimedia": ".campos-multimedia"
    };

    Object.values(camposMap).forEach(selector => {
        const el = div.querySelector(selector);
        if (el) el.classList.add("d-none");
    });

    const selectorAMostrar = camposMap[tipo];
    if (selectorAMostrar) div.querySelector(selectorAMostrar)?.classList.remove("d-none");
}

function poblarSelectCategorias(selectEl, selected = "") {
    if (!selectEl) return;

    const cats = categoriasDisponibles(); // <- sale del Set dinámico

    selectEl.innerHTML =
        `<option value="">Seleccioná una categoría...</option>` +
        cats.map(c => {
            const sel = (String(c) === String(selected)) ? "selected" : "";
            return `<option value="${escapeHtml(c)}" ${sel}>${escapeHtml(c)}</option>`;
        }).join("");
}


async function poblarSelectFuentes(selectEl, selectedValue = "") {
    if (!selectEl) return;

    selectEl.disabled = true;
    selectEl.innerHTML = `<option value="">Cargando fuentes...</option>`;

    try {
        const fuentes = await obtenerTodasLasFuentes(); // [{value,label},...]
        selectEl.innerHTML =
            `<option value="">Seleccioná una fuente...</option>` +
            fuentes.map(f => {
                const sel = (String(f.value) === String(selectedValue)) ? "selected" : "";
                return `<option value="${escapeHtml(f.value)}" ${sel}>${escapeHtml(f.label)}</option>`;
            }).join("");
    } catch (e) {
        console.error(e);
        selectEl.innerHTML = `<option value="">Error al cargar fuentes</option>`;
    } finally {
        selectEl.disabled = false;
    }
}
// Cache simple para no pedir hechos/fuentes 20 veces

let _fuentesCache = null;

let _catsCargadas = false;
let _catsPromise = null;

async function ensureCategoriasDesdeHechos() {
    if (_catsCargadas) return;
    if (_catsPromise) return _catsPromise;

    _catsPromise = (async () => {
        const hechos = await obtenerHechos();
        registrarCategoriasDesdeHechos(hechos); // que sume al Set, no lo reemplace
        _catsCargadas = true;
    })().finally(() => {
        _catsPromise = null;
    });

    return _catsPromise;
}


async function ensureFuentes() {
    if (_fuentesCache) return _fuentesCache;
    _fuentesCache = await obtenerTodasLasFuentes(); // [{value,label},...]
    return _fuentesCache;
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

      <div class="col-md-4 valor-col">
        <label class="form-label">Valor</label>

        <!-- Este input es el que lee armarCriterio() -->
        <input type="text" name="valor" class="form-control valor-text" placeholder="Valor o texto">

        <!-- Select solo para UI de categoría -->
        <select class="form-select valor-categoria d-none"></select>
      </div>

      <div class="col-md-3">
        <label class="form-label">Incluir</label>
        <select name="inclusion" class="form-select">
          <option value="true" selected>Incluir</option>
          <option value="false">Excluir</option>
        </select>
      </div>

      <div class="col-md-1 d-flex align-items-end">
        <button type="button" class="btn btn-outline-danger btn-sm" onclick="this.closest('.criterio-box').remove()">X</button>
      </div>
    </div>

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
        <label>Fuente</label>
        <select name="idFuenteDeDatos" class="form-select fuente-select">
          <option value="">Cargando fuentes...</option>
        </select>
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
        <button type="button" class="btn btn-outline-success w-100" onclick="abrirMapaUbicacion(this)">
          Seleccionar en mapa
        </button>
      </div>
    </div>

    <div class="row mb-2 campos-multimedia d-none">
      <div class="col">
        <label>Tipo de multimedia</label>
        <select name="tipoMultimedia" class="form-select">
          <option value="FOTO">FOTO</option>
          <option value="VIDEO">VIDEO</option>
          <option value="AUDIO">AUDIO</option>
        </select>
      </div>
    </div>
  `;

    // refs
    const tipoSelect = div.querySelector(".tipo-criterio");
    const valorCol = div.querySelector(".valor-col");
    const inputValor = div.querySelector(".valor-text");
    const selCategoria = div.querySelector(".valor-categoria");
    const selFuente = div.querySelector('select[name="idFuenteDeDatos"]');

    // cuando elijo una categoría, la guardo en el input name="valor"
    selCategoria.addEventListener("change", () => {
        inputValor.value = selCategoria.value || "";
    });

    function syncUIByTipo(tipo) {
        // base
        valorCol.classList.remove("d-none");
        inputValor.classList.remove("d-none");
        selCategoria.classList.add("d-none");

        actualizarCamposCriterio(div, tipo);

        if (tipo === "categoria") {
            inputValor.classList.add("d-none");
            selCategoria.classList.remove("d-none");
        }

        if (tipo === "fuente" || tipo === "ubicacion" || tipo === "multimedia" || tipo === "fecha" || tipo === "fechareportaje") {
            valorCol.classList.add("d-none");
        }
    }

    tipoSelect.addEventListener("change", () => syncUIByTipo(tipoSelect.value));

    // precarga si viene existente
    if (criterioExistente) {
        tipoSelect.value = criterioExistente.tipo;
        div.querySelector('[name="inclusion"]').value = criterioExistente.inclusion ? "true" : "false";

        // valor (si no es categoría)
        inputValor.value = criterioExistente.valor || "";

        if (criterioExistente.fechaDesde) div.querySelector('[name="fechaDesde"]').value = criterioExistente.fechaDesde;
        if (criterioExistente.fechaHasta) div.querySelector('[name="fechaHasta"]').value = criterioExistente.fechaHasta;

        if (criterioExistente.idFuenteDeDatos != null) selFuente.value = String(criterioExistente.idFuenteDeDatos);

        if (criterioExistente.latitud != null) div.querySelector('[name="latitud"]').value = criterioExistente.latitud;
        if (criterioExistente.longitud != null) div.querySelector('[name="longitud"]').value = criterioExistente.longitud;
        if (criterioExistente.radio != null) div.querySelector('[name="radio"]').value = criterioExistente.radio;
        if (criterioExistente.tipoMultimedia) div.querySelector('[name="tipoMultimedia"]').value = criterioExistente.tipoMultimedia;
    }

    // UI inicial
    syncUIByTipo(tipoSelect.value);
    selCategoria.disabled = true;
    selCategoria.innerHTML = `<option value="">Cargando categorías...</option>`;

    // Cargar categorías y fuentes (async) sin romper la UI
    (async () => {
        await ensureCategoriasDesdeHechos();

        poblarSelectCategorias(
            selCategoria,
            criterioExistente?.tipo === "categoria" ? (criterioExistente?.valor || "") : ""
        );

        selCategoria.disabled = false;

        if (criterioExistente?.tipo === "categoria") {
            selCategoria.value = criterioExistente.valor || "";
            inputValor.value = selCategoria.value || "";
        }

        // fuentes (igual que ahora)
        try {
            const fuentes = await ensureFuentes();
            selFuente.innerHTML =
                `<option value="">Seleccioná una fuente...</option>` +
                fuentes.map(f => {
                    const sel = (String(f.value) === String(criterioExistente?.idFuenteDeDatos ?? "")) ? "selected" : "";
                    return `<option value="${escapeHtml(f.value)}" ${sel}>${escapeHtml(f.label)}</option>`;
                }).join("");
        } catch (e) {
            selFuente.innerHTML = `<option value="">Error al cargar fuentes</option>`;
        }
    })();


    container.appendChild(div);
}



window.agregarCriterio = agregarCriterio;

function armarCriterio(div) {
    const get = n => div.querySelector(`[name="${n}"]`)?.value?.trim() || null;
    const num = n => {
        const v = get(n);
        if (v === null || v === "") return null;
        const x = parseFloat(v);
        return Number.isFinite(x) ? x : null;
    };

    const tipo = get("tipo");

    // valor: si es categoria, viene del select .valor-categoria (no del input)
    let valor = get("valor");
    if (tipo === "categoria") {
        valor = div.querySelector(".valor-categoria")?.value?.trim() || null;
    }

    const criterio = {
        tipo,
        valor,
        inclusion: get("inclusion") === "true"
    };

    if (get("fechaDesde")) criterio.fechaDesde = get("fechaDesde");
    if (get("fechaHasta")) criterio.fechaHasta = get("fechaHasta");

    // fuente: se guarda SI o SI en idFuenteDeDatos
    const idFuenteStr = get("idFuenteDeDatos");
    if (idFuenteStr) criterio.idFuenteDeDatos = parseInt(idFuenteStr, 10);

    const lat = num("latitud");
    const lon = num("longitud");
    const radio = num("radio");

    if (lat !== null) criterio.latitud = lat;
    if (lon !== null) criterio.longitud = lon;
    if (radio !== null) criterio.radio = radio;

    if (get("tipoMultimedia")) criterio.tipoMultimedia = get("tipoMultimedia");

    // opcional: si es fuente, no mandes "valor" para evitar ruido
    if (tipo === "fuente") criterio.valor = null;

    return criterio;
}


/* =========================================================
   Detalle y solicitudes desde detalle (mínimo)
   ========================================================= */
function mostrarDetalleHecho(h) {
    const modalEl = document.getElementById("modalDetalle");
    const modal = bootstrap.Modal.getOrCreateInstance(modalEl);
    const contenedor = document.getElementById("detalleHecho");
    renderDetalleHechoView(h, contenedor);
    const mediaCont = contenedor.querySelector("#multimediaHecho");
    cargarYRenderizarMultimedia(h, mediaCont);
    modal.show();
}
window.mostrarDetalleHecho = mostrarDetalleHecho;

function renderDetalleHechoView(h, contenedor) {
    const multimedia = Array.isArray(h.multimedia) ? h.multimedia : [];

    const baseFD = String(window.METAMAPA.API_FUENTE_DINAMICA || "").replace(/\/$/, "");
    const urlArchivo = (m) => `${baseFD}/archivos/${encodeURIComponent(m.path || "")}`;

    const renderMediaItem = (m) => {
        const tipo = (m.tipoMultimedia || "").toUpperCase();
        const fileUrl = urlArchivo(m);
        const nombre = escapeHtml(m.path || "archivo");

        if (tipo === "FOTO") {
            return `
              <div class="col-12 col-md-6">
                <div class="border rounded p-2 bg-white">
                  <img
                    src="${fileUrl}"
                    alt="${nombre}"
                    class="img-fluid rounded w-100"
                    style="max-height: 340px; object-fit: cover;"
                    loading="lazy"
                  />
                  <div class="d-flex justify-content-between align-items-center mt-2">
                    <span class="small text-muted">Foto</span>
                    <a class="small" href="${fileUrl}" target="_blank" rel="noopener">Abrir</a>
                  </div>
                </div>
              </div>`;
        }

        if (tipo === "VIDEO") {
            return `
              <div class="col-12">
                <div class="border rounded p-2 bg-white">
                  <video controls class="w-100 rounded" style="max-height: 420px;">
                    <source src="${fileUrl}" type="video/mp4" />
                    Tu navegador no soporta la reproducción de video.
                  </video>
                  <div class="d-flex justify-content-between align-items-center mt-2">
                    <span class="small text-muted">Video</span>
                    <a class="small" href="${fileUrl}" target="_blank" rel="noopener">Abrir</a>
                  </div>
                </div>
              </div>`;
        }

        // AUDIO u otros
        return `
          <div class="col-12">
            <div class="border rounded p-2 bg-white d-flex justify-content-between align-items-center">
              <div>
                <div class="fw-semibold">${escapeHtml(m.tipoMultimedia || "Archivo")}</div>
                <div class="small text-muted text-truncate" style="max-width: 520px;">${nombre}</div>
              </div>
              <a class="btn btn-sm btn-outline-primary" href="${fileUrl}" target="_blank" rel="noopener">
                Abrir
              </a>
            </div>
          </div>`;
    };

    const mediaHTML = multimedia.length
        ? `<div class="row g-2 mt-1">${multimedia.map(renderMediaItem).join("")}</div>`
        : `<div class="text-muted fst-italic">Sin archivos multimedia</div>`;

    contenedor.innerHTML = `
      <div class="container-fluid">
        <div class="d-flex flex-wrap justify-content-between align-items-start gap-2">
          <div>
            <h4 class="mb-1">${escapeHtml(h.titulo || "Sin título")}</h4>
            <div class="d-flex flex-wrap gap-2">
              <span class="badge text-bg-secondary">${escapeHtml(h.categoria || "—")}</span>
              <span class="badge text-bg-light border">Fuente: ${escapeHtml(h.idFuente ?? "—")}</span>
              <span class="badge text-bg-light border">ID: ${escapeHtml(h.id ?? "—")}</span>
            </div>
          </div>
        </div>

        <hr class="my-3"/>

        <div class="row g-3">
          <div class="col-12 col-lg-6">
            <div class="mb-2"><b>Descripción:</b> ${escapeHtml(h.descripcion || "—")}</div>
            <div class="small text-muted">
              <div><b>Fecha hecho:</b> ${escapeHtml(h.fechaHecho || "—")}</div>
              <div><b>Fecha carga:</b> ${escapeHtml(h.fechaCarga || "—")}</div>
              <div><b>Fecha modificación:</b> ${escapeHtml(h.fechaModificacion || "—")}</div>
            </div>

            <hr class="my-3"/>

            <h6 class="mb-2">Multimedia</h6>
            ${mediaHTML}
          </div>

          <div class="col-12 col-lg-6">
            <div class="border rounded p-3 bg-white">
              <div><b>Latitud:</b> ${escapeHtml(h.latitud ?? "—")}</div>
              <div><b>Longitud:</b> ${escapeHtml(h.longitud ?? "—")}</div>
              <div class="mt-2"><b>Anónimo:</b> ${h.anonimo ? "Sí" : "No"}</div>
              <div><b>Eliminado:</b> ${h.eliminado ? "Sí" : "No"}</div>

              <hr class="my-3"/>

              <div class="small text-muted"><b>usuarioId:</b> ${escapeHtml(h.usuarioId || "Sin usuario")}</div>
              <div class="small text-muted"><b>Consensos:</b> ${
        Array.isArray(h.consensos) && h.consensos.length
            ? escapeHtml(h.consensos.join(", "))
            : "Ninguno"
    }</div>
            </div>

            <div class="d-flex justify-content-end gap-2 mt-3">
              <button id="btnSolicitarEdicion" class="btn btn-warning">Solicitar edición</button>
              <button id="btnSolicitarEliminacion" class="btn btn-danger">Solicitar eliminación</button>
            </div>
          </div>
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
    const cats = categoriasDisponibles();
    if (h.categoria && !cats.includes(h.categoria)) cats.unshift(h.categoria);
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
<select id="categoriaMod" class="form-select">
  ${cats.map(c => `
    <option value="${escapeHtml(c)}" ${c === (h.categoria || "") ? "selected" : ""}>
      ${escapeHtml(c)}
    </option>
  `).join("")}
</select>
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
        <div class="mb-2">
          <label class="form-label"><b>Seleccionar ubicación en el mapa</b></label>
          <div id="mapaEdicionSolicitud" class="border rounded" style="height:260px;"></div>
          <div class="form-text">Click en el mapa o arrastrá el marcador.</div>
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
    setTimeout(() => {
        const lat = (h.latitud != null) ? Number(h.latitud) : NaN;
        const lng = (h.longitud != null) ? Number(h.longitud) : NaN;
        inicializarMapaEdicionSolicitud(lat, lng);
    }, 150);

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
        alert(ok ? "✅ Solicitud de edición enviada con éxito." : "Error al enviar la solicitud.");
        renderDetalleHechoView(h, contenedor);
    });
}

/* =========================================================
   Sesión / roles (SSO real - componente usuarios)
   ========================================================= */
// Elementos fijos de la interfaz (navbar)
const btnLogin = document.getElementById("btnLogin");
const btnLogout = document.getElementById("btnLogout");
const nombreUsuarioDisplay = document.getElementById("nombreUsuarioDisplay");

// Helpers de roles
function esContribuyenteOAdmin(roles) {
    return roles.includes("CONTRIBUYENTE") || roles.includes("ADMINISTRADOR");
}
function esAdministrador(roles) {
    return roles.includes("ADMINISTRADOR");
}

// UI basada en roles
let rolesUsuarioActual = [];

function actualizarVisibilidadPorRoles(roles, nombre) {
    if (roles) rolesUsuarioActual = roles;

    const isContribOrAdmin = esContribuyenteOAdmin(rolesUsuarioActual);
    const isAdminRole = esAdministrador(rolesUsuarioActual);

    const btnsVisualizador = document.querySelectorAll(".visualizador-level");
    const btnsLoginRequired = document.querySelectorAll(".login-required, .contribuyente-level");
    const btnsAdminOnly = document.querySelectorAll(".admin-only"); // (sin espacio adelante)
    const chip = document.getElementById("userChip");
    if (chip) chip.classList.remove("d-none");


    // Login/Logout
    if (btnLogin) btnLogin.classList.add("d-none");
    if (btnLogout) btnLogout.classList.remove("d-none");

    // Nombre
    if (nombreUsuarioDisplay) {
        nombreUsuarioDisplay.textContent = nombre || "Usuario";
    }

    // Visualizador siempre visible (siempre lo ven)
    btnsVisualizador.forEach(btn => btn.classList.remove("d-none"));

    // Contribuyente o Admin
    btnsLoginRequired.forEach(btn => {
        btn.classList.toggle("d-none", !isContribOrAdmin);
    });

    // Solo Admin
    btnsAdminOnly.forEach(btn => {
        btn.classList.toggle("d-none", !isAdminRole);
    });
}

function ocultarTodoYMostrarLogin() {
    const btnsLoginRequired = document.querySelectorAll(".login-required, .contribuyente-level");
    const btnsAdminOnly = document.querySelectorAll(".admin-only");
    const chip = document.getElementById("userChip");
    if (chip) chip.classList.add("d-none");

    btnsLoginRequired.forEach(btn => btn.classList.add("d-none"));
    btnsAdminOnly.forEach(btn => btn.classList.add("d-none"));


    if (btnLogin) btnLogin.classList.remove("d-none");
    if (btnLogout) btnLogout.classList.add("d-none");

    if (nombreUsuarioDisplay) nombreUsuarioDisplay.textContent = "";
}

// Reusa tu variable global del archivo (ya la usás para idUsuario al crear hecho)
async function verificarSesionYActualizarUI() {
    try {
        const resp = await fetch(`${window.METAMAPA.API_USUARIOS}/api-auth/me`, {
            credentials: "include"
        });

        if (resp.ok) {
            usuarioActual = await resp.json();
            const roles = usuarioActual.roles || [];
            actualizarVisibilidadPorRoles(roles, usuarioActual.nombre);
        } else {
            usuarioActual = null;
            ocultarTodoYMostrarLogin();
        }
    } catch (e) {
        usuarioActual = null;
        ocultarTodoYMostrarLogin();
    }
}

function iniciarSesionSSO() {
    window.location.href = `${window.METAMAPA.API_USUARIOS}/login`;
}

function cerrarSesion() {
    fetch(`${window.METAMAPA.API_USUARIOS}/logout`, {
        method: "POST",
        credentials: "include"
    })
        .catch(err => console.error("Error al hacer logout:", err))
        .finally(() => {
            ocultarTodoYMostrarLogin();
            window.location.href = "http://localhost:9000/index.html";
            // window.location.href = `${window.location.origin}/index.html`;
        });
}

window.iniciarSesionSSO = iniciarSesionSSO;
window.cerrarSesion = cerrarSesion;

/* =========================================================
   Formularios / init
   ========================================================= */
function limpiarFormularioHecho() {
    const form = document.getElementById("formHecho");
    if (!form) return;
    form.reset();
    document.getElementById("latitud").value = "";
    document.getElementById("longitud").value = "";
    const input = document.getElementById("inputMultimedia");
    if (input) input.value = "";
    const res = document.getElementById("resultadoHecho");
    if (res) res.textContent = "";
}

function limpiarFormularioColeccion() {
    const form = document.getElementById("formColeccion");
    if (!form) return;
    form.reset();
    document.getElementById("criteriosContainer").innerHTML = "";
    document.getElementById("modalColeccionTitle").innerText = "Nueva colección";
    const res = document.getElementById("resultadoColeccion");
    if (res) res.textContent = "";
}

document.addEventListener("DOMContentLoaded", async () => {
    console.log("Iniciando MetaMapa...");

    const formHecho = document.getElementById("formHecho");
    const formColeccion = document.getElementById("formColeccion");
    const modalHecho = document.getElementById("modalHecho");
    const modalColeccion = document.getElementById("modalColeccion");

    const categoriaInput = document.getElementById("categoriaSelect");
    categoriaInput.addEventListener("input", () => {
        const valor = categoriaInput.value.trim();
        const ok = (valor === "" || categoriasDisponibles().includes(valor));
        categoriaInput.classList.toggle("is-invalid", !ok);
        categoriaInput.classList.toggle("is-valid", ok && valor !== "");
    });

    if (formHecho) formHecho.addEventListener("submit", crearHecho);
    if (formColeccion) formColeccion.addEventListener("submit", crearColeccion);
    const categoriasReady = ensureCategoriasDesdeHechos().catch(() => {});
    if (modalHecho) {
        modalHecho.addEventListener("shown.bs.modal", async () => {
            await categoriasReady;   // en vez de llamar ensureCategoriasDesdeHechos() otra vez
            cargarCategorias();
            await cargarSelectFuentesDinamicas();
            setTimeout(inicializarMapaSeleccion, 200);
        });

        modalHecho.addEventListener("hidden.bs.modal", () => {
            limpiarMapaSeleccion();
            limpiarFormularioHecho();
        });
    }

    if (modalColeccion) {
        modalColeccion.addEventListener("hidden.bs.modal", limpiarFormularioColeccion);
    }

    await verificarSesionYActualizarUI();
    ensureCategoriasDesdeHechos().catch(() => {});

    const vista = sessionStorage.getItem("vistaActual") || "colecciones";
    await mostrar(vista);
    marcarNavActiva(vista);

});

function esUrlAbsoluta(u) {
    return /^https?:\/\//i.test(String(u || ""));
}

function resolverUrlMultimedia(item) {
    const u = item?.url || item?.uri || item?.href || item?.path || item?.ruta || item?.src || "";
    if (!u) return null;

    if (esUrlAbsoluta(u)) return u;

    // Si viene como "/algo", asumimos que vive en el servicio de la fuente dinámica (ajustá si en tu back es otro)
    const base = (window.METAMAPA?.API_FUENTE_DINAMICA || "").replace(/\/$/, "");
    if (!base) return u;

    return u.startsWith("/") ? `${base}${u}` : `${base}/${u}`;
}

function tipoMultimedia(item) {
    const t = (item?.tipo || item?.tipoMultimedia || item?.mimeType || "").toString().toUpperCase();
    if (t.includes("IMAGE") || t.includes("FOTO") || t.includes("JPG") || t.includes("PNG") || t.includes("JPEG") || t.includes("WEBP")) return "FOTO";
    if (t.includes("VIDEO") || t.includes("MP4") || t.includes("WEBM")) return "VIDEO";
    if (t.includes("AUDIO") || t.includes("MP3") || t.includes("WAV") || t.includes("OGG")) return "AUDIO";
    return item?.tipoMultimedia || item?.tipo || null;
}

async function obtenerMultimediaHecho(hecho) {
    // 1) si ya vino embebida en el hecho, usarla
    if (Array.isArray(hecho?.multimedia) && hecho.multimedia.length) return hecho.multimedia;
    if (Array.isArray(hecho?.archivos) && hecho.archivos.length) return hecho.archivos;

    // 2) si no vino, intentar endpoints típicos (no rompe si no existen)
    const id = hecho?.id;
    if (!id) return [];

    const base = (window.METAMAPA?.API_AGREGADOR || "").replace(/\/$/, "");
    const candidatos = [
        `${base}/hechos/${id}/multimedia`,
        `${base}/hechos/${id}`, // por si el detalle trae multimedia
    ];

    for (const url of candidatos) {
        try {
            const r = await fetch(url, { credentials: "include" });
            if (!r.ok) continue;
            const json = await r.json();

            if (Array.isArray(json)) return json;
            if (Array.isArray(json?.multimedia)) return json.multimedia;
            if (Array.isArray(json?.archivos)) return json.archivos;
        } catch {
            // seguir probando
        }
    }

    return [];
}

function renderizarMultimedia(list, cont) {
    if (!cont) return;

    const arr = Array.isArray(list) ? list : [];
    if (!arr.length) {
        cont.innerHTML = `<div class="text-muted small">Sin archivos adjuntos.</div>`;
        return;
    }

    cont.innerHTML = arr.map((m, idx) => {
        const url = resolverUrlMultimedia(m);
        if (!url) {
            return `<div class="col-12"><div class="text-muted small">Archivo ${idx + 1}: sin URL</div></div>`;
        }

        const tipo = tipoMultimedia(m) || "ARCHIVO";
        const nombre = (m?.nombre || m?.filename || m?.originalName || `Archivo ${idx + 1}`).toString();

        if (tipo === "FOTO") {
            return `
        <div class="col-6 col-md-4">
          <a href="${url}" target="_blank" rel="noopener">
            <img src="${url}" class="img-fluid rounded border" alt="${escapeHtml(nombre)}">
          </a>
        </div>`;
        }

        if (tipo === "VIDEO") {
            return `
        <div class="col-12 col-md-6">
          <video class="w-100 rounded border" controls preload="metadata" src="${url}"></video>
          <div class="small text-muted mt-1">${escapeHtml(nombre)}</div>
        </div>`;
        }

        if (tipo === "AUDIO") {
            return `
        <div class="col-12">
          <audio class="w-100" controls src="${url}"></audio>
          <div class="small text-muted mt-1">${escapeHtml(nombre)}</div>
        </div>`;
        }

        // fallback: link
        return `
      <div class="col-12">
        <a href="${url}" target="_blank" rel="noopener">${escapeHtml(nombre)}</a>
      </div>`;
    }).join("");
}

async function cargarYRenderizarMultimedia(hecho, cont) {
    if (!cont) return;
    cont.innerHTML = `<div class="text-muted small">Cargando multimedia...</div>`;

    const lista = await obtenerMultimediaHecho(hecho);
    renderizarMultimedia(lista, cont);
}
let mapaEdicionSolicitud = null;
let marcadorEdicionSolicitud = null;

function actualizarInputsLatLngMod(lat, lng) {
    const inLat = document.getElementById("latitudMod");
    const inLng = document.getElementById("longitudMod");
    if (inLat) inLat.value = Number(lat).toFixed(6);
    if (inLng) inLng.value = Number(lng).toFixed(6);
}

function colocarMarcadorEdicionSolicitud(lat, lng) {
    if (!mapaEdicionSolicitud) return;

    if (!marcadorEdicionSolicitud) {
        marcadorEdicionSolicitud = L.marker([lat, lng], { draggable: true })
            .addTo(mapaEdicionSolicitud)
            .on("drag dragend", (ev) => {
                const p = ev.target.getLatLng();
                actualizarInputsLatLngMod(p.lat, p.lng);
            });
    } else {
        marcadorEdicionSolicitud.setLatLng([lat, lng]);
    }

    actualizarInputsLatLngMod(lat, lng);
}

function inicializarMapaEdicionSolicitud(latInicial, lngInicial) {
    const el = document.getElementById("mapaEdicionSolicitud");
    if (!el) return;

    // Si el div fue recreado (innerHTML), el mapa viejo queda pegado a un container viejo: destruir
    if (mapaEdicionSolicitud && mapaEdicionSolicitud._container !== el) {
        try { mapaEdicionSolicitud.remove(); } catch {}
        mapaEdicionSolicitud = null;
        marcadorEdicionSolicitud = null;
    }

    if (!mapaEdicionSolicitud) {
        const lat0 = Number.isFinite(latInicial) ? latInicial : -34.61;
        const lng0 = Number.isFinite(lngInicial) ? lngInicial : -58.38;

        mapaEdicionSolicitud = L.map("mapaEdicionSolicitud").setView([lat0, lng0], 12);

        L.tileLayer("https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png", {
            attribution: "&copy; OpenStreetMap contributors"
        }).addTo(mapaEdicionSolicitud);

        mapaEdicionSolicitud.on("click", (e) => {
            colocarMarcadorEdicionSolicitud(e.latlng.lat, e.latlng.lng);
        });
    }

    const lat = Number.isFinite(latInicial) ? latInicial : mapaEdicionSolicitud.getCenter().lat;
    const lng = Number.isFinite(lngInicial) ? lngInicial : mapaEdicionSolicitud.getCenter().lng;

    mapaEdicionSolicitud.setView([lat, lng], 12);
    colocarMarcadorEdicionSolicitud(lat, lng);

    setTimeout(() => mapaEdicionSolicitud.invalidateSize(true), 0);
}
let _modalFuente = null;

function abrirModalFuente(tipo) {
    const modalEl = document.getElementById("modalFuente");
    if (!modalEl) {
        console.error("No existe #modalFuente en el HTML");
        return;
    }

    _modalFuente = bootstrap.Modal.getOrCreateInstance(modalEl);

    // refs
    const title = document.getElementById("modalFuenteTitle");
    const subtitle = document.getElementById("modalFuenteSubtitle");
    const inTipo = document.getElementById("fuenteTipo");
    const inNombre = document.getElementById("fuenteNombre");
    const inUrl = document.getElementById("fuenteUrl");
    const inEndpoint = document.getElementById("fuenteEndpoint");
    const gUrl = document.getElementById("grupoFuenteUrl");
    const gEndpoint = document.getElementById("grupoFuenteEndpoint");
    const err = document.getElementById("fuenteError");

    // reset UI
    err.classList.add("d-none");
    err.textContent = "";
    inTipo.value = tipo;
    inNombre.value = "";
    inUrl.value = "";
    inEndpoint.value = "";

    // modo según tipo
    gUrl.classList.toggle("d-none", tipo !== "demo");
    gEndpoint.classList.toggle("d-none", tipo !== "metamapa");

    if (tipo === "dinamica") {
        title.textContent = "Crear fuente dinámica";
        subtitle.textContent = "Se crea en el servicio de fuentes dinámicas.";
    } else if (tipo === "estatica") {
        title.textContent = "Crear fuente estática";
        subtitle.textContent = "Luego podés cargar un CSV en la fuente.";
    } else if (tipo === "demo") {
        title.textContent = "Crear fuente demo";
        subtitle.textContent = "Requiere una URL.";
    } else if (tipo === "metamapa") {
        title.textContent = "Crear fuente metamapa";
        subtitle.textContent = "Requiere un endpoint.";
    } else {
        title.textContent = "Crear fuente";
        subtitle.textContent = "Completá los datos.";
    }

    _modalFuente.show();

    // foco
    setTimeout(() => inNombre?.focus(), 150);
}

window.abrirModalFuente = abrirModalFuente;

// submit (una sola vez)
document.addEventListener("DOMContentLoaded", () => {
    const form = document.getElementById("formFuente");
    if (!form) return;

    form.addEventListener("submit", async (e) => {
        e.preventDefault();

        const tipo = document.getElementById("fuenteTipo").value;
        const nombre = (document.getElementById("fuenteNombre").value || "").trim();
        const url = (document.getElementById("fuenteUrl").value || "").trim();
        const endpoint = (document.getElementById("fuenteEndpoint").value || "").trim();

        const btn = document.getElementById("btnCrearFuente");
        const loader = document.getElementById("fuenteLoader");
        const btnText = document.getElementById("fuenteBtnText");
        const err = document.getElementById("fuenteError");

        err.classList.add("d-none");
        err.textContent = "";

        if (!nombre) {
            err.textContent = "El nombre es obligatorio.";
            err.classList.remove("d-none");
            return;
        }
        if (tipo === "demo" && !url) {
            err.textContent = "La URL es obligatoria para una fuente demo.";
            err.classList.remove("d-none");
            return;
        }
        if (tipo === "metamapa" && !endpoint) {
            err.textContent = "El endpoint es obligatorio para una fuente metamapa.";
            err.classList.remove("d-none");
            return;
        }

        btn.disabled = true;
        loader.classList.remove("d-none");
        btnText.textContent = "Creando...";

        try {
            let ok = false;

            if (tipo === "dinamica") ok = !!(await crearFuenteDinamica(nombre));
            else if (tipo === "estatica") ok = !!(await crearFuenteEstatica(nombre));
            else if (tipo === "demo") ok = !!(await crearFuenteDemo(nombre, url));
            else if (tipo === "metamapa") ok = !!(await crearFuenteMetamapa(nombre, endpoint));

            if (ok) {
                _modalFuente?.hide();

                // refresco suave de la vista fuentes (sin mensajes)
                try { await mostrar("fuentes"); } catch {}
            } else {
                err.textContent = "No se pudo crear la fuente (respuesta no OK).";
                err.classList.remove("d-none");
            }
        } catch (ex) {
            console.error(ex);
            err.textContent = "Error de red o del servidor al crear la fuente.";
            err.classList.remove("d-none");
        } finally {
            btn.disabled = false;
            loader.classList.add("d-none");
            btnText.textContent = "Crear";
        }
    });
});

function marcarNavActiva(seccion) {
    // Limpia activos
    document.querySelectorAll('.mm-nav [data-seccion].is-active')
        .forEach(el => el.classList.remove('is-active'));

    // Marca el que coincide
    const btn = document.querySelector(`.mm-nav [data-seccion="${seccion}"]`);
    if (btn) btn.classList.add('is-active');

    // Si estás en fuentes/solicitudes, marcá también el toggle Admin
    const adminToggle = document.getElementById('btnAdminMenu');
    if (adminToggle) {
        const esAdminSeccion = (seccion === 'fuentes' || seccion === 'solicitudes');
        adminToggle.classList.toggle('is-active', esAdminSeccion);
    }
}

document.addEventListener("DOMContentLoaded", () => {

    document
        .getElementById("btnConfirmarCrearFuenteDinamica")
        .addEventListener("click", async () => {
            const nombre = document
                .getElementById("inputNombreFuenteDinamica")
                .value
                .trim();

            if (!nombre) return;

            const fuente = await crearFuenteDinamica(nombre);

            if (!fuente) {
                mostrarModal("No se pudo crear la fuente dinámica", "Error");
                return;
            }

            modalCrearFuenteDinamica.hide();
            mostrarModal("Fuente dinámica creada.", "Fuentes");
        });

});

async function agregarFuenteAColeccion(id, idFuente) {
    const url = `${window.METAMAPA.API_COLECCIONES}/colecciones/${id}/fuentes/${idFuente}`;
    const resp = await fetch(url, { method: "POST" });
    if (!resp.ok) throw new Error(await resp.text());
    return true;
}

async function resolverIdFuentePorNombre(nombre) {
    const n = (nombre || "").trim().toLowerCase();
    if (!n) return null;

    const [est, dyn, demo, meta] = await Promise.all([
        obtenerFuentesEstaticas(),
        obtenerFuentesDinamicas(),
        obtenerFuentesDemo(),
        obtenerFuentesMetamapa()
    ]);

    // Estáticas: fuenteId
    if (est?.disponible) {
        const f = est.fuentes.find(x => (x.nombre || "").trim().toLowerCase() === n);
        if (f) return f.fuenteId;
    }

    // Dinámicas: id
    if (dyn?.disponible) {
        const f = dyn.fuentes.find(x => (x.nombre || "").trim().toLowerCase() === n);
        if (f) return f.id;
    }

    // Demo: id
    if (demo?.disponible) {
        const f = demo.fuentes.find(x => (x.nombre || "").trim().toLowerCase() === n);
        if (f) return f.id;
    }

    // Metamapa: id
    if (meta?.disponible) {
        const f = meta.fuentes.find(x => (x.nombre || "").trim().toLowerCase() === n);
        if (f) return f.id;
    }

    return null;
}