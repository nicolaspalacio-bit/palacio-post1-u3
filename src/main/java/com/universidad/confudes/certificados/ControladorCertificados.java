package com.universidad.confudes.certificados;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

// Refactorizado (Necesidad 2): ya no conoce los cuatro servicios de emisión,
// solo depende del colaborador ServicioCertificados (Facade en producción,
// o cualquier decorador/proxy que también implemente ese mismo contrato).
@RestController
@RequestMapping("/api/certificados")
public class ControladorCertificados {

    private final ServicioCertificados servicioCertificados;

    public ControladorCertificados(ServicioCertificados servicioCertificados) {
        this.servicioCertificados = servicioCertificados;
    }

    @PostMapping("/{eventoId}/{participanteId}")
    public ResponseEntity<String> emitir(@PathVariable String eventoId, @PathVariable String participanteId,
                                          @RequestParam String nombre, @RequestParam String correoDestino) {
        try {
            SolicitudCertificado solicitud = new SolicitudCertificado(eventoId, participanteId, nombre, correoDestino);
            servicioCertificados.emitir(solicitud);
            return ResponseEntity.ok("Certificado emitido y enviado");
        } catch (AsistenciaInsuficienteException ex) {
            return ResponseEntity.status(403).body(ex.getMessage());
        }
    }
}
