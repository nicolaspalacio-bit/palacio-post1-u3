package com.universidad.confudes.asistencia;

import com.universidad.confudes.externo.qrcheck.QRCheckClient;
import com.universidad.confudes.externo.qrcheck.QRCheckRequest;
import com.universidad.confudes.externo.qrcheck.QRCheckResponse;
import org.springframework.stereotype.Service;

/**
 * Necesidad 1 - Patrón Adapter.
 *
 * <p>Traduce el contrato de un único colaborador externo (QRCheckClient, cuyo contrato
 * lo define el proveedor y no puede modificarse) al contrato interno ServicioAsistencia
 * que el resto del sistema ya consume (ControladorCheckIn, ya en producción).</p>
 *
 * <p>Responsabilidades de traducción:</p>
 * <ul>
 *   <li>eventoId llega como String; QRCheckRequest exige un long. Se extrae la parte
 *       numérica del identificador (p. ej. "EVT-001" → 1L); si no hay dígitos, se usa
 *       un identificador derivado y estable a partir del propio String.</li>
 *   <li>credencialQR se asigna directamente como payload: por convención del sistema,
 *       una credencial QR válida ya llega con el prefijo "QR-" que exige el proveedor;
 *       el adaptador no debe "corregir" ni forzar ese prefijo sobre una credencial que
 *       no lo trae, porque eso convertiría en válida una credencial que en realidad es
 *       inválida.</li>
 *   <li>El código de respuesta del proveedor (200/401/otro) se traduce a un
 *       ResultadoCheckIn: 200 se traduce a éxito, cualquier otro código a fallo,
 *       reutilizando el detalle que el proveedor ya entrega como mensaje.</li>
 * </ul>
 */
@Service
public class ServicioAsistenciaAdapter implements ServicioAsistencia {

    private static final int CODIGO_EXITO_PROVEEDOR = 200;

    private final QRCheckClient qrCheckClient;

    public ServicioAsistenciaAdapter(QRCheckClient qrCheckClient) {
        this.qrCheckClient = qrCheckClient;
    }

    @Override
    public ResultadoCheckIn registrarAsistencia(String eventoId, String participanteId, String credencialQR) {
        QRCheckRequest solicitudProveedor = new QRCheckRequest(credencialQR, idEventoComoLong(eventoId));
        QRCheckResponse respuestaProveedor = qrCheckClient.validar(solicitudProveedor);
        return traducirRespuesta(respuestaProveedor);
    }

    private long idEventoComoLong(String eventoId) {
        if (eventoId == null) {
            return 0L;
        }
        String soloDigitos = eventoId.replaceAll("\\D", "");
        if (soloDigitos.isEmpty()) {
            return Math.abs((long) eventoId.hashCode());
        }
        try {
            return Long.parseLong(soloDigitos);
        } catch (NumberFormatException ex) {
            return Math.abs((long) eventoId.hashCode());
        }
    }

    private ResultadoCheckIn traducirRespuesta(QRCheckResponse respuestaProveedor) {
        boolean exitoso = respuestaProveedor.getCodigoRespuesta() == CODIGO_EXITO_PROVEEDOR;
        return new ResultadoCheckIn(exitoso, respuestaProveedor.getDetalle());
    }
}
