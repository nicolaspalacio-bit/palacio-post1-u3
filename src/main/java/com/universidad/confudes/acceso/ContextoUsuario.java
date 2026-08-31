package com.universidad.confudes.acceso;

// Provisto por el módulo de autenticación de ConfUDES - no modificar.
// En producción lee el rol desde el token de la petición actual.
public class ContextoUsuario {
    public static String rolActual() {
        return System.getProperty("confudes.rol", "PARTICIPANTE");
    }
}
