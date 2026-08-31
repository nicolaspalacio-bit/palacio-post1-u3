package com.universidad.confudes.acceso;

import com.universidad.confudes.certificados.ServicioCertificados;
import com.universidad.confudes.certificados.SolicitudCertificado;

/**
 * Necesidad 4 - Patrón Proxy (proxy de protección).
 *
 * <p>Implementa el mismo contrato ServicioCertificados que envuelve, pero decide si
 * la llamada llega o no al objeto real: verifica el rol con {@link ContextoUsuario}
 * antes de delegar, y rechaza con una excepción a quien no sea ORGANIZADOR o ADMIN
 * sin llegar a invocar la lógica costosa de emisión (que en la operación real de
 * descarga masiva significa evitar decenas o cientos de llamadas al proveedor de
 * firma digital, limitado a 60 por minuto).</p>
 *
 * <p>El resto del sistema —incluido el flujo de emisión individual de la Necesidad 2,
 * usado por los propios participantes— sigue inyectando ServicioCertificados sin saber
 * que este proxy existe: solo se coloca delante de la implementación usada para la
 * descarga masiva de organizadores.</p>
 */
public class ServicioCertificadosControlAcceso implements ServicioCertificados {

    private static final String ROL_ORGANIZADOR = "ORGANIZADOR";
    private static final String ROL_ADMIN = "ADMIN";

    private final ServicioCertificados servicioReal;

    public ServicioCertificadosControlAcceso(ServicioCertificados servicioReal) {
        this.servicioReal = servicioReal;
    }

    @Override
    public byte[] emitir(SolicitudCertificado solicitud) {
        String rol = ContextoUsuario.rolActual();
        if (!ROL_ORGANIZADOR.equals(rol) && !ROL_ADMIN.equals(rol)) {
            throw new SecurityException("Rol no autorizado para descarga masiva: " + rol);
        }
        return servicioReal.emitir(solicitud);
    }
}
