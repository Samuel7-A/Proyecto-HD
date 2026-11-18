# Informe APF3 – LanReserve

## 1. Objetivo del avance

El tercer Avance de Proyecto Final (APF3) tiene como objetivo documentar la optimización y la refactorización del repositorio de **LanReserve**, y la poda de ramas que dejó el repositorio limpio y ordenado.

Se describen los criterios usados para decidir qué ramas eliminar, el procedimiento y los comandos de la poda, las verificaciones de que no se perdió ningún commit ni se rompió ningún Pull Request, y las evidencias del resultado. También se registran las refactorizaciones integradas a `develop` durante el avance.

## 2. Estado inicial del repositorio
Antes de la poda, el repositorio acumulaba ramas de las funcionalidades ya integradas a `develop` mediante Pull Request, además de un hotfix y una rama sin PR asociado. Cada rama se cruzó contra el historial de merges de `develop` (`git log origin/develop --merges`) para confirmar su PR y su fecha de integración.
## 3. Criterios de poda

Una rama solo se eliminaba si cumplía estas condiciones:
- No tenía un Pull Request abierto.
- Su último commit ya estaba contenido en `develop` (o en `main`, en el caso del hotfix), es decir, no tenía commits sin integrar.
- No era una rama de integración (`main`, `develop`) ni tenía un PR pendiente de revisión (`docs/informe-apf2`, PR #23).

Primero se integraron los trabajos pendientes a `develop`, y después se verificó cada rama antes de eliminarla. 

| Rama | PR | Merge a develop |
|---|---|---|
| feature/auth | #1 | 2026-09-02 |
| feature/reservas | #2 | 2026-09-14 |
| feature/bd-docker | #4 (reutilizada en #34) | 2026-09-14 |
| feature/frontend-operador | #6 | 2026-10-17 |
| feature/auth-completo | #7 | 2026-09-17 |
| feature/frontend-cliente | #8 | 2026-10-17 |
| feature/docs-flujos | #9 | 2026-10-17 |
| feature/registro-presencial | #12 | 2026-10-17 |
| fix/arranque-datos | #13 | 2026-10-18 |
| chore/ci | #14 | 2026-10-18 |
| fix/disponibilidad-render | #15 | 2026-10-18 |
| feature/consola-controller | #16 | 2026-10-18 |
| feature/maquina | #17 | 2026-10-18 |
| feature/disponibilidad-controller | #18 | 2026-10-18 |
| feature/reserva-repository | #19 | 2026-10-19 |
| docs/readme-develop | #20 | 2026-10-19 |
| feature/reserva-controller | #25 | 2026-10-19 |
| feature/redesign-cliente | #27 | 2026-10-20 |
| fix/consola-reservas-reales | #26 | 2026-10-21 |
| feature/panel-administrador | #28 | 2026-10-21 |
| feature/frontend-admin-operador | #29 | 2026-10-21 |
| release/v0.2.0 | #30 | 2026-10-21 |
## 4. Procedimiento de poda (comandos y verificaciones)
## 5. Resultado

La poda redujo el repositorio de **28 a 3 ramas remotas** (25 eliminadas, reducción del 89,29 %). Quedaron `main`, `develop` y `docs/informe-apf2`, esta última por tener el PR #23 abierto.

| Ramas eliminadas | Cantidad | Verificación |
|---|---|---|
| Con PR mergeado a `develop` | 23 | Último commit contenido en `develop` |
| Hotfix (`hotfix/docs-readme`, PR #24) | 1 | Último commit contenido en `main` |
| Sin PR (`Rama-Secundaria`) | 1 | Commit `852dcc1f` contenido en `main` y `develop` |

La poda cerró con `develop` en `d2872a4` (merge del PR #36) y el CI en verde.
## 6. Optimización y refactorización
## 7. Evidencias
## 8. Tablero Kanban