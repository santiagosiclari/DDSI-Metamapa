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

// ---- Login ----
const formLogin = document.getElementById('formLogin');
if (formLogin) {
    formLogin.addEventListener('submit', e => {
        e.preventDefault();
        const usuario = document.getElementById('usuario').value.trim();
        if (!usuario) return;
        localStorage.setItem('usuario', usuario);
        window.location.href = 'index.html';
    });
}

// ---- Registro ----
const formRegistro = document.getElementById('formRegistro');
formRegistro.addEventListener('submit', async e => {
    e.preventDefault();

    const email = document.getElementById('nuevoEmail').value.trim();
    const password = document.getElementById('nuevoPassword').value;
    const nombre = document.getElementById('nuevoNombre').value.trim();
    const apellido = document.getElementById('nuevoApellido').value.trim();
    const edad = parseInt(document.getElementById('nuevoEdad').value, 10);
    const rol = document.getElementById('tipoUsuario').value;

    if (!email || !password || !nombre || !apellido || !edad || !rol) return;

    try {
        const response = await fetch('http://localhost:9001/register', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                email,
                contrasenia: password,
                nombre,
                apellido,
                edad,
                roles: [rol]  // siempre en lista
            })
        });
        if (!response.ok) {
            const error = await response.json();
            alert('Error: ' + (error.mensaje || response.statusText));
            return;
        }
        alert('Usuario registrado correctamente');
        window.location.href = 'login.html';
    } catch (err) {
        console.error(err);
        alert('Error al registrarse');
    }
});