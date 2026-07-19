$(function () {
    $('.menu .item').tab();

    function mostrarError(mensaje) {
        $('#mensaje-error').text(mensaje).show();
    }

    function redirigirPorRol(usuario) {
        if (usuario.rol === 'ADMINISTRADOR') {
            window.location.href = '/admin.html';
        } else if (usuario.rol === 'VENDEDOR') {
            window.location.href = '/pos.html';
        } else {
            window.location.href = '/catalogo.html';
        }
    }

    if (Api.usuario()) {
        redirigirPorRol(Api.usuario());
    }

    $('#form-login').on('submit', async function (e) {
        e.preventDefault();
        $('#mensaje-error').hide();
        const email = this.email.value;
        const password = this.password.value;
        try {
            const auth = await Api.post('/api/auth/login', { email, password });
            Api.guardarSesion(auth);
            redirigirPorRol(auth.usuario);
        } catch (err) {
            mostrarError(err.message);
        }
    });

    $('#form-registro').on('submit', async function (e) {
        e.preventDefault();
        $('#mensaje-error').hide();
        const nombre = this.nombre.value;
        const email = this.email.value;
        const password = this.password.value;
        const rol = this.rol.value;
        try {
            const auth = await Api.post('/api/auth/registro', { nombre, email, password, rol });
            Api.guardarSesion(auth);
            redirigirPorRol(auth.usuario);
        } catch (err) {
            mostrarError(err.message);
        }
    });
});
