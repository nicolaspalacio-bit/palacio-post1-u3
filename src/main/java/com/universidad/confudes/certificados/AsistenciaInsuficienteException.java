package com.universidad.confudes.certificados;

/**
 * Señala que el participante no cumple el porcentaje mínimo de asistencia
 * requerido para emitir su certificado. Se usa en lugar de un valor de
 * retorno especial para que ServicioCertificados.emitir() mantenga una
 * firma simple (byte[]) tanto en el flujo exitoso como en las decoraciones
 * y el proxy de control de acceso que se apoyan en el mismo contrato.
 */
public class AsistenciaInsuficienteException extends RuntimeException {
    public AsistenciaInsuficienteException(String mensaje) {
        super(mensaje);
    }
}
