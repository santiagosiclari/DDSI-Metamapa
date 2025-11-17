// js/auth.js

// Clases de elementos de la interfaz
const loginRequired = document.querySelectorAll('.login-required, .contribuyente-level'); // Combina ambas clases para Contribuyente/Admin
const adminOnly = document.querySelectorAll('.admin-only'); // Funciones exclusivas de Admin
const btnLogin = document.getElementById('btnLogin');
const btnLogout = document.getElementById('btnLogout');
const nombreUsuarioDisplay = document.getElementById('nombreUsuarioDisplay'); // Asumiendo un elemento para mostrar el nombre

// --- Helpers de Roles ---

function esContribuyenteOAdmin(roles) {
    // Si el array de roles incluye CONTRIBUYENTE o ADMINISTRADOR
    return roles.includes("CONTRIBUYENTE") || roles.includes("ADMINISTRADOR");
}

function esAdministrador(roles) {
    // Si el array de roles incluye ADMINISTRADOR
    return roles.includes("ADMINISTRADOR");
}


// --- Funciones de Actualización de UI (Basadas en Roles) ---

function actualizarVisibilidadPorRoles(roles, nombre) {
    const isContribOrAdmin = esContribuyenteOAdmin(roles);
    const isAdminRole = esAdministrador(roles);

    // 1. Visibilidad de Login/Logout
    if (btnLogin) btnLogin.classList.add('d-none');
    if (btnLogout) btnLogout.classList.remove('d-none');

    // Opcional: Mostrar nombre de usuario
    if (nombreUsuarioDisplay) {
        nombreUsuarioDisplay.textContent = nombre || 'Usuario';
    }

    // 2. Controlar botones de Contribuyente/Admin (Hechos, +Hecho, Mis Solicitudes)
    loginRequired.forEach(btn => {
        if (isContribOrAdmin) {
            btn.classList.remove('d-none');
        } else {
            btn.classList.add('d-none');
        }
    });

    // 3. Controlar botones de Administrador (Fuentes, +Colección, Curar/Actualizar)
    adminOnly.forEach(btn => {
        if (isAdminRole) {
            btn.classList.remove('d-none');
        } else {
            btn.classList.add('d-none');
        }
    });
}


function ocultarTodoYMostrarLogin() {
    // Oculta todos los elementos protegidos
    loginRequired.forEach(btn => btn.classList.add('d-none'));
    adminOnly.forEach(btn => btn.classList.add('d-none'));

    // Muestra el botón de Iniciar Sesión
    if (btnLogin) btnLogin.classList.remove('d-none');
    if (btnLogout) btnLogout.classList.add('d-none');

    if (nombreUsuarioDisplay) {
        nombreUsuarioDisplay.textContent = '';
    }
}

async function verificarSesionYActualizarUI() {
    try {
        const resp = await fetch(
            `${window.METAMAPA.API_USUARIOS}/api-auth/me`,
            { credentials: 'include' }
        );

        if (resp.ok) {
            const usuario = await resp.json();
            const roles = usuario.roles || [];
            actualizarVisibilidadPorRoles(roles, usuario.nombre);
        } else {
            ocultarTodoYMostrarLogin();
        }
    } catch (e) {
        console.error("Error verificando sesión:", e);
        ocultarTodoYMostrarLogin();
    }
}

// --- Event Listeners y Inicialización ---

// Llamar a la función al cargar el DOM para revisar si hay una sesión JWT activa
document.addEventListener('DOMContentLoaded', verificarSesionYActualizarUI);

function iniciarSesionSSO() {
    // Lleva al login del SSO
    window.location.href = `${window.METAMAPA.API_USUARIOS}/login`;
}

function cerrarSesion() {
    // Logout contra el SSO
    fetch(`${window.METAMAPA.API_USUARIOS}/logout`, {
        method: 'POST',
        credentials: 'include'
    })
        .catch(err => {
            console.error("Error al hacer logout:", err);
        })
        .finally(() => {
            // Limpio la UI del lado del frontend
            ocultarTodoYMostrarLogin();
            // Vuelvo al index del Metamapa
            window.location.href = 'http://localhost:9000/index.html';
        });
}