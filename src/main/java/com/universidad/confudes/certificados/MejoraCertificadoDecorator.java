package com.universidad.confudes.certificados;

/**
 * Necesidad 3 - Patrón Decorator.
 *
 * <p>Clase base para las mejoras opcionales y combinables del certificado
 * (marca de agua, código QR de verificación, traducción al inglés). Cada
 * mejora concreta envuelve un {@link ServicioCertificados} — el colaborador
 * base de la Necesidad 2 o cualquier otra mejora ya aplicada — e implementa
 * el mismo contrato, por lo que se puede apilar en cualquier orden y en
 * cualquier combinación sin crear una clase nueva por cada combinación
 * posible ni tocar el colaborador base.</p>
 */
public abstract class MejoraCertificadoDecorator implements ServicioCertificados {

    protected final ServicioCertificados delegado;

    protected MejoraCertificadoDecorator(ServicioCertificados delegado) {
        this.delegado = delegado;
    }
}
