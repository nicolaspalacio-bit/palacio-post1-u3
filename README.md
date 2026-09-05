<div align="center">

# 🎓 ConfUDES — Patrones Estructurales

**Post-contenido · Unidad 3 · Patrones de Diseño de Software**

*Decisión y aplicación de patrones estructurales sobre el backend real de una plataforma de gestión de congresos académicos*

![Java](https://img.shields.io/badge/Java-17-orange?logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.0-6DB33F?logo=springboot&logoColor=white)
![Maven](https://img.shields.io/badge/Maven-3.8+-C71A36?logo=apachemaven&logoColor=white)
![JUnit](https://img.shields.io/badge/JUnit-5-25A162?logo=junit5&logoColor=white)
![Tests](https://img.shields.io/badge/tests-9%2F9%20passing-brightgreen)
![Status](https://img.shields.io/badge/estado-completo-success)

**Autor:** Nicolas Andres Palacio Boada · Ingeniería de Sistemas · UDES, sede Cúcuta

</div>

---

## 📑 Tabla de contenido

1. [Descripción](#-descripción)
2. [Estructura del proyecto](#-estructura-del-proyecto)
3. [Cómo ejecutar](#-cómo-ejecutar)
4. [Parte 1 — Decisiones de diseño](#-parte-1--decisiones-de-diseño)
5. [Parte 2 — Decisiones de diseño](#-parte-2--decisiones-de-diseño)
6. [Reflexión — Composite y Flyweight](#-reflexión--composite-y-flyweight-no-rubricada)
7. [Herramientas utilizadas](#-herramientas-utilizadas)
8. [Conclusiones](#-conclusiones)

---

## 📋 Descripción

Este repositorio resuelve el post-contenido de la Unidad 3 en un único proyecto Spring Boot
(`confudes-patrones-estructurales`) que interviene el backend real de **ConfUDES**, la plataforma
interna de la universidad para el registro de asistencia y la emisión de certificados de sus
congresos académicos. Cada una de las cuatro necesidades reportadas por el equipo de mantenimiento
se resolvió aplicando **un** patrón estructural GoF, sin modificar ninguna de las clases entregadas
como código dado y sin fusionar responsabilidades que el enunciado exige mantener separadas.

| # | Necesidad | Síntoma de diseño | Patrón aplicado |
|---|---|---|---|
| 1 | Registro de asistencia con `QRCheckAPI` | Un colaborador externo con contrato incompatible que el resto del sistema no debe conocer | 🔌 **Adapter** |
| 2 | Emisión de certificados con 4 colaboradores | Un controlador que orquesta demasiados colaboradores conocidos directamente | 🏛️ **Facade** |
| 3 | Mejoras opcionales y combinables del PDF | Comportamiento apilable en combinaciones que no deben multiplicar clases | 🎁 **Decorator** |
| 4 | Control de acceso a la descarga masiva | Una operación costosa que debe negarse *antes* de ejecutarse según el rol | 🛡️ **Proxy** |

---

## 🗂️ Estructura del proyecto

```
palacio-post1-u3/
├── pom.xml
├── README.md
└── src/
    ├── main/java/com/universidad/confudes/
    │   ├── ConfUdesApp.java
    │   ├── externo/qrcheck/                        (DADO — SDK del proveedor)
    │   │   ├── QRCheckClient.java
    │   │   ├── QRCheckRequest.java
    │   │   └── QRCheckResponse.java
    │   ├── asistencia/                              Necesidad 1
    │   │   ├── ServicioAsistencia.java              (DADO — contrato interno)
    │   │   ├── ResultadoCheckIn.java                (DADO)
    │   │   ├── ControladorCheckIn.java               (DADO — no modificado)
    │   │   └── ServicioAsistenciaAdapter.java        🔌 Adapter — SOLUCIÓN
    │   ├── certificados/                             Necesidades 2 y 3
    │   │   ├── ValidadorAsistencia.java              (DADO)
    │   │   ├── GeneradorCertificadoPDF.java          (DADO)
    │   │   ├── FirmaDigitalService.java              (DADO)
    │   │   ├── EnvioCorreoService.java               (DADO)
    │   │   ├── UtilidadesPDF.java                    (DADO)
    │   │   ├── ServicioCertificados.java              contrato formal (Paso 7)
    │   │   ├── SolicitudCertificado.java              DTO de la solicitud
    │   │   ├── AsistenciaInsuficienteException.java   excepción de dominio
    │   │   ├── EmisionCertificadoFacade.java         🏛️ Facade — SOLUCIÓN Nec. 2
    │   │   ├── ControladorCertificados.java           (DADO, refactorizado)
    │   │   ├── MejoraCertificadoDecorator.java        🎁 base del Decorator — Nec. 3
    │   │   ├── MarcaDeAguaDecorator.java              🎁 mejora combinable
    │   │   ├── CodigoQRDecorator.java                 🎁 mejora combinable
    │   │   └── TraduccionDecorator.java               🎁 mejora combinable
    │   └── acceso/                                    Necesidad 4
    │       ├── ContextoUsuario.java                   (DADO)
    │       └── ServicioCertificadosControlAcceso.java 🛡️ Proxy — SOLUCIÓN
    └── test/java/com/universidad/confudes/
        ├── asistencia/CheckInIntegracionTest.java
        ├── certificados/EmisionCertificadoTest.java
        ├── certificados/MejorasCertificadoTest.java
        └── acceso/AccesoDescargaMasivaTest.java
```

> Ninguna clase marcada como **(DADO)** fue modificada. `ControladorCertificados` es la única
> excepción explícita del enunciado: se refactorizó su forma de construcción sin tocar los cuatro
> servicios de los que dependía.

---

## ▶️ Cómo ejecutar

```bash
mvn clean package
mvn spring-boot:run
mvn test
```

Los cuatro archivos de prueba (uno por necesidad) validan **comportamiento**, no una clase concreta:
`mvn test` debe ejecutar 9 pruebas en verde sobre las cuatro necesidades.

---

## 🧩 Parte 1 — Decisiones de diseño

### Necesidad 1 — Registro de asistencia (`asistencia/ServicioAsistenciaAdapter`)

**Patrón aplicado: Adapter.**

**Síntoma de diseño.** `ControladorCheckIn` (ya en producción) depende únicamente de la interfaz
interna `ServicioAsistencia`. El proveedor `QRCheckAPI` entrega un SDK (`QRCheckClient`) con un
contrato propio e incompatible: recibe un `QRCheckRequest` que exige `idEvento` como `long` (el
sistema maneja `eventoId` como `String`) y responde con un `QRCheckResponse` cuyo significado
(código `200`/`401`) no coincide con el `ResultadoCheckIn` (`boolean` + mensaje) que el resto del
sistema espera. Es **un único colaborador externo** cuya forma de hablar no calza con la forma de
hablar que el sistema ya tiene.

**Alternativa descartada: Facade.** Se consideró envolver `QRCheckClient` en una fachada de
"registro de asistencia". Se descartó porque una Facade resuelve *demasiados colaboradores conocidos
por el cliente*, no una *incompatibilidad de contrato con un único colaborador*. Aquí solo hay un
colaborador externo (`QRCheckClient`); el problema no es de cantidad sino de traducción: `eventoId`
debe convertirse a `long`, y el código de respuesta del proveedor debe traducirse al vocabulario de
`ResultadoCheckIn`. Una Facade no resolvería esa traducción de tipos y semántica —seguiría exigiendo
que alguien tradujera `QRCheckResponse` a `ResultadoCheckIn`—, así que en el fondo terminaría
conteniendo un Adapter de todos modos. Aplicar directamente Adapter es más preciso y evita una capa
de indirección sin propósito adicional.

**Decisión de implementación.** `ServicioAsistenciaAdapter implements ServicioAsistencia` recibe
`QRCheckClient` por inyección de dependencias y traduce en ambas direcciones: convierte `eventoId`
(String) a `idEvento` (long) extrayendo su parte numérica, reenvía `credencialQR` como el `payload`
que el proveedor espera (que ya llega con el prefijo `QR-` cuando es válida, por convención del
sistema — el adaptador no *fuerza* ese prefijo sobre una credencial que no lo trae, porque eso
convertiría artificialmente en válida una credencial inválida), y traduce el código de respuesta
(`200` → éxito, cualquier otro → fallo) reutilizando el `detalle` del proveedor como mensaje.
`ControladorCheckIn` y `ServicioAsistencia` no se modificaron.

---

### Necesidad 2 — Emisión de certificados (`certificados/EmisionCertificadoFacade`)

**Patrón aplicado: Facade.**

**Síntoma de diseño.** `ControladorCertificados` orquestaba directamente cuatro servicios
(`ValidadorAsistencia`, `GeneradorCertificadoPDF`, `FirmaDigitalService`, `EnvioCorreoService`), cada
uno con una API que funciona perfectamente tal como está. El problema **no** es de compatibilidad de
interfaces —ninguno de los cuatro tiene un contrato que el resto del sistema ya espere y que resulte
incompatible—, sino de **cuántos colaboradores debe conocer el cliente** y en qué orden coordinarlos
(validar → generar → firmar → enviar).

**Alternativa descartada: Adapter.** Se descartó explícitamente porque no hay ninguna interfaz
incompatible que traducir: los cuatro servicios ya exponen métodos que `ControladorCertificados`
podía invocar sin ningún problema de tipos o formatos. Envolver cada uno en un Adapter no reduciría
el número de dependencias del controlador ni resolvería el verdadero síntoma (mantenibilidad
degradada por conocer y coordinar cuatro colaboradores en el mismo método). Adapter resuelve *una*
incompatibilidad uno a uno; aquí no hay ninguna que resolver, hay una *orquestación* que esconder.

**Decisión de implementación.** `EmisionCertificadoFacade implements ServicioCertificados` recibe
los cuatro servicios por constructor y expone una única operación, `emitir(SolicitudCertificado)`,
que encapsula toda la secuencia. `ControladorCertificados` se refactorizó para declarar un único
constructor con un único parámetro (`ServicioCertificados`), y su método `emitir()` del endpoint
quedó en 6 líneas de cuerpo (por debajo del límite de 10 exigido). Los cuatro servicios no se
modificaron ni se fusionaron: siguen siendo reutilizables por otros módulos de ConfUDES, la Facade
solo les da un único punto de coordinación para este caso de uso.

---

## 🧵 Parte 2 — Decisiones de diseño

### Necesidad 3 — Mejoras combinables del certificado (`certificados/*Decorator`)

**Patrón aplicado: Decorator.**

**Síntoma de diseño.** Sobre el PDF ya emitido por `ServicioCertificados`, los organizadores piden
activar —por evento, en cualquier combinación— tres mejoras (marca de agua, código QR de
verificación, traducción al inglés), apoyadas en `UtilidadesPDF` (no modificable). Con tres mejoras
hay ocho combinaciones posibles, y el enunciado exige que agregar o quitar una mejora no cree una
clase nueva por combinación.

**Alternativas descartadas (Punto de decisión 3):**

- **Herencia (una subclase por combinación).** Con 3 mejoras ya se necesitarían hasta 8 subclases de
  `ServicioCertificados` (`ConMarcaDeAgua`, `ConMarcaDeAguaYQR`, `ConMarcaDeAguaYQRYTraduccion`,
  …), y cada mejora nueva multiplicaría el número de subclases existentes por dos. Es exactamente el
  problema de **jerarquías paralelas** que Bridge fue diseñado para evitar en el taller
  pre-contenido, y que aquí se manifiesta como una explosión combinatoria de subclases si se intenta
  resolver por herencia en lugar de composición.
- **Parámetros booleanos en `emitir()`** (`activarMarcaDeAgua`, `activarQR`, `activarTraduccion`).
  Funciona para tres mejoras, pero cada mejora nueva exige modificar la firma del método —y a todo
  el que ya lo invoca— violando el Principio de Abierto/Cerrado. Además mezcla en un único método la
  decisión de *qué* mejoras aplicar con la lógica de *cómo* aplicarlas, dificultando probar cada
  mejora de forma aislada.

Ninguna de las dos escala igual de bien que Decorator cuando el número de mejoras crece: Decorator
mantiene el crecimiento **aditivo** (una clase por mejora, no por combinación), mientras que las dos
alternativas descartadas crecen de forma multiplicativa o rompen la firma existente.

**Decisión de implementación.** `MejoraCertificadoDecorator` (clase base abstracta) implementa
`ServicioCertificados` y envuelve otro `ServicioCertificados` (el colaborador base de la Necesidad 2
o cualquier otra mejora ya aplicada). `MarcaDeAguaDecorator`, `CodigoQRDecorator` y
`TraduccionDecorator` son sus tres implementaciones concretas: cada una delega primero en el objeto
envuelto y aplica su propia transformación con `UtilidadesPDF` sobre el resultado. Se combinan
anidando constructores en cualquier orden, por ejemplo:

```java
ServicioCertificados conTodo =
    new MarcaDeAguaDecorator(new CodigoQRDecorator(new TraduccionDecorator(colaboradorBase)));
```

Solo existen 3 clases nuevas para 8 combinaciones posibles, y `EmisionCertificadoFacade` (el
colaborador base de la Necesidad 2) no fue modificado.

---

### Necesidad 4 — Control de acceso a la descarga masiva (`acceso/ServicioCertificadosControlAcceso`)

**Patrón aplicado: Proxy (proxy de protección).**

**Síntoma de diseño.** La descarga masiva de certificados de un evento es una operación costosa
(el proveedor de firma digital limita las llamadas a 60 por minuto, y cada certificado requiere al
menos una) y restringida (solo `ORGANIZADOR` o `ADMIN` pueden solicitarla). El resto del sistema
—incluido el flujo de emisión individual de la Necesidad 2, usado por los propios participantes—
debe seguir inyectando `ServicioCertificados` sin conocer roles ni límites del proveedor.

**Alternativa descartada: el patrón de la Necesidad 3 (Decorator) — Punto de decisión 4.** Esta es
la comparación más sutil del laboratorio: tanto Decorator como Proxy envuelven un objeto que
implementa `ServicioCertificados` con otro objeto que implementa la misma interfaz, y
estructuralmente ambos se ven casi idénticos. La diferencia está en la **intención**:

| | Decorator (Necesidad 3) | Proxy (Necesidad 4) |
|---|---|---|
| Pregunta que responde | "¿Qué más debe hacer este objeto?" | "¿Debe permitirse esta llamada?" |
| Relación con la llamada real | **Siempre** termina delegando en el objeto real | **Decide** si delega o no; puede sustituir por completo el acceso sin que el objeto real llegue a ejecutarse |
| Efecto en el flujo | Añade capacidades, complementa | Condiciona el flujo, puede negarlo |

Si se usara el patrón de la Necesidad 4 (Proxy) para resolver la Necesidad 3, cada mejora tendría
que decidir si "deja pasar" o no la llamada, lo cual no tiene sentido para una marca de agua o una
traducción —siempre deben aplicarse si están activas— y dejaría sin resolver el problema real de
combinarlas libremente sin explosión de clases. A la inversa, si se usara el patrón de la Necesidad
3 (Decorator) para el control de acceso, el decorador ejecutaría igual la lógica costosa de emisión
*antes* de poder decidir si el usuario tiene permiso, porque Decorator siempre delega: el rechazo
llegaría demasiado tarde, después de haber gastado las llamadas al proveedor de firma que se
querían evitar. Solo Proxy puede **interceptar y rechazar sin ejecutar** el objeto real.

**Decisión de implementación.** `ServicioCertificadosControlAcceso implements ServicioCertificados`
envuelve el `ServicioCertificados` real y, antes de delegar, consulta `ContextoUsuario.rolActual()`
(no modificado). Si el rol no es `ORGANIZADOR` ni `ADMIN`, lanza `SecurityException` **sin invocar**
al objeto envuelto —la lógica costosa de emisión nunca se ejecuta—; si el rol es válido, delega
normalmente. El flujo de emisión individual de la Necesidad 2 (usado por los participantes) inyecta
`ServicioCertificados` sin ningún cambio y sin saber que este proxy existe: solo se coloca delante
de la implementación usada específicamente para la descarga masiva de organizadores.

---

## 🔍 Reflexión — Composite y Flyweight (no rubricada)

**Agenda del congreso (tracks → sesiones → actividades).** De los siete patrones estructurales de
la guía, **Composite** encajaría de forma natural en la agenda de cada congreso: un track contiene
sesiones y una sesión contiene actividades, una estructura jerárquica árbol-de-partes donde el
cliente querría tratar un track completo, una sesión individual o una actividad suelta de manera
uniforme (por ejemplo, calcular la duración total de un track sumando recursivamente la duración de
sus sesiones y actividades, sin que el código cliente necesite distinguir si está parado sobre un
nodo compuesto o una hoja).

**Credenciales QR y Flyweight.** Con miles de credenciales QR generadas (una por participante), el
patrón Flyweight —pensado para reducir memoria compartiendo el estado *intrínseco* común entre
muchos objetos similares— no amerita aplicarse aquí: cada credencial QR tiene datos únicos e
irrepetibles (el payload codifica al participante y al evento), no hay estado compartible entre
credenciales distintas. Flyweight ayuda cuando miles de objetos comparten la mayor parte de su
estado y solo difieren en una porción pequeña y extrínseca (por ejemplo, glifos de una fuente
tipográfica reutilizados por miles de caracteres); no es el caso de una credencial que en su
totalidad es específica de un único asistente.

---

## 🛠️ Herramientas utilizadas

- Java 17, Spring Boot 3.2.0, Apache Maven, JUnit 5
- Visual Studio Code / IntelliJ IDEA Community
- Git y GitHub

---

## ✅ Conclusiones

Las cuatro necesidades confirman que la distinción entre patrones estructurales rara vez la da la
forma del diagrama de clases —Adapter, Facade, Decorator y Proxy comparten en distinto grado el
mecanismo de composición y delegación— sino la intención de diseño: traducir un contrato
incompatible con un único colaborador (Adapter), esconder la coordinación de varios colaboradores
detrás de una operación simple (Facade), añadir responsabilidades combinables que siempre delegan
(Decorator), o condicionar si la llamada llega a ejecutarse (Proxy). La comparación más difícil fue,
sin duda, Decorator contra Proxy en las Necesidades 3 y 4: ambos envuelven la misma interfaz con la
misma forma estructural, y solo la pregunta "¿esto decide si la llamada ocurre, o solo qué más hace
el objeto?" permitió separarlos con claridad. Le siguió en dificultad diferenciar Adapter de Facade
en las Necesidades 1 y 2, resuelto con la prueba de retirar mentalmente la clase envolvente: si el
cliente seguiría necesitando hablarle a un único colaborador con una firma distinta, es Adapter; si
tendría que orquestar varios colaboradores en orden, es Facade. En los cuatro casos, aplicar el
patrón "vecino" habría producido código que compila y hasta funciona en el caso feliz, pero que
no resuelve el problema real reportado por el equipo de mantenimiento de ConfUDES.

---

<div align="center">

*Post-contenido — Unidad 3: Patrones Estructurales · Patrones de Diseño de Software · UDES*

</div>
