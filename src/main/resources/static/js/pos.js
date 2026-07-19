$(function () {
    const usuario = Api.requerirRol('VENDEDOR', 'ADMINISTRADOR');
    $('#nombre-usuario').text(usuario.nombre);
    $('#btn-logout').on('click', () => Api.cerrarSesion());

    let itemsVenta = [];

    function formatoMoneda(valor) {
        return 'S/ ' + Number(valor).toFixed(2);
    }

    function renderVenta() {
        const filas = $('#filas-venta').empty();
        let total = 0;
        itemsVenta.forEach((item, index) => {
            const subtotal = item.precio * item.cantidad;
            total += subtotal;
            const fila = $(`
                <tr>
                    <td>${item.sku}</td>
                    <td>${item.nombre}</td>
                    <td>${item.cantidad}</td>
                    <td>${formatoMoneda(subtotal)}</td>
                    <td><i class="trash alternate outline icon" style="cursor:pointer;"></i></td>
                </tr>
            `);
            fila.find('.icon').on('click', () => {
                itemsVenta.splice(index, 1);
                renderVenta();
            });
            filas.append(fila);
        });
        $('#total-venta').text(formatoMoneda(total));
    }

    async function agregarProductoPorSku(sku) {
        $('#mensaje-error-venta').hide();
        $('#mensaje-error-scanner').hide();
        try {
            const producto = await Api.post('/api/pos/escanear', { codigo: sku });
            const existente = itemsVenta.find(i => i.sku === producto.sku);
            if (existente) {
                existente.cantidad += 1;
            } else {
                itemsVenta.push({ sku: producto.sku, nombre: producto.nombre, precio: producto.precio, cantidad: 1 });
            }
            renderVenta();
        } catch (err) {
            $('#mensaje-error-venta').text(err.message).show();
        }
    }

    $('#btn-agregar-manual').on('click', () => {
        const sku = $('#input-sku-manual').val().trim();
        if (sku) {
            agregarProductoPorSku(sku);
            $('#input-sku-manual').val('');
        }
    });

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
                origen: 'VENTA_POS',
                origenId: parseInt($('#venta-comprobante-id').text(), 10),
                tipo: this.tipo.value,
                clienteDocumento: this.clienteDocumento.value,
                clienteNombre: this.clienteNombre.value
            });
            $('#mensaje-exito-comprobante').text('Comprobante ' + comprobante.numero + ' emitido. Total: ' + formatoMoneda(comprobante.total)).show();
        } catch (err) {
            $('#mensaje-error-comprobante').text(err.message).show();
        }
    });

    $('#btn-registrar-venta').on('click', async () => {
        $('#mensaje-error-venta').hide();
        $('#mensaje-exito-venta').hide();
        $('#panel-comprobante').hide();
        if (itemsVenta.length === 0) {
            $('#mensaje-error-venta').text('Agregue al menos un producto').show();
            return;
        }
        try {
            const transaccionId = Api.nuevaTransaccionId();
            const items = itemsVenta.map(i => ({ sku: i.sku, cantidad: i.cantidad }));
            const venta = await Api.post('/api/pos/ventas', { transaccionId, items });
            $('#mensaje-exito-venta').text('Venta #' + venta.id + ' registrada. Total: ' + formatoMoneda(venta.total)).show();
            itemsVenta = [];
            renderVenta();
            $('#venta-comprobante-id').text(venta.id);
            $('#mensaje-error-comprobante').hide();
            $('#mensaje-exito-comprobante').hide();
            $('#form-comprobante')[0].reset();
            $('#panel-comprobante').show();
        } catch (err) {
            $('#mensaje-error-venta').text(err.message).show();
        }
    });

    if (typeof Html5Qrcode !== 'undefined') {
        const scanner = new Html5Qrcode('qr-reader');
        Html5Qrcode.getCameras().then(camaras => {
            if (camaras && camaras.length) {
                scanner.start(
                    { facingMode: 'environment' },
                    { fps: 10, qrbox: 220 },
                    (decodedText) => agregarProductoPorSku(decodedText),
                    () => {}
                );
            }
        }).catch(() => {
            $('#mensaje-error-scanner').text('No se pudo acceder a la camara. Use el campo de SKU manual.').show();
        });
    }
});
