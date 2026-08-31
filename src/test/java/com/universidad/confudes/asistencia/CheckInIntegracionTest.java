package com.universidad.confudes.asistencia;

import com.universidad.confudes.externo.qrcheck.QRCheckClient;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CheckInIntegracionTest {

    @Test
    void registraAsistenciaConCredencialValida() {
        ServicioAsistencia servicio = new ServicioAsistenciaAdapter(new QRCheckClient());
        ResultadoCheckIn resultado = servicio.registrarAsistencia("EVT-001", "PART-123", "QR-abc123");
        assertTrue(resultado.isExitoso());
    }

    @Test
    void rechazaCredencialInvalidaSinLanzarExcepcion() {
        ServicioAsistencia servicio = new ServicioAsistenciaAdapter(new QRCheckClient());
        ResultadoCheckIn resultado = servicio.registrarAsistencia("EVT-001", "PART-999", "no-es-un-qr");
        assertFalse(resultado.isExitoso());
    }
}
