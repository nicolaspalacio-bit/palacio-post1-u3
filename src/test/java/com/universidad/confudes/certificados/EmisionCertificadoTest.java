package com.universidad.confudes.certificados;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class EmisionCertificadoTest {

    @Test
    void ordenColaboradorOrquestaLasCuatroEtapasSinExcepcion() {
        var validador = new ValidadorAsistencia();
        var generador = new GeneradorCertificadoPDF();
        var firma = new FirmaDigitalService();
        var correo = new EnvioCorreoService();

        ServicioCertificados colaborador = new EmisionCertificadoFacade(validador, generador, firma, correo);
        SolicitudCertificado solicitud = new SolicitudCertificado("EVT-001", "PART-123", "Ana Ríos", "ana@correo.com");

        // La operación simple expuesta por su colaborador debe ejecutarse sin lanzar excepciones
        assertDoesNotThrow(() -> {
            // invocar aquí el único método público de su colaborador
            colaborador.emitir(solicitud);
        });
    }

    @Test
    void controladorCertificadosSoloDependeDeUnColaborador() {
        // Verificación de diseño: ControladorCertificados debe declarar un solo
        // constructor con un solo parámetro tras la refactorización.
        var constructores = ControladorCertificados.class.getDeclaredConstructors();
        assertEquals(1, constructores.length);
        assertEquals(1, constructores[0].getParameterCount());
    }
}
