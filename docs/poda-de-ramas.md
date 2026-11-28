# Poda de ramas — LanReserve

**Proyecto:** LanReserve (Proyecto-HD) — Herramientas de Desarrollo II
**Avance:** APF3 — Optimización
**Fecha de ejecución:** 17 de noviembre de 2026
**Responsable:** Keny Evangelista (Kaiilo1020)
**Repositorio:** https://github.com/Samuel7-A/Proyecto-HD

---

## 1. Política de ramas del equipo

El equipo trabaja con un flujo derivado de **Git Flow**, adaptado al tamaño del
proyecto. Solo se admiten ramas vivas de estos tipos:

| Tipo | Nombre | Vida útil | Se borra al integrarse |
|---|---|---|---|
| Permanente | `main` | Indefinida | Nunca |
| Permanente | `develop` | Indefinida | Nunca |
| Trabajo | `feature/<tema>` | Mientras dure la funcionalidad | Sí |
| Corrección | `fix/<tema>` | Mientras dure la corrección | Sí |
| Documentación | `docs/<tema>` | Mientras dure la redacción | Sí |
| Tareas de infra | `chore/<tema>`, `infra/<tema>` | Mientras dure la tarea | Sí |
| Refactorización | `refactor/<tema>` | Mientras dure el refactor | Sí |
| Liberación | `release/<version>` | Hasta publicar la versión | Sí |
| Emergencia | `hotfix/<tema>` | Hasta aplicar el parche | Sí |

**Regla central:** ninguna rama de trabajo sobrevive a la fusión de su Pull
Request. La rama es una etiqueta móvil que apunta a commits; una vez que esos
commits están dentro de `develop` (o de `main`), la etiqueta deja de aportar
información y solo ensucia el historial.

---

## 2. Criterio de poda

Una rama se puede eliminar **si y solo si** se cumplen las tres condiciones:

1. **Está integrada.** Todos sus commits ya existen en `develop` o en `main`.
   Se comprueba con la relación de ancestro de Git.
2. **No tiene Pull Request abierto.** Si el PR sigue abierto, borrar la rama
   deja el PR imposible de actualizar y de fusionar.
3. **No es una rama permanente.** `main` y `develop` nunca se tocan, sin
   excepciones.

Una rama con commits propios que no estén en `develop` ni en `main` **no se
borra**, aunque parezca vieja. Esos commits se perderían.

---

## 3. Verificación previa y salvaguardas

### 3.1 Comprobación de integración

Antes de borrar nada, cada rama se clasifica con `git merge-base --is-ancestor`:

```bash
git merge-base --is-ancestor origin/<rama> origin/develop && echo INTEGRADA
git merge-base --is-ancestor origin/<rama> origin/main   && echo INTEGRADA
```

Si la rama es ancestro de cualquiera de las dos, todos sus commits ya están
dentro y borrarla no elimina trabajo.

### 3.2 Creación del Pull Request antes de la poda

Además de los commits, se conserva **la conversación**. Al fusionar un PR queda
registrado de forma permanente:

- el diff completo y la discusión,
- quién revisó y quién aprobó,
- el commit de fusión,
- la rama de origen y de destino.

Por eso la rama temporal puede desaparecer sin que se pierda trazabilidad: el
historial de la decisión vive en el Pull Request, no en el nombre de la rama.

### 3.3 Las tres salvaguardas aplicadas

| # | Salvaguarda | Cómo se comprobó |
|---|---|---|
| 1 | Nunca borrar una rama con PR abierto | `gh pr list --state open` antes de la poda; las tres ramas con PR abierto (`docs/informe-apf2`, `feature/bd-docker`, `infra/proteccion-ramas`) se excluyeron explícitamente |
| 2 | Nunca borrar una rama con commits fuera de `develop` y `main` | `git merge-base --is-ancestor` sobre las 28 ramas; solo pasaron las 23 integradas |
| 3 | Nunca borrar `main` ni `develop` | Excluidas por lista explícita antes de generar los comandos |

### 3.4 Consecuencia de un error

Aunque una rama se borre por error, los commits siguen accesibles por su SHA
durante el tiempo de retención de GitHub, y se puede recrear la referencia:

```bash
git push origin <sha-del-commit>:refs/heads/<rama-recuperada>
```

Esto reduce el riesgo, pero **no sustituye a la verificación previa**.

---

## 4. Ejecución

### 4.1 Estado de partida

```bash
git fetch --prune origin
gh api "repos/Samuel7-A/Proyecto-HD/branches?per_page=100" --jq 'length'
# 28
```

### 4.2 Clasificación

Se listaron las 28 ramas y se clasificaron en tres grupos:

- **2 permanentes**, que nunca se tocan: `main` y `develop`.
- **3 con trabajo vivo**, excluidas porque tenían un Pull Request abierto:
  `docs/informe-apf2` (PR #23), `feature/bd-docker` (PR #34) e
  `infra/proteccion-ramas` (PR #36). Esta última además no estaba integrada:
  sus commits aún no existían en `develop`.
- **23 integradas**, todas candidatas a poda.

La comprobación fue: `2 + 3 + 23 = 28`, el total de partida.

### 4.3 Borrado

Se ejecutó el borrado rama por rama, no en lote ciego, para que cada operación
quedara registrada de forma individual y fuera verificable:

```bash
git push origin --delete <rama>
```

Cada operación respondió `- [deleted]`. Las que responden
`remote ref does not exist` ya no existían y no se consideran error.

### 4.4 Verificación posterior

```bash
gh api "repos/Samuel7-A/Proyecto-HD/branches?per_page=100" --jq 'length'
# 3
```

Se comprobó además, con consultas individuales a la API, que las ramas
eliminadas devuelven `404 Branch not found` y que los Pull Requests abiertos
siguen intactos.

### 4.5 Resultado

| Métrica | Antes | Después |
|---|---|---|
| Ramas remotas | 28 | 3 |
| Reducción | — | **89 %** |
| PRs afectados | — | **0** |
| Commits perdidos | — | **0** |

---

## 5. Evidencia

### 5.1 Las 23 ramas eliminadas y el PR que integró cada una

| # | Rama eliminada | Integrada por | Fecha |
|---|---|---|---|
| 1 | `Rama-Secundaria` | *(ejercicio inicial de ramificación, sin PR)* | — |
| 2 | `chore/ci` | PR #14 | 18-oct-2026 |
| 3 | `docs/readme-develop` | PR #20 | 19-oct-2026 |
| 4 | `feature/auth` | PR #1 | 02-sep-2026 |
| 5 | `feature/auth-completo` | PR #7 | 18-sep-2026 |
| 6 | `feature/consola-controller` | PR #16 | 18-oct-2026 |
| 7 | `feature/disponibilidad-controller` | PR #18 | 19-oct-2026 |
| 8 | `feature/docs-flujos` | PR #9 | 18-oct-2026 |
| 9 | `feature/frontend-admin-operador` | PR #29 | 21-oct-2026 |
| 10 | `feature/frontend-cliente` | PR #8 | 18-oct-2026 |
| 11 | `feature/frontend-operador` | PR #6 | 18-oct-2026 |
| 12 | `feature/maquina` | PR #17 | 18-oct-2026 |
| 13 | `feature/panel-administrador` | PR #28 | 21-oct-2026 |
| 14 | `feature/redesign-cliente` | PR #27 | 21-oct-2026 |
| 15 | `feature/registro-presencial` | PR #12 | 18-oct-2026 |
| 16 | `feature/reserva-controller` | PR #25 | 19-oct-2026 |
| 17 | `feature/reserva-repository` | PR #19 | 19-oct-2026 |
| 18 | `feature/reservas` | PR #2 | 14-sep-2026 |
| 19 | `fix/arranque-datos` | PR #13 | 18-oct-2026 |
| 20 | `fix/consola-reservas-reales` | PR #26 | 21-oct-2026 |
| 21 | `fix/disponibilidad-render` | PR #15 | 18-oct-2026 |
| 22 | `hotfix/docs-readme` | PR #24 | 19-oct-2026 |
| 23 | `release/v0.2.0` | PR #30 | 21-oct-2026 |

**22 de las 23 ramas tienen su Pull Request documentado.** La única excepción es
`Rama-Secundaria`, del ejercicio inicial de ramificación del APF1: su contenido
ya estaba en `main` y nunca tuvo PR propio.

### 5.2 Las ramas que se conservaron y por qué

| Rama | Motivo |
|---|---|
| `main` | Rama permanente y estable |
| `develop` | Rama permanente de integración |
| `docs/informe-apf2` | PR #23 abierto — se poda al fusionarse |
| `feature/bd-docker` | PR #34 abierto — se poda al fusionarse |
| `infra/proteccion-ramas` | PR #36 abierto — se poda al fusionarse |

Las tres ramas de trabajo se podaron **el mismo día** en que se fusionaron sus
respectivos Pull Requests, lo que confirma que la política se aplica de forma
sistemática y no solo una vez.

### 5.3 Estado final

```
main
develop
docs/informe-apf2   ← única superviviente de trabajo, a la espera del PR #23
```

---

## 6. Mantenimiento permanente

La poda no es una limpieza puntual: es una práctica. Para que el repositorio no
vuelva a acumular ramas se dejaron tres mecanismos activos.

### 6.1 Borrado automático al fusionar

En **Settings → Pull Requests** se activó
`Automatically delete head branches`.

Con esta opción, GitHub elimina la rama de origen en el momento de confirmar la
fusión. La poda deja de depender de que alguien se acuerde.

### 6.2 Protección de `develop` y `main`

En **Settings → Branches** se configuró, para ambas ramas:

- `Require a pull request before merging`
- **1 aprobación** obligatoria
- `Require status checks to pass` con el check **build** obligatorio

Con esto, ningún cambio entra sin revisión y sin pasar las pruebas. Además, el
flujo obliga a que cada integración tenga su PR, lo que garantiza que **toda
rama pueda podarse después con trazabilidad completa**.

### 6.3 Hábito de equipo

La regla operativa queda escrita en el contrato de trabajo del equipo y se repite
en cada revisión:

> Cada tarea nace como issue, se trabaja en su rama, se integra con un Pull
> Request revisado y, al fusionarse, la rama se elimina.

### 6.4 Comprobación periódica

Una vez por avance, se ejecuta:

```bash
gh api "repos/Samuel7-A/Proyecto-HD/branches?per_page=100" --jq '.[].name'
```

Si aparece alguna rama cuyo PR ya está fusionado, se poda con el mismo
procedimiento de la sección 3.

---

## 7. Resumen

| | |
|---|---|
| Ramas antes | 28 |
| Ramas después | 3 |
| Ramas eliminadas | 23 |
| Reducción | 89 % |
| Pull Requests afectados | 0 |
| Commits perdidos | 0 |
| Ramas con PR documentado | 22 de 23 |
| Automatización | Borrado automático activado |

La poda convirtió un repositorio con 28 ramas —la mayoría ya integradas y sin
función— en uno con tres referencias vivas y significativas, sin perder un solo
commit y conservando la trazabilidad completa de cada integración en su Pull
Request.
