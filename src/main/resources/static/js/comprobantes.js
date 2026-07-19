$(function () {
    const usuario = Api.requerirRol('CLIENTE', 'VENDEDOR', 'ADMINISTRADOR');
    $('#nombre-usuario').text(usuario.nombre);
    $('#btn-logout').on('click', () => Api.cerrarSesion());

    function formatoMoneda(valor) {
        return 'S/ ' + Number(valor).toFixed(2);
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
                if (usuario.rol === 'ADMINISTRADOR' && c.estado === 'EMITIDO') {
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
            $('#mensaje-error-comprobantes').text(err.message).show();
        }
    }

    cargarComprobantes();
});
