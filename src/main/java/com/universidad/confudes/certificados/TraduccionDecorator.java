package com.universidad.confudes.certificados;

/** Necesidad 3 - Mejora combinable: traduce el texto fijo del certificado al inglés. */
public class TraduccionDecorator extends MejoraCertificadoDecorator {

    public TraduccionDecorator(ServicioCertificados delegado) {
        super(delegado);
    }

    @Override
    public byte[] emitir(SolicitudCertificado solicitud) {
        byte[] documento = delegado.emitir(solicitud);
        return UtilidadesPDF.traducirAIngles(documento);
    }
}
