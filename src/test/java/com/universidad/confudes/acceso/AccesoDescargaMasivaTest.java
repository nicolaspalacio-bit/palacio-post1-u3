package com.universidad.confudes.acceso;

import com.universidad.confudes.certificados.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.*;

class AccesoDescargaMasivaTest {

    private ServicioCertificados servicioReal() {
        return new EmisionCertificadoFacade(new ValidadorAsistencia(), new GeneradorCertificadoPDF(),
                new FirmaDigitalService(), new EnvioCorreoService());
    }

    @AfterEach
    void limpiarRol() {
        System.clearProperty("confudes.rol");
    }

    @Test
    void rechazaAParticipanteSinLlegarAEmitir() {
        System.setProperty("confudes.rol", "PARTICIPANTE");
        ServicioCertificados controlado = new ServicioCertificadosControlAcceso(servicioReal());
        SolicitudCertificado solicitud = new SolicitudCertificado("EVT-001", "PART-123", "Ana", "ana@correo.com");
        assertThrows(SecurityException.class, () -> controlado.emitir(solicitud));
    }

    @Test
    void permiteAOrganizador() {
        System.setProperty("confudes.rol", "ORGANIZADOR");
        ServicioCertificados controlado = new ServicioCertificadosControlAcceso(servicioReal());
        SolicitudCertificado solicitud = new SolicitudCertificado("EVT-001", "PART-123", "Ana", "ana@correo.com");
        assertDoesNotThrow(() -> controlado.emitir(solicitud));
    }
}
