# Cómo contribuir a LanReserve

Este proyecto es una plataforma web para reservar máquinas en centros de cómputo.
Cada integrante contribuye con commits en su rama `feature/` y Pull Requests hacia `develop`.

## Modelo de trabajo

1. Cada integrante tiene asignado un módulo (ver README, sección "Equipo").
2. Todo desarrollo se hace en su rama `feature/<nombre>`.
3. Se integra con un **Pull Request** hacia `develop`.
4. Al menos **un compañero** debe revisar y aprobar el PR antes del merge.
5. Los **commits** usan mensajes semánticos:

   - `feat:` — nuevas funcionalidades
   - `fix:` — correcciones de bugs
   - `docs:` — documentación
   - `refactor:` — cambios que no alteran el comportamiento

## Ramas

- `main` — rama estable; solo se integra vía Pull Request.
- `develop` — integración del equipo.
- `feature/<nombre>` — una rama por funcionalidad.
- `release/vX.Y.Z` — preparación de entrega.

## Resolución de conflictos

1. Al hacer merge a `develop`, si hay conflictos, se resuelven **en la rama de integración**.
2. Documenta la decisión en el comentario del PR.

## Normas de código

- Mantén el nombre de clases y métodos en inglés.
- Usa nombres descriptivos (sin abreviaturas).
- Añade comentarios solo donde el código no sea auto-explicativo.
- Antes de abrir el PR, compila localmente: `mvn clean compile`.
