const Api = (() => {
    function token() {
        return localStorage.getItem('mashi_token');
    }

    function usuario() {
        const raw = localStorage.getItem('mashi_usuario');
        return raw ? JSON.parse(raw) : null;
    }

    function guardarSesion(authResponse) {
        localStorage.setItem('mashi_token', authResponse.token);
        localStorage.setItem('mashi_usuario', JSON.stringify(authResponse.usuario));
    }

    function cerrarSesion() {
        localStorage.removeItem('mashi_token');
        localStorage.removeItem('mashi_usuario');
        window.location.href = '/index.html';
    }

    async function request(method, url, body) {
        const headers = { 'Content-Type': 'application/json' };
        const t = token();
        if (t) {
            headers['Authorization'] = 'Bearer ' + t;
        }

        const response = await fetch(url, {
            method,
            headers,
            body: body !== undefined ? JSON.stringify(body) : undefined
        });

        if (response.status === 204) {
            return null;
        }

        const data = await response.json().catch(() => null);

        if (!response.ok) {
            const mensaje = (data && data.message) ? data.message : ('Error ' + response.status);
            throw new Error(mensaje);
        }

        return data;
    }

    function requerirRol(...roles) {
        const u = usuario();
        if (!u || !roles.includes(u.rol)) {
            window.location.href = '/index.html';
        }
        return u;
    }

    function nuevaTransaccionId() {
        return crypto.randomUUID();
    }

    return {
        get: (url) => request('GET', url),
        post: (url, body) => request('POST', url, body),
        put: (url, body) => request('PUT', url, body),
        patch: (url, body) => request('PATCH', url, body),
        del: (url) => request('DELETE', url),
        token, usuario, guardarSesion, cerrarSesion, requerirRol, nuevaTransaccionId
    };
})();
