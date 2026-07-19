$(function () {
    const usuario = Api.requerirRol('ADMINISTRADOR');
    $('#nombre-usuario').text(usuario.nombre);
    $('#btn-logout').on('click', () => Api.cerrarSesion());

    function formatoMoneda(valor) {
        return 'S/ ' + Number(valor).toFixed(2);
    }

    async function cargarCategorias() {
        const categorias = await Api.get('/api/categorias');
        const select = $('#select-categoria').empty();
        categorias.forEach(c => select.append(`<option value="${c.id}">${c.nombre}</option>`));
        $(select).dropdown();
    }

    async function cargarProductos() {
        const productos = await Api.get('/api/productos/todos');
        const filas = $('#filas-productos').empty();
        productos.forEach(p => {
            filas.append(`
                <tr>
                    <td>${p.sku}</td>
                    <td>${p.nombre}</td>
                    <td>${p.categoriaNombre ?? ''}</td>
                    <td>${formatoMoneda(p.precio)}</td>
                    <td>${p.stockDisponible}</td>
                    <td>${p.activo ? 'Si' : 'No'}</td>
                </tr>
            `);
        });
    }

    $('#form-categoria').on('submit', async function (e) {
        e.preventDefault();
        $('#mensaje-error-categoria').hide();
        try {
            await Api.post('/api/categorias', { nombre: this.nombre.value, descripcion: this.descripcion.value });
            this.reset();
            await cargarCategorias();
        } catch (err) {
            $('#mensaje-error-categoria').text(err.message).show();
        }
    });

    $('#form-producto').on('submit', async function (e) {
        e.preventDefault();
        $('#mensaje-error-producto').hide();
        try {
            await Api.post('/api/productos', {
                sku: this.sku.value,
                nombre: this.nombre.value,
                descripcion: this.descripcion.value,
                precio: parseFloat(this.precio.value),
                categoriaId: parseInt(this.categoriaId.value, 10),
                stockDisponible: parseInt(this.stockDisponible.value, 10)
            });
            this.reset();
            await cargarProductos();
        } catch (err) {
            $('#mensaje-error-producto').text(err.message).show();
        }
    });

    cargarCategorias();
    cargarProductos();
});
