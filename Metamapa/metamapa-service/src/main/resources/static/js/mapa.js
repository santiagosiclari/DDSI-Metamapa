console.log("mapa.js cargado correctamente");

let mapa, markersLayer, legendDiv;
// Paleta de colores por categoría
const categoriaColores = {
    Delito: "red",
    Accidente: "orange",
    Emergencia: "blue",
    Clima: "purple",
    Otro: "green"
};

const colorPorCategoria = cat => categoriaColores[cat] || categoriaColores.Otro;

// ==============================
// MAPA PRINCIPAL
// ==============================
function inicializarMapa(divId = "mapa") {
    const cont = document.getElementById(divId);
    if (!cont) return setTimeout(() => inicializarMapa(divId), 200);

    mapa?.remove(); // si ya existe, eliminar
    mapa = L.map(divId).setView([-34.61, -58.38], 11);
    L.tileLayer("https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png", {
        attribution: "&copy; OpenStreetMap contributors"
    }).addTo(mapa);

    markersLayer = L.layerGroup().addTo(mapa);
    console.log("🗺️ Mapa inicializado nuevamente.");
}

const limpiarMarcadores = () => markersLayer?.clearLayers();

function agregarMarcador(hecho) {
    const { latitud, longitud, categoria, titulo, descripcion, fechaHecho } = hecho;
    if (!latitud || !longitud) return;

    const color = colorPorCategoria(hecho.categoria);
    const icono = L.divIcon({
        html: `<div style="background:${color}; width:16px; height:16px; border-radius:50%; border:2px solid white"></div>`,
        className: ""
    });

    const popup = `
    <b>${titulo}</b><br>${descripcion}<br>
    <small>${categoria || "Sin categoría"} | ${fechaHecho}</small>
  `;
    const marker = L.marker([latitud, longitud], { icon: icono })
        .bindPopup(popup)
        .on("click", () => mostrarDetalleHecho(hecho));
    marker.addTo(markersLayer);
}

function mostrarHechosEnMapa(hechos) {
    limpiarMarcadores();
    hechos.forEach(agregarMarcador);
    actualizarLeyenda(hechos);

    const { latitud, longitud } = hechos[0] || {};
    if (latitud)
        mapa.setView([latitud, longitud], 11);
}

// === Leyenda dinámica ===
function agregarLeyenda() {
    legendDiv = L.control({ position: "bottomright" });
    legendDiv.onAdd = function () {
        this._div = L.DomUtil.create("div", "mapa-leyenda");
        this._div.innerHTML = "<strong>Leyenda</strong><br><small>Sin datos</small>";
        return this._div;
    };
    legendDiv.addTo(mapa);
}

function actualizarLeyenda(hechos) {
    if (!legendDiv?._div) return;
    const cats = [...new Set(hechos.map(h => h.categoria || "Otro"))];
    legendDiv._div.innerHTML = cats.length
        ? `<strong>Leyenda</strong><br>${cats
            .map(c => `
          <div style="display:flex;align-items:center;margin-bottom:2px">
            <div style="background:${colorPorCategoria(c)};width:12px;height:12px;border-radius:50%;margin-right:6px"></div>${c}
          </div>`)
            .join("")}`
        : "<strong>Leyenda</strong><br><small>Sin hechos</small>";
}

// ==========================
// Mapa selector para crear hecho
// ==========================
let mapaSeleccion, marcadorSeleccion;

function inicializarMapaSeleccion() {
    // Evitar reinicialización múltiple
    if (mapaSeleccion)
       return setTimeout(() => mapaSeleccion.invalidateSize(), 200);

    // Crear mapa base
    mapaSeleccion = L.map("mapaSeleccion").setView([-34.61, -58.38], 11);

    L.tileLayer("https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png", {
        attribution: "&copy; OpenStreetMap contributors"
    }).addTo(mapaSeleccion);

    // Intentar centrar en ubicación del usuario
    navigator.geolocation?.getCurrentPosition(({ coords }) =>
        mapaSeleccion.setView([coords.latitude, coords.longitude], 13)
    );

    // Al hacer clic, agregar o mover marcador
    mapaSeleccion.on("click", e => colocarMarcadorSeleccion(e.latlng.lat, e.latlng.lng));
}

// Crear o mover marcador y actualizar inputs
function colocarMarcadorSeleccion(lat, lng) {
    // Si no existe el marcador, crearlo
    if (!marcadorSeleccion) {
        marcadorSeleccion = L.marker([lat, lng], { draggable: true }).addTo(mapaSeleccion)
            .on("drag dragend", ev => {
                const { lat, lng } = ev.target.getLatLng();
                actualizarInputsLatLng(lat, lng);
            });
    } else {
        marcadorSeleccion.setLatLng([lat, lng]);
    }

    actualizarInputsLatLng(lat, lng);
}

// Actualiza los campos del formulario con la posición actual
const actualizarInputsLatLng = (lat, lng) => {
    document.getElementById("latitud").value = lat.toFixed(6);
    document.getElementById("longitud").value = lng.toFixed(6);
};

// Limpia el marcador actual (si existe)
const limpiarMapaSeleccion = () => {
    if (mapaSeleccion && marcadorSeleccion) {
        mapaSeleccion.removeLayer(marcadorSeleccion);
        marcadorSeleccion = null;
    }
};
// =============================
// MAPA DE UBICACIÓN (criterios con radio)
// =============================
let mapaUbicacion, marcadorUbicacion, circuloUbicacion;
let radioActual = 5; // km

// Contexto temporal para guardar los inputs de destino
let _mapaUbicacionContext = null;

// ==================================================
// Seleccionar ubicación en mapa (genérica)
// ==================================================
function abrirMapaUbicacion(boton) {
    try {
        // Buscar el contenedor más cercano que tenga inputs de coordenadas
        const parent =
            boton.closest(".criterio-box") ||
            boton.closest(".p-2") ||
            boton.closest(".row") ||
            document;

        // Buscar los inputs donde se guardarán los valores
        const latInput =
            parent.querySelector("input[name='latitud']") ||
            parent.querySelector(".latitud");
        const lonInput =
            parent.querySelector("input[name='longitud']") ||
            parent.querySelector(".longitud");
        const radioInput =
            parent.querySelector("input[name='radio']") ||
            parent.querySelector(".radio");

        // Guardar referencias globales para usarlas al confirmar
        _mapaUbicacionContext = { latInput, lonInput, radioInput };

        // Mostrar modal
        const modal = new bootstrap.Modal(document.getElementById("modalUbicacion"));
        modal.show();

        // Inicializar mapa con pequeño retardo
        setTimeout(() => {
            if (!mapaUbicacion) inicializarMapaUbicacion();
            else mapaUbicacion.invalidateSize();
        }, 200);
    } catch (err) {
        console.error("Error al abrir mapa de ubicación:", err);
    }
}

function confirmarUbicacion() {
    const mapaCont = document.getElementById("mapaUbicacion");
    const lat = parseFloat(mapaCont.dataset.lat);
    const lng = parseFloat(mapaCont.dataset.lng);
    const radio = parseFloat(document.getElementById("radioSlider").value);

    if (isNaN(lat) || isNaN(lng)) {
        alert("Seleccioná una ubicación en el mapa.");
        return;
    }

    // Asignar a los campos detectados
    if (_mapaUbicacionContext) {
        const { latInput, lonInput, radioInput } = _mapaUbicacionContext;
        if (latInput) latInput.value = lat.toFixed(6);
        if (lonInput) lonInput.value = lng.toFixed(6);
        if (radioInput) radioInput.value = radio.toFixed(1);
    }

    bootstrap.Modal.getInstance(document.getElementById("modalUbicacion")).hide();
}

// Confirmar selección de ubicación
function inicializarMapaUbicacion() {
    // Crear mapa solo una vez
    if (!mapaUbicacion) {
        mapaUbicacion = L.map("mapaUbicacion").setView([-34.61, -58.38], 11);

        // Capa base
        L.tileLayer("https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png", {
            attribution: "&copy; OpenStreetMap contributors"
        }).addTo(mapaUbicacion);

        // Clic en mapa → colocar marcador
        mapaUbicacion.on("click", e => {
            const { lat, lng } = e.latlng;
            colocarMarcadorUbicacion(lat, lng);
        });

        // Control del slider de radio
        const slider = document.getElementById("radioSlider");
        slider.addEventListener("input", e => {
            radioActual = parseFloat(e.target.value);
            document.getElementById("radioLabel").innerText = `${radioActual} km`;
            actualizarCirculoUbicacion();
        });
    }

    // Esperar a que el modal termine de mostrarse antes de redibujar
    setTimeout(() => mapaUbicacion.invalidateSize(), 300);
}

function colocarMarcadorUbicacion(lat, lng) {
    if (!marcadorUbicacion) {
        marcadorUbicacion = L.marker([lat, lng], { draggable: true }).addTo(mapaUbicacion);
        marcadorUbicacion.on("drag", e => {
            const { lat, lng } = e.target.getLatLng();
            actualizarCirculoUbicacion(lat, lng);
            // Actualizar dataset al arrastrar
            const cont = document.getElementById("mapaUbicacion");
            cont.dataset.lat = lat;
            cont.dataset.lng = lng;
        });
    } else {
        marcadorUbicacion.setLatLng([lat, lng]);
    }
    actualizarCirculoUbicacion(lat, lng);
    // Guardar coordenadas para confirmarUbicacion()
    const cont = document.getElementById("mapaUbicacion");
    cont.dataset.lat = lat;
    cont.dataset.lng = lng;
}

function actualizarCirculoUbicacion(lat, lng) {
    if (!lat && marcadorUbicacion) ({ lat, lng } = marcadorUbicacion.getLatLng());
    if (!lat) return;

    const radiusMeters = radioActual * 1000;
    circuloUbicacion?.remove();
    circuloUbicacion = L.circle([lat, lng], {
        radius: radiusMeters,
        color: "green",
        fillColor: "rgba(0,200,0,0.2)",
        fillOpacity: 0.4
    }).addTo(mapaUbicacion);

    mapaUbicacion.setView([lat, lng], 12);
}