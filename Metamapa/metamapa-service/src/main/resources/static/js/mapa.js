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
