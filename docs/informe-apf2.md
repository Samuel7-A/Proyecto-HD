# Informe APF2 – LanReserve

## 1. Objetivo del avance

El segundo Avance de Proyecto Final (APF2) tiene como objetivo documentar y evidenciar las prácticas de colaboración, control de versiones e integración continua aplicadas durante el desarrollo de **LanReserve**, una plataforma web para la reserva de máquinas en centros de cómputo y lan centers.

Durante este avance se registra la organización del equipo mediante Git y GitHub, el uso de ramas para desarrollar funcionalidades, la creación y revisión de Pull Requests (PR) y la integración de los cambios en la rama `develop`.

Asimismo, se documenta la implementación de un flujo de integración continua mediante GitHub Actions, encargado de compilar la aplicación desarrollada con Spring Boot y Java 21, así como ejecutar las pruebas automatizadas mediante Maven y JDK 21 en el entorno de CI.

Finalmente, se presentan evidencias del trabajo colaborativo, las revisiones de código, las ejecuciones del pipeline y los errores detectados durante el proceso de integración, con el propósito de evaluar las prácticas utilizadas por el equipo y mantener un historial verificable de los cambios realizados.

## 2. Estructura del documento

El presente informe está organizado en las siguientes secciones:

- **Sección 1. Objetivo del avance:** describe la finalidad del APF2 y las actividades de colaboración e integración realizadas.
- **Sección 2. Estructura del documento:** presenta la organización del informe.
- **Sección 3. Roles y ramas del equipo:** identifica a los integrantes, sus responsabilidades, las ramas utilizadas y los Pull Requests asociados.
- **Sección 4. Estrategia de Git aplicada:** describe el modelo de ramificación, el flujo de trabajo con Pull Requests, las convenciones de commits, la integración continua, el proceso de release y hotfix, y la resolución de conflictos de fusión.
- **Sección 5. Exposición y enlace:** presenta el enlace al repositorio de LanReserve y los recursos relacionados con la exposición del avance.

## 3. Roles y ramas del equipo
| Integrante | Rol | Rama | PR | Merge |
|---|---|---|---|---|
| Samuel Torres | BD / DevOps | feature/bd-docker | #4 | 2026-09-14 |
| Samuel Torres | BD / DevOps | feature/registro-presencial | #12 | 2026-10-17 |
| Samuel Torres | BD / DevOps | chore/ci | #14 | 2026-10-18 |
| Keny Evangelista | Backend 1 | feature/auth | #1 | 2026-09-02 |
| Keny Evangelista | Backend 1 | feature/auth-completo | #7 | 2026-09-17 |
| Keny Evangelista | Backend 1 | fix/arranque-datos | #13 | 2026-10-18 |
| José Fernando Orellana | Backend 2 | feature/reservas | #2 | 2026-09-14 |
| José Fernando Orellana | Backend 2 | feature/maquina | #17 | 2026-10-18 |
| José Fernando Orellana | Backend 2 | feature/disponibilidad-controller | #18 | 2026-10-18 |
| Sebastián Quiroz | Frontend 1 | feature/frontend-cliente | #8 | 2026-10-17 |
| Sebastián Quiroz | Frontend 1 | fix/disponibilidad-render | #15 | 2026-10-18 |
| Christian Villacorta | Frontend 2 | feature/frontend-operador | #6 | 2026-10-17 |
| Christian Villacorta | Frontend 2 | feature/consola-controller | #16 | 2026-10-18 |
| Benjamin Mendez | Documentación / QA | feature/docs-flujos | #9 | 2026-10-17 |
| Benjamin Mendez | Documentación / QA | docs/informe-apf2 | (pendiente) | — |

PRs cerrados sin merge: #3 (Sebastián) y #5 (Christian, con cambios solicitados).
## 4. Estrategia de Git aplicada

### 4.1 Modelo de ramas
El equipo definió su modelo en `CONTRIBUTING.md`. En la práctica se usó así:

| Rama | Función | Ejemplo real |
|---|---|---|
| main | Versión estable; solo recibe releases vía PR | Pendiente: release v0.2.0 |
| develop | Integración del equipo | Recibió 14 PRs mergeados |
| feature/* | Funcionalidad nueva | feature/maquina (PR #17) |
| fix/* | Corrección de errores | fix/arranque-datos (PR #13) |
| chore/* | Infraestructura | chore/ci (PR #14) |
| release/* | Preparar una versión | Pendiente: release/v0.2.0 |
| hotfix/* | Arreglo urgente sobre main | Pendiente |

### 4.2 Flujo de trabajo
Cada integrante trabaja en su rama, abre un Pull Request hacia `develop` y
necesita la aprobación de al menos un compañero antes del merge.
- 14 PRs mergeados a `develop` (#1, #2, #4, #6, #7, #8, #9, #12 al #18), todos aprobados.
- 2 PRs cerrados sin merge: #3 y #5. El #5 recibió "Changes requested".
- Ejemplo de revisión: PR #13, aprobado por Samuel antes del merge.
- Issues enlazados a PRs: el issue #10 (registro presencial) se resolvió con el PR #12.
  Quedaron pendientes dos issues enlazados al trabajo ya hecho: #21 (módulo de reservas completo,
  relacionado con los PRs #2, #12, #18 y #19) y #22 (despliegue con Docker y CD, relacionado con
  los PRs #4 y #14).
- Se usó merge commit (en el historial figura "Merge pull request #N from ...").

### 4.3 Convención de commits
Se usan mensajes semánticos:
- `feat:` agregar entidad Reserva
- `fix:` configurar el datasource y la base en memoria para las pruebas
- `docs:` agregar flujos de casos de uso CU-05 y CU-12
- `chore:` agregar pipeline de integracion continua
- `refactor:` migrar el hash de contrasenas de SHA-256 a BCrypt

### 4.4 Integración continua
El PR #14 agregó el pipeline de GitHub Actions (`.github/workflows/build.yml`),
que compila y ejecuta las pruebas con Maven y JDK 21 (Temurin).
- Se ejecuta en cada push a `main`, `develop`, `feature/*` y `hotfix/*`.
- Se ejecuta en cada Pull Request hacia `main` o `develop`.

Evidencia (pestaña Actions, 11 ejecuciones: 10 exitosas y 1 fallida):
- La primera ejecución (build #1, PR #14) falló: la prueba `LancenterhdApplicationTests.contextLoads`
  dio el error "Failed to determine a suitable driver class" porque faltaba configurar la base
  de datos para las pruebas. Se corrigió con una base H2 de prueba y un `application.properties`
  de test. Al reabrir el PR, el build #2 pasó.
- 5 merges consecutivos a `develop` con CI verde: #14, #17, #15, #16 y #18
  (builds #3, #4, #5, #6 y #9).
- Con check previo al merge: #14, #18 y #19. Los #15, #16 y #17 se validaron
  al llegar a `develop`, porque se abrieron antes de que existiera el pipeline.

- **Resultados de las pruebas:** se adjunta captura del build #9 de GitHub Actions,
  donde se evidencia la ejecución de las 10 pruebas automatizadas, todas aprobadas.

  ![Build #9: 10 pruebas, 0 fallos, BUILD SUCCESS](imagenes/build-9-pruebas.png)

### 4.5 Release y hotfix
[Pendiente: PR del release v0.2.0, tag y hotfix, con enlaces y capturas.]

### 4.6 Resolución del conflicto de fusión
- **Documentación en el PR:** el conflicto y su resolución se describen en el PR #23 de `docs/informe-apf2` hacia `develop`.

  ![PR con la descripción del conflicto](imagenes/pr-informe-apf2.png)
  
**Conflicto detectado en la terminal:**

![Terminal con el CONFLICT en README.md](imagenes/conflicto-terminal.png)

**Zonas en conflicto en el editor:**

![Conflicto en stack tecnológico y estructura](imagenes/conflicto-stack-estructura.png)

![Conflicto en requisitos y ejecución](imagenes/conflicto-requisitos-ejecucion.png)

![Conflicto en estado del proyecto](imagenes/conflicto-estado.png)
## 5. Exposición y enlace

**Repositorio:** https://github.com/Samuel7-A/Proyecto-HD

El APF2 consolidó el trabajo con ramas, Pull Requests revisados e integración continua activa.