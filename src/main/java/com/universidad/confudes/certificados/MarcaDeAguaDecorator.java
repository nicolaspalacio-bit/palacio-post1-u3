package com.universidad.confudes.certificados;

/** Necesidad 3 - Mejora combinable: aplica la marca de agua institucional sobre el PDF ya emitido. */
public class MarcaDeAguaDecorator extends MejoraCertificadoDecorator {

    private static final String TEXTO_MARCA_INSTITUCIONAL = "ConfUDES - Documento Oficial";

    public MarcaDeAguaDecorator(ServicioCertificados delegado) {
        super(delegado);
    }

    @Override
    public byte[] emitir(SolicitudCertificado solicitud) {
        byte[] documento = delegado.emitir(solicitud);
        return UtilidadesPDF.aplicarMarcaDeAgua(documento, TEXTO_MARCA_INSTITUCIONAL);
    }
}
