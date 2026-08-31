package com.universidad.confudes.certificados;

import org.springframework.stereotype.Service;

import java.time.LocalDate;

/**
 * Necesidad 2 - Patrón Facade.
 *
 * <p>Único colaborador nuevo que recibe los cuatro servicios existentes
 * (ValidadorAsistencia, GeneradorCertificadoPDF, FirmaDigitalService,
 * EnvioCorreoService) y expone una operación simple, {@link #emitir(SolicitudCertificado)},
 * que encapsula toda la secuencia validar → generar → firmar → enviar.</p>
 *
 * <p>Los cuatro servicios siguen usándose directamente por otros módulos de ConfUDES en
 * otros contextos: esta clase no los reemplaza ni los fusiona, solo les da un único
 * punto de coordinación para el caso de uso de emisión individual de certificados,
 * de modo que ControladorCertificados deje de conocerlos uno por uno.</p>
 */
@Service
public class EmisionCertificadoFacade implements ServicioCertificados {

    private static final String PLANTILLA_DEFECTO = "plantilla-2026";
    private static final String CERTIFICADO_INSTITUCIONAL = "cert-udes-2026.pfx";
    private static final double ASISTENCIA_MINIMA_REQUERIDA = 0.8;
    private static final String ASUNTO_CORREO = "Su certificado de participación";
    private static final String CUERPO_CORREO = "Adjunto encontrará su certificado.";

    private final ValidadorAsistencia validador;
    private final GeneradorCertificadoPDF generador;
    private final FirmaDigitalService firma;
    private final EnvioCorreoService correo;

    public EmisionCertificadoFacade(ValidadorAsistencia validador,
                                     GeneradorCertificadoPDF generador,
                                     FirmaDigitalService firma,
                                     EnvioCorreoService correo) {
        this.validador = validador;
        this.generador = generador;
        this.firma = firma;
        this.correo = correo;
    }

    @Override
    public byte[] emitir(SolicitudCertificado solicitud) {
        if (!validador.tieneAsistenciaMinima(solicitud.getParticipanteId(), solicitud.getEventoId(),
                ASISTENCIA_MINIMA_REQUERIDA)) {
            throw new AsistenciaInsuficienteException("Asistencia insuficiente");
        }

        byte[] documentoBase = generador.iniciarDocumento(PLANTILLA_DEFECTO);
        generador.insertarDatosParticipante(documentoBase, solicitud.getNombre(), solicitud.getEventoId(),
                LocalDate.now().toString());
        byte[] documentoFinal = generador.finalizarDocumento();

        FirmaDigitalService.Sesion sesion = firma.abrirSesion(CERTIFICADO_INSTITUCIONAL);
        byte[] documentoFirmado = firma.firmar(sesion, documentoFinal);
        firma.cerrarSesion(sesion);

        correo.adjuntarArchivo(solicitud.getCorreoDestino(), documentoFirmado,
                "certificado-" + solicitud.getParticipanteId() + ".pdf");
        correo.enviar(ASUNTO_CORREO, CUERPO_CORREO);

        return documentoFirmado;
    }
}
