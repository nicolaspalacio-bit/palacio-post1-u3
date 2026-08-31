package com.universidad.confudes.certificados;

// Contrato formalizado en el Paso 7: base sobre la que se construye toda la Parte 2
// (mejoras combinables de la Necesidad 3 y control de acceso de la Necesidad 4).
public interface ServicioCertificados {
    byte[] emitir(SolicitudCertificado solicitud);
}
