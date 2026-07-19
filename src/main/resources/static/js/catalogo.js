$(function () {
    const usuario = Api.requerirRol('CLIENTE');
    $('#nombre-usuario').text(usuario.nombre);
    $('#btn-logout').on('click', () => Api.cerrarSesion());

    function formatoMoneda(valor) {
        return 'S/ ' + Number(valor).toFixed(2);
    }

    async function cargarProductos() {
        try {
            const productos = await Api.get('/api/productos');
            const contenedor = $('#lista-productos').empty();
            productos.forEach(p => {
                const card = $(`
                    <div class="card">
                        <div class="content">
                            <div class="header">${p.nombre}</div>
                            <div class="meta">${p.categoriaNombre ?? ''}</div>
                            <div class="description">${p.descripcion ?? ''}</div>
                        </div>
                        <div class="content">
                            <strong>${formatoMoneda(p.precio)}</strong>
                            <span style="float:right;">Stock: ${p.stockDisponible}</span>
                        </div>
                        <div class="ui bottom attached teal button">Agregar al carrito</div>
                    </div>
                `);
                card.find('.button').on('click', () => agregarAlCarrito(p.id));
                contenedor.append(card);
            });
        } catch (err) {
            $('#mensaje-error-productos').text(err.message).show();
        }
    }

    async function cargarCarrito() {
        try {
            const carrito = await Api.get('/api/carrito');
            const filas = $('#filas-carrito').empty();
            carrito.items.forEach(item => {
                const fila = $(`
                    <tr>
                        <td>${item.productoNombre}</td>
                        <td>${item.cantidad}</td>
                        <td>${formatoMoneda(item.subtotal)}</td>
                        <td><i class="trash alternate outline icon" style="cursor:pointer;"></i></td>
                    </tr>
                `);
                fila.find('.icon').on('click', () => eliminarItem(item.itemId));
                filas.append(fila);
            });
            $('#total-carrito').text(formatoMoneda(carrito.total));
        } catch (err) {
            $('#mensaje-error-carrito').text(err.message).show();
        }
    }

    async function agregarAlCarrito(productoId) {
        $('#mensaje-error-carrito').hide();
        try {
            await Api.post('/api/carrito/items', { productoId, cantidad: 1 });
            await cargarCarrito();
        } catch (err) {
            $('#mensaje-error-carrito').text(err.message).show();
        }
    }

    async function eliminarItem(itemId) {
        try {
            await Api.del('/api/carrito/items/' + itemId);
            await cargarCarrito();
        } catch (err) {
            $('#mensaje-error-carrito').text(err.message).show();
        }
    }

    async function cargarComprobantes() {
        try {
            const comprobantes = await Api.get('/api/comprobantes/mios');
            const filas = $('#filas-comprobantes').empty();
            comprobantes.forEach(c => {
                filas.append(`
                    <tr>
                        <td>${c.numero}</td>
                        <td>${c.tipo}</td>
                        <td>${formatoMoneda(c.total)}</td>
                        <td>${c.estado}</td>
                        <td>${new Date(c.fechaEmision).toLocaleString()}</td>
                    </tr>
                `);
            });
        } catch (err) {
            // el panel de comprobantes es informativo, no bloquea el resto de la pagina
        }
    }

    $('#select-tipo-comprobante').on('change', function () {
        const esFactura = this.value === 'FACTURA';
        $('#input-documento-comprobante')
            .attr('placeholder', esFactura ? 'RUC (11 digitos)' : 'DNI (8 digitos)')
            .attr('maxlength', esFactura ? 11 : 8);
    });

    $('#form-comprobante').on('submit', async function (e) {
        e.preventDefault();
        $('#mensaje-error-comprobante').hide();
        $('#mensaje-exito-comprobante').hide();
        try {
            const comprobante = await Api.post('/api/comprobantes', {
                origen: 'PEDIDO',
                origenId: parseInt($('#pedido-comprobante-id').text(), 10),
                tipo: this.tipo.value,
                clienteDocumento: this.clienteDocumento.value,
                clienteNombre: this.clienteNombre.value
            });
            $('#mensaje-exito-comprobante').text('Comprobante ' + comprobante.numero + ' emitido. Total: ' + formatoMoneda(comprobante.total)).show();
            await cargarComprobantes();
        } catch (err) {
            $('#mensaje-error-comprobante').text(err.message).show();
        }
    });

    $('#btn-checkout').on('click', async () => {
        $('#mensaje-error-carrito').hide();
        $('#mensaje-exito-carrito').hide();
        $('#panel-comprobante').hide();
        try {
            const transaccionId = Api.nuevaTransaccionId();
            const pedido = await Api.post('/api/checkout', { transaccionId });
            $('#mensaje-exito-carrito').text('Pedido #' + pedido.id + ' registrado. Total: ' + formatoMoneda(pedido.total)).show();
            $('#pedido-comprobante-id').text(pedido.id);
            $('#mensaje-error-comprobante').hide();
            $('#mensaje-exito-comprobante').hide();
            $('#form-comprobante')[0].reset();
            $('#panel-comprobante').show();
            await cargarCarrito();
            await cargarProductos();
        } catch (err) {
            $('#mensaje-error-carrito').text(err.message).show();
        }
    });

    cargarProductos();
    cargarCarrito();
    cargarComprobantes();
});
