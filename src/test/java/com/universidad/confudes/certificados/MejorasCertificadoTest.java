package com.universidad.confudes.certificados;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class MejorasCertificadoTest {

    private final SolicitudCertificado solicitud =
            new SolicitudCertificado("EVT-001", "PART-123", "Ana Ríos", "ana@correo.com");

    private ServicioCertificados colaboradorBase() {
        return new EmisionCertificadoFacade(new ValidadorAsistencia(), new GeneradorCertificadoPDF(),
                new FirmaDigitalService(), new EnvioCorreoService());
    }

    @Test
    void emiteSinNingunaMejoraActivada() {
        ServicioCertificados base = colaboradorBase();
        assertDoesNotThrow(() -> base.emitir(solicitud));
    }

    @Test
    void combinaLasTresMejorasSinCrearUnaClaseNueva() {
        ServicioCertificados conTodo =
                new MarcaDeAguaDecorator(new CodigoQRDecorator(new TraduccionDecorator(colaboradorBase())));
        assertDoesNotThrow(() -> conTodo.emitir(solicitud));
    }

    @Test
    void unaSolaMejoraFuncionaDeFormaIndependiente() {
        ServicioCertificados soloMarcaDeAgua = new MarcaDeAguaDecorator(colaboradorBase());
        assertDoesNotThrow(() -> soloMarcaDeAgua.emitir(solicitud));
    }
}
