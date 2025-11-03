// ---- Registro ----
const formRegistro = document.getElementById('formRegistro');
if (formRegistro) {
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
            const resultado = await registrarUsuario({
                email,
                contrasenia: password,
                nombre,
                apellido,
                edad,
                roles: [rol] // siempre lista
            });
            if (!resultado.ok) {
                alert('❌ Error: ' + resultado.mensaje);
                return;
            }
            alert('✅ Usuario registrado correctamente');
            window.location.href = 'login.html';
        } catch (err) {
            console.error(err);
            alert('❌ Error de red al registrarse');
        }

    });
}