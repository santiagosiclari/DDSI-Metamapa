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