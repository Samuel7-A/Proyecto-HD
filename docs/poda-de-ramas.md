# Poda de ramas - LanReserve

**Proyecto:** LanReserve (Proyecto-HD) - Herramientas de Desarrollo II
**Avance:** APF3 - Optimización
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
| 1 | Nunca borrar una rama con PR abierto | `gh pr list --state open` antes de la poda; tres ramas tenían un PR sin fusionar en ese momento (`docs/informe-apf2` PR #23, `feature/bd-docker` PR #34 e `infra/proteccion-ramas` PR #36) y se excluyeron de la lista de borrado |
| 2 | Nunca borrar una rama con commits fuera de `develop` y `main` | `git merge-base --is-ancestor` sobre las 28 ramas; solo pasaron las 23 que ya estaban dentro |
| 3 | Nunca borrar `main` ni `develop` | Excluidas por lista explícita antes de generar los comandos |

### 3.4 Consecuencia de un error

Aunque una rama se borre por error, los commits siguen accesibles por su SHA
durante el tiempo de retención de GitHub, y se puede recrear la referencia:

```bash
git push origin <sha-del-commit>:refs/heads/<rama-recuperada>
```

Esto reduce el riesgo, pero **no sustituye a la verificación previa**.

### 3.5 Los dos casos en que `merge-base` no alcanza

`git merge-base --is-ancestor` responde una sola pregunta: ¿está este commit
dentro de aquella historia? Hay dos situaciones del repositorio donde esa
respuesta no basta.

**Ramas locales que nunca se subieron.** El criterio se aplica sobre
`origin/<rama>`, así que cualquier rama que exista solo en disco queda fuera del
inventario. No aparece en `git branch -r`, no tiene upstream y ningún script que
recorra las referencias remotas la va a listar. La poda del APF3 se hizo sobre
las 28 ramas remotas, y `fix/autorizacion-por-roles` (21 de octubre, con un
commit de 855 líneas en 16 archivos) nunca formó parte de ese inventario porque
jamás se subió.

La comprobación complementaria es local:

```bash
git branch -vv                                          # lista todas, marca las que perdieron su remoto
git branch --no-merged origin/develop                   # las que NO están dentro de develop
git rev-list --count <rama> ^origin/develop ^origin/main
```

**Contenido duplicado por otro camino.** Una rama puede no ser ancestro de
`develop` y aun así no aportar nada, porque el mismo problema se resolvió de
otra forma. `fix/autorizacion-por-roles` traía un `SesionInterceptor` de 97
líneas, un `UsuarioAdvice` y una página `/sin-permiso`; `develop` resolvió el
mismo control de acceso con `SeguridadConfig` y `hasRole()` de Spring Security.
Antes de conservar una rama por su conteo de commits hay que leer el diff y
preguntar si lo que contiene sigue teniendo sentido con la arquitectura actual:

```bash
git diff origin/develop...<rama> --stat
git diff origin/develop...<rama> -- <archivo-concreto>
```

En este proyecto el conteo decía que la rama aportaba un commit propio, y el
diff mostró que ese commit implementaba un enfoque que el equipo ya había
descartado. Guardar una rama por si acaso sin leerla solo traslada la
decisión, no la resuelve.

Las dos comprobaciones se hacen antes de borrar y se dejan escritas en el
informe, porque son las que justifican la decisión cuando alguien pregunte
después por qué esa rama desapareció o por qué se conservó.

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
- **3 con trabajo vivo**, excluidas porque tenían un Pull Request sin fusionar
  en el momento de la poda: `docs/informe-apf2` (PR #23), `feature/bd-docker`
  (PR #34) e `infra/proteccion-ramas` (PR #36). Estas dos últimas además no
  estaban integradas: sus commits aún no existían en `develop`.
- **23 integradas**, todas candidatas a poda.

La comprobación fue: `2 + 3 + 23 = 28`, el total de partida.

Esas tres ramas no quedaron fuera del historial. Al fusionarse sus Pull
Requests se borraron por el mecanismo automático de la sección 6.1: los PR #34
y #36 el 17 de noviembre a las 16:40 y 16:41, y el PR #23 el 18 de noviembre a
las 05:45. Por eso el repositorio pasó de 28 a 3 ramas con **25 eliminaciones
en total**: 23 en la poda manual más las 2 que se fusionaron esa misma tarde, y
`docs/informe-apf2` al día siguiente con la fusión del #23.

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
| Ramas eliminadas | - | **25** |
| Reducción | - | **89,29 %** |
| PRs afectados | - | **0** |
| Commits perdidos | - | **0** |

Las 3 que quedaron fueron `main`, `develop` y `docs/informe-apf2`. Esta última
esperaba la fusión de su Pull Request, que llegó al día siguiente; al fusionarse
se borró sola y desde entonces el repositorio quedó con 2 ramas permanentes.

---

## 5. Evidencia

### 5.1 Las 25 ramas eliminadas y el PR que integró cada una

| # | Rama eliminada | Integrada por | Fecha | Cuándo se borró |
|---|---|---|---|---|
| 1 | `Rama-Secundaria` | *(ejercicio inicial de ramificación, sin PR)* | - | En la poda |
| 2 | `chore/ci` | PR #14 | 18-oct-2026 | En la poda |
| 3 | `docs/readme-develop` | PR #20 | 19-oct-2026 | En la poda |
| 4 | `feature/auth` | PR #1 | 02-sep-2026 | En la poda |
| 5 | `feature/auth-completo` | PR #7 | 18-sep-2026 | En la poda |
| 6 | `feature/consola-controller` | PR #16 | 18-oct-2026 | En la poda |
| 7 | `feature/disponibilidad-controller` | PR #18 | 19-oct-2026 | En la poda |
| 8 | `feature/docs-flujos` | PR #9 | 18-oct-2026 | En la poda |
| 9 | `feature/frontend-admin-operador` | PR #29 | 21-oct-2026 | En la poda |
| 10 | `feature/frontend-cliente` | PR #8 | 18-oct-2026 | En la poda |
| 11 | `feature/frontend-operador` | PR #6 | 18-oct-2026 | En la poda |
| 12 | `feature/maquina` | PR #17 | 18-oct-2026 | En la poda |
| 13 | `feature/panel-administrador` | PR #28 | 21-oct-2026 | En la poda |
| 14 | `feature/redesign-cliente` | PR #27 | 21-oct-2026 | En la poda |
| 15 | `feature/registro-presencial` | PR #12 | 18-oct-2026 | En la poda |
| 16 | `feature/reserva-controller` | PR #25 | 19-oct-2026 | En la poda |
| 17 | `feature/reserva-repository` | PR #19 | 19-oct-2026 | En la poda |
| 18 | `feature/reservas` | PR #2 | 14-sep-2026 | En la poda |
| 19 | `fix/arranque-datos` | PR #13 | 18-oct-2026 | En la poda |
| 20 | `fix/consola-reservas-reales` | PR #26 | 21-oct-2026 | En la poda |
| 21 | `fix/disponibilidad-render` | PR #15 | 18-oct-2026 | En la poda |
| 22 | `hotfix/docs-readme` | PR #24 | 19-oct-2026 | En la poda |
| 23 | `release/v0.2.0` | PR #30 | 21-oct-2026 | En la poda |
| 24 | `feature/bd-docker` | PR #34 | 17-nov-2026 | Al fusionar el #34 |
| 25 | `infra/proteccion-ramas` | PR #36 | 17-nov-2026 | Al fusionar el #36 |

**Las 23 primeras se borraron en la poda del 17 de noviembre.** Las dos últimas
no se tocaron ese día porque tenían un Pull Request sin fusionar; se borraron
solas cuando sus PR se fusionaron esa misma tarde, por el borrado automático de
la sección 6.1. Una vigésima sexta rama, `docs/informe-apf2`, esperó hasta el 18
de noviembre y corrió la misma suerte al fusionarse su PR #23.

**24 de las 25 ramas tienen su Pull Request documentado.** La única excepción es
`Rama-Secundaria`, del ejercicio inicial de ramificación del APF1: su contenido
ya estaba en `main` y nunca tuvo PR propio.

### 5.2 Las ramas que se conservaron y por qué

En el momento de la poda se dejaron cinco ramas remotas: las dos permanentes y
las tres que tenían un Pull Request sin fusionar.

| Rama | Motivo en ese momento | Estado actual |
|---|---|---|
| `main` | Rama permanente y estable | Existe |
| `develop` | Rama permanente de integración | Existe |
| `docs/informe-apf2` | PR #23 sin fusionar - se poda al fusionarse | Eliminada el 18-nov al fusionar el #23 |
| `feature/bd-docker` | PR #34 sin fusionar - se poda al fusionarse | Eliminada el 17-nov al fusionar el #34 |
| `infra/proteccion-ramas` | PR #36 sin fusionar - se poda al fusionarse | Eliminada el 17-nov al fusionar el #36 |

Las tres ramas de trabajo se podaron solas al fusionarse sus Pull Requests, tal
como estaba previsto. Los PR #34 y #36 cerraron el mismo 17 de noviembre, unas
horas después de la poda manual; el #23 cerró al día siguiente. Ninguna se borró
a mano: las eliminó el borrado automático de la sección 6.1 en el momento de la
fusión, que es exactamente el comportamiento que se buscaba.

### 5.3 Estado final

Inmediatamente después de la poda manual quedaron tres ramas remotas:

```
main
develop
docs/informe-apf2   <- en espera de la fusión de su Pull Request
```

Al fusionarse esa última rama, el repositorio quedó con las dos permanentes.
Desde entonces las ramas de trabajo nacen, se integran con un PR revisado y se
borran solas al fusionarse. Las ramas abiertas que se ven en el repositorio
pertenecen a los avances siguientes (APF3 y proyecto final), no a la poda.

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

En **Settings → Branches** se configuró la protección de `develop` con:

- `Require a pull request before merging`
- `Require status checks to pass` con el check **build** obligatorio

Con esto, ningún cambio entra a `develop` sin pasar las pruebas. La regla de
revisión por pares se sostiene en el acuerdo del equipo: cada PR se revisa antes
de fusionarlo, y el flujo obliga a que toda integración pase por un PR, de modo
que **toda rama pueda podarse después con trazabilidad completa**.

Al cierre de este documento la protección está aplicada sobre `develop`. `main`
todavía no la tiene, y la aprobación obligatoria no está activada: el check de
CI es el único requisito que bloquea una fusión. Conviene completar ambas cosas
antes de la entrega final, porque sin revisión obligatoria la salvaguarda 1 de
la sección 3.3 depende de la disciplina del equipo y no del repositorio.

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
| Ramas después de la poda | 3 |
| Ramas eliminadas | 25 |
| Reducción | 89,29 % |
| Pull Requests afectados | 0 |
| Commits perdidos | 0 |
| Ramas con PR documentado | 24 de 25 |
| Automatización | Borrado automático activado |

La poda convirtió un repositorio con 28 ramas, la mayoría ya integradas y sin
función, en uno con dos referencias vivas y significativas, sin perder un solo
commit y conservando la trazabilidad completa de cada integración en su Pull
Request.

Las cifras se verificaron contra la API de GitHub el 28 de noviembre de 2026.
