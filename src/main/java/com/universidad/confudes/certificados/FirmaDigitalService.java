package com.universidad.confudes.certificados;

public class FirmaDigitalService {
    public Sesion abrirSesion(String certificadoInstitucional) {
        System.out.println("Abriendo sesión de firma con " + certificadoInstitucional);
        return new Sesion("sesion-" + System.currentTimeMillis());
    }

    public byte[] firmar(Sesion sesion, byte[] documento) {
        System.out.println("Firmando documento en sesión " + sesion.getId());
        return documento; // simulación
    }

    public void cerrarSesion(Sesion sesion) {
        System.out.println("Cerrando sesión " + sesion.getId());
    }

    public static class Sesion {
        private final String id;
        public Sesion(String id) { this.id = id; }
        public String getId() { return id; }
    }
}
