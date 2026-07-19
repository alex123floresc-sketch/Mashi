$(function () {
    const usuario = Api.requerirRol('ADMINISTRADOR');
    $('#nombre-usuario').text(usuario.nombre);
    $('#btn-logout').on('click', () => Api.cerrarSesion());

    function formatoMoneda(valor) {
        return 'S/ ' + Number(valor).toFixed(2);
    }

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

    cargarAnalitica();
});
