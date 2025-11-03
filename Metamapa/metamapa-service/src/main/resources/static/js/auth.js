// ---- Funciones de mostrar/ocultar botones ----
const loginRequired = document.querySelectorAll('.login-required');
const btnLogout = document.getElementById('btnLogout');

function mostrarBotonesLogin() {
    loginRequired.forEach(btn => btn.classList.remove('d-none'));
    const btnLogin = document.getElementById('btnLogin');
    if (btnLogin) btnLogin.classList.add('d-none');
    if (btnLogout) btnLogout.classList.remove('d-none');
}

function ocultarBotonesLogin() {
    loginRequired.forEach(btn => btn.classList.add('d-none'));
    const btnLogin = document.getElementById('btnLogin');
    if (btnLogin) btnLogin.classList.remove('d-none');
    if (btnLogout) btnLogout.classList.add('d-none');
}

// Revisar sesión al cargar la página
if (localStorage.getItem('usuario')) {
    mostrarBotonesLogin();
} else {
    ocultarBotonesLogin();
}

// Cerrar sesión
if (btnLogout) {
    btnLogout.addEventListener('click', () => {
        localStorage.removeItem('usuario');
        ocultarBotonesLogin();
        // opcional: redirigir al login
        // window.location.href = 'login.html';
    });
}