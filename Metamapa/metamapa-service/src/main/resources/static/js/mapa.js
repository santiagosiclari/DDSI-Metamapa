let mapa;
let markersLayer;
let legendDiv;

// Paleta de colores por categoría
const categoriaColores = {
    "Delito": "red",
    "Accidente": "orange",
    "Emergencia": "blue",
    "Clima": "purple",
    "Otro": "green"
};

function colorPorCategoria(cat) {
    if (!cat) return categoriaColores["Otro"];
    return categoriaColores[cat] || categoriaColores["Otro"];
}

function inicializarMapa(divId = "mapa") {
    mapa = L.map(divId).setView([-34.61, -58.38], 11);
    L.tileLayer("https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png", {
        attribution: '&copy; OpenStreetMap contributors'
    }).addTo(mapa);
    markersLayer = L.layerGroup().addTo(mapa);
    agregarLeyenda();
}

function limpiarMarcadores() {
    if (markersLayer) markersLayer.clearLayers();
}

function agregarMarcador(hecho) {
    if (!hecho.latitud || !hecho.longitud) return;

    const color = colorPorCategoria(hecho.categoria);
    const icono = L.divIcon({
        html: `<div style="background:${color}; width:16px; height:16px; border-radius:50%; border:2px solid white"></div>`,
        className: ""
    });

    const popup = `
    <b>${hecho.titulo}</b><br>
    ${hecho.descripcion}<br>
    <small>${hecho.categoria || "Sin categoría"} | ${hecho.fechaHecho}</small>
  `;
    const marker = L.marker([hecho.latitud, hecho.longitud], { icon: icono })
        .bindPopup(popup)
        .on("click", () => mostrarDetalleHecho(hecho));
    marker.addTo(markersLayer);
}

function mostrarHechosEnMapa(hechos) {
    limpiarMarcadores();
    hechos.forEach(agregarMarcador);
    actualizarLeyenda(hechos);

    if (hechos.length > 0 && hechos[0].latitud) {
        mapa.setView([hechos[0].latitud, hechos[0].longitud], 11);
    }
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
    if (!legendDiv || !legendDiv._div) return;
    const cats = [...new Set(hechos.map(h => h.categoria || "Otro"))];
    if (!cats.length) {
        legendDiv._div.innerHTML = "<strong>Leyenda</strong><br><small>Sin hechos</small>";
        return;
    }

    legendDiv._div.innerHTML = `
    <strong>Leyenda</strong><br>
    ${cats.map(c => {
        const color = colorPorCategoria(c);
        return `<div style="display:flex;align-items:center;margin-bottom:2px">
                <div style="background:${color};width:12px;height:12px;border-radius:50%;margin-right:6px"></div>
                ${c}
              </div>`;
    }).join("")}
  `;
}
// ==========================
// 🗺️ Mapa selector para crear hecho
// ==========================
let mapaSeleccion;
let marcadorSeleccion;

function inicializarMapaSeleccion() {
    // Evitar reinicialización múltiple
    if (mapaSeleccion) {
        setTimeout(() => mapaSeleccion.invalidateSize(), 200);
        return;
    }

    // Crear mapa base
    mapaSeleccion = L.map("mapaSeleccion").setView([-34.61, -58.38], 11);

    L.tileLayer("https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png", {
        attribution: '&copy; OpenStreetMap contributors'
    }).addTo(mapaSeleccion);

    // Intentar centrar en ubicación del usuario
    if (navigator.geolocation) {
        navigator.geolocation.getCurrentPosition(pos => {
            const { latitude, longitude } = pos.coords;
            mapaSeleccion.setView([latitude, longitude], 13);
        });
    }

    // Al hacer clic, agregar o mover marcador
    mapaSeleccion.on("click", function (e) {
        const { lat, lng } = e.latlng;
        colocarMarcadorSeleccion(lat, lng);
    });
}

// Crear o mover marcador y actualizar inputs
function colocarMarcadorSeleccion(lat, lng) {
    // Si no existe el marcador, crearlo
    if (!marcadorSeleccion) {
        marcadorSeleccion = L.marker([lat, lng], { draggable: true }).addTo(mapaSeleccion);
        marcadorSeleccion.on("drag", function (ev) {
            const { lat, lng } = ev.latlng;
            actualizarInputsLatLng(lat, lng);
        });
        marcadorSeleccion.on("dragend", function (ev) {
            const { lat, lng } = ev.target.getLatLng();
            actualizarInputsLatLng(lat, lng);
        });
    } else {
        marcadorSeleccion.setLatLng([lat, lng]);
    }

    actualizarInputsLatLng(lat, lng);
}

// Actualiza los campos del formulario con la posición actual
function actualizarInputsLatLng(lat, lng) {
    document.getElementById("latitud").value = lat.toFixed(6);
    document.getElementById("longitud").value = lng.toFixed(6);
}

// Limpia el marcador actual (si existe)
function limpiarMapaSeleccion() {
    if (mapaSeleccion && marcadorSeleccion) {
        mapaSeleccion.removeLayer(marcadorSeleccion);
        marcadorSeleccion = null;
    }
}
// =============================
// 🌍 MAPA DE UBICACIÓN (criterios)
// =============================
let mapaUbicacion;
let marcadorUbicacion;
let circuloUbicacion;
let radioActual = 5; // km
let inputLatDestino, inputLonDestino;

function abrirMapaUbicacion(btn) {
    // Guardar inputs de destino del criterio actual
    const parent = btn.closest(".criterio-box");
    inputLatDestino = parent.querySelector('[name="latitud"]');
    inputLonDestino = parent.querySelector('[name="longitud"]');

    const modal = new bootstrap.Modal(document.getElementById("modalUbicacion"));
    modal.show();

    setTimeout(() => inicializarMapaUbicacion(), 300);
}

function inicializarMapaUbicacion() {
    if (!mapaUbicacion) {
        mapaUbicacion = L.map("mapaUbicacion").setView([-34.61, -58.38], 11);

        L.tileLayer("https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png", {
            attribution: '&copy; OpenStreetMap contributors'
        }).addTo(mapaUbicacion);

        // Click para seleccionar centro
        mapaUbicacion.on("click", e => {
            const { lat, lng } = e.latlng;
            colocarMarcadorUbicacion(lat, lng);
        });

        // Slider de radio
        const slider = document.getElementById("radioSlider");
        slider.addEventListener("input", e => {
            radioActual = parseInt(e.target.value);
            document.getElementById("radioLabel").innerText = `${radioActual} km`;
            actualizarCirculoRadio();
        });
    }

    setTimeout(() => mapaUbicacion.invalidateSize(), 300);
}

function colocarMarcadorUbicacion(lat, lng) {
    if (marcadorUbicacion) {
        marcadorUbicacion.setLatLng([lat, lng]);
    } else {
        marcadorUbicacion = L.marker([lat, lng], { draggable: true }).addTo(mapaUbicacion);
        marcadorUbicacion.on("drag", e => {
            const pos = e.target.getLatLng();
            actualizarCirculoRadio(pos.lat, pos.lng);
        });
    }
    actualizarCirculoRadio(lat, lng);
}

function actualizarCirculoRadio(lat, lng) {
    if (!lat && marcadorUbicacion) {
        const pos = marcadorUbicacion.getLatLng();
        lat = pos.lat; lng = pos.lng;
    }
    if (!lat) return;

    const radiusMeters = radioActual * 1000; // km -> m
    if (circuloUbicacion) mapaUbicacion.removeLayer(circuloUbicacion);

    circuloUbicacion = L.circle([lat, lng], {
        radius: radiusMeters,
        color: "green",
        fillColor: "rgba(0,200,0,0.2)",
        fillOpacity: 0.4
    }).addTo(mapaUbicacion);

    mapaUbicacion.setView([lat, lng], 12);
}

function confirmarUbicacion() {
    if (!marcadorUbicacion) {
        alert("Debes seleccionar un punto en el mapa.");
        return;
    }

    const { lat, lng } = marcadorUbicacion.getLatLng();
    inputLatDestino.value = lat.toFixed(6);
    inputLonDestino.value = lng.toFixed(6);

    const modal = bootstrap.Modal.getInstance(document.getElementById("modalUbicacion"));
    modal.hide();
}
