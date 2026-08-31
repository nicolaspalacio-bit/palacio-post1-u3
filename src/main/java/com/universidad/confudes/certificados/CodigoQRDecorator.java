package com.universidad.confudes.certificados;

/** Necesidad 3 - Mejora combinable: inserta un código QR que enlaza a la página de validación del certificado. */
public class CodigoQRDecorator extends MejoraCertificadoDecorator {

    private static final String BASE_URL_VERIFICACION = "https://confudes.udes.edu.co/verificar/";

    public CodigoQRDecorator(ServicioCertificados delegado) {
        super(delegado);
    }

    @Override
    public byte[] emitir(SolicitudCertificado solicitud) {
        byte[] documento = delegado.emitir(solicitud);
        String urlVerificacion = BASE_URL_VERIFICACION + solicitud.getParticipanteId();
        return UtilidadesPDF.insertarCodigoQR(documento, urlVerificacion);
    }
}
