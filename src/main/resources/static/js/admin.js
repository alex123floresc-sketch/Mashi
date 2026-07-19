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

    let graficoVentas = null;

    async function cargarAnalitica() {
        try {
            const resumen = await Api.get('/api/analitica/resumen?dias=30');

            $('#stat-total-online').text(formatoMoneda(resumen.totalVentasOnline));
            $('#stat-total-pos').text(formatoMoneda(resumen.totalVentasPos));
            $('#stat-total-general').text(formatoMoneda(resumen.totalVentasGeneral));
            $('#stat-cantidad-ventas').text(resumen.cantidadPedidos + resumen.cantidadVentasPos);

            const etiquetas = resumen.ventasPorDia.map(v => v.fecha);
            const datosOnline = resumen.ventasPorDia.map(v => v.totalOnline);
            const datosPos = resumen.ventasPorDia.map(v => v.totalPos);

            const ctx = document.getElementById('grafico-ventas');
            if (graficoVentas) {
                graficoVentas.data.labels = etiquetas;
                graficoVentas.data.datasets[0].data = datosOnline;
                graficoVentas.data.datasets[1].data = datosPos;
                graficoVentas.update();
            } else if (typeof Chart !== 'undefined') {
                graficoVentas = new Chart(ctx, {
                    type: 'bar',
                    data: {
                        labels: etiquetas,
                        datasets: [
                            { label: 'Online', data: datosOnline, backgroundColor: '#00b5ad' },
                            { label: 'POS', data: datosPos, backgroundColor: '#767676' }
                        ]
                    },
                    options: {
                        responsive: true,
                        scales: { x: { stacked: true }, y: { stacked: true, beginAtZero: true } }
                    }
                });
            }

            const filasTop = $('#filas-top-productos').empty();
            resumen.topProductos.forEach(p => {
                filasTop.append(`
                    <tr>
                        <td>${p.nombre}</td>
                        <td>${p.cantidadVendida}</td>
                        <td>${formatoMoneda(p.totalVendido)}</td>
                    </tr>
                `);
            });
        } catch (err) {
            $('#mensaje-error-analitica').text(err.message).show();
        }
    }

    async function cargarComprobantes() {
        try {
            const comprobantes = await Api.get('/api/comprobantes/mios');
            const filas = $('#filas-comprobantes').empty();
            comprobantes.forEach(c => {
                const fila = $(`
                    <tr>
                        <td>${c.numero}</td>
                        <td>${c.tipo}</td>
                        <td>${c.clienteNombre}</td>
                        <td>${formatoMoneda(c.total)}</td>
                        <td>${c.estado}</td>
                        <td>${new Date(c.fechaEmision).toLocaleString()}</td>
                        <td></td>
                    </tr>
                `);
                if (c.estado === 'EMITIDO') {
                    const boton = $('<button class="ui tiny red button">Anular</button>');
                    boton.on('click', async () => {
                        try {
                            await Api.post('/api/comprobantes/' + c.id + '/anular');
                            await cargarComprobantes();
                        } catch (err) {
                            window.alert(err.message);
                        }
                    });
                    fila.find('td').last().append(boton);
                }
                filas.append(fila);
            });
        } catch (err) {
            // no bloquea el resto del panel de administracion
        }
    }

    cargarCategorias();
    cargarProductos();
    cargarAnalitica();
    cargarComprobantes();
});
