// === Visualización de estadísticas MetaMapa ===
console.log("✅ stats js cargado correctamente");
// --- Funciones auxiliares ---
const getEstadisticas = async () => (await fetch(`${API_BASE}/estadisticas`)).json();

const crearChart = (id, type, label, data, extraOptions = {}) => {
    const ctx = document.getElementById(id);
    if (!ctx || !data) return;
    new Chart(ctx, {
        type,
        data: {
            labels: Object.keys(data),
            datasets: [{ label, data: Object.values(data) }]
        },
        options: { responsive: true, plugins: { legend: { display: false } }, ...extraOptions }
    });
};

// Muestra gráficos dentro del contenedor principal
async function renderEstadisticas() {
    const cont = document.getElementById("contenido");
    cont.innerHTML = `<h2>Estadísticas</h2>
    <div class="charts">
      <canvas id="chartCategorias"></canvas>
      <canvas id="chartProvincias"></canvas>
      <canvas id="chartSolicitudes"></canvas>
    </div>`;

    try {
        const data = await getEstadisticas();

        // 1. Hechos por categoría
        crearChart("chartCategorias", "bar", "Hechos por categoría", data.hechosPorCategoria);
        // 2. Hechos por provincia
        crearChart("chartProvincias", "pie", "Hechos por provincia", data.hechosPorProvincia, {
            plugins: { legend: { display: true } }
        });

        // 3. Solicitudes de eliminación (spam / válidas)
        if (data.solicitudes) {
            const { spam = 0, validas = 0 } = data.solicitudes;
            new Chart(document.getElementById("chartSolicitudes"), {
                type: "doughnut",
                data: {
                    labels: ["Spam", "Válidas"],
                    datasets: [{
                        data: [spam, validas],
                        backgroundColor: ["#dc3545", "#28a745"]
                    }]
                },
                options: { responsive: true }
            });
        }
    } catch (err) {
        cont.innerHTML = `<div class="error">Error cargando estadísticas: ${err}</div>`;
    }
}