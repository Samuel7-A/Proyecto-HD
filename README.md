# LanReserve 🖥️
Plataforma web para la reserva de máquinas en centros de cómputo y lan centers.
**Curso:** Herramientas de Desarrollo (100000S61T) — Universidad Tecnológica del Perú (UTP)
**Ciclo:** 2026 – Ciclo 2 (Agosto) · **Carrera:** Ingeniería de Software / Sistemas e Informática
**Etapa actual:** Avance de Proyecto Final 1 (APF1 — Semana 6)
---
## 🎯 Problemática
Los dueños de centros de cómputo y lan centers de gran escala (100+ máquinas) tienen dificultad para vender todas las horas de sus máquinas porque la demanda se gestiona de forma exclusivamente presencial y solo se conoce cuando el cliente llega al local:
- En **horas punta**, pierden al cliente que llega, encuentra el local lleno y se va.
- En **horas valle**, las máquinas permanecen ociosas, consumiendo energía sin retorno.
En ambos casos el resultado es el mismo: **pérdida de ingresos**.
**Solución propuesta:** una plataforma web que permite al cliente consultar la disponibilidad y reservar su máquina con antelación (día y horario), y al negocio adelantar su demanda, organizar mejor su operación y analizar su ocupación.
---
## 🎯 Objetivos
**Objetivo general**
Desarrollar y desplegar una plataforma web que permita la reserva de máquinas en centros de cómputo y lan centers, gestionando la disponibilidad, los pagos y la ocupación del local.
**Objetivos específicos**
1. Implementar los perfiles de Cliente, Administrador y Operador, con sus flujos de reserva, validación de pago y liberación de máquinas.
2. Permitir la consulta de disponibilidad por fecha y horario, evitando la doble reserva mediante la detección de solapamientos.
3. Proveer al administrador un panel de ocupación para la toma de decisiones.
4. Desplegar la plataforma públicamente utilizando Docker y un pipeline de CI/CD.
---
## 👥 Roles
| Rol | Descripción |
|---|---|
| **Cliente** | Consulta disponibilidad, reserva, cancela y ve sus reservas |
| **Operador** | Valida pagos en el local, libera máquinas y marca no-shows |
| **Administrador** | Gestiona máquinas, tarifas, horarios, operadores y el panel de ocupación |
---
## 🧠 Reglas de negocio clave
1. **La ocupación es sagrada:** una reserva en estado `PAGADO` no puede ser pisada por nadie.
2. **Extender = nueva reserva:** la estadía se extiende creando una reserva para el bloque siguiente (si está libre), nunca alargando la actual.
3. **Reubicación:** si no se puede extender en la misma máquina, se busca otra libre; si no hay ninguna, el cliente termina su hora.
4. **Cancelación libre hasta 2 horas antes** del inicio; después, solo el operador.
5. **No-show:** tras 15 minutos de gracia sin asistencia, el operador marca la reserva como `VENCIDA` y la máquina queda libre.
6. **Disponibilidad derivada:** una máquina está libre si no está en mantenimiento y no tiene reservas activas (`PENDIENTE_PAGO` / `PAGADO`) que se solapen en ese horario.
**Ciclo de vida de una reserva:**
```
PENDIENTE_PAGO ──(operador valida pago)──▶ PAGADO ──(operador libera)──▶ COMPLETADA
      │                                        │
      ├─(cliente cancela ≤2h antes)──▶ CANCELADA
      └─(no llega / no-show)──────────▶ VENCIDA
```
**Delimitación del alcance (por decisión de diseño):**
- Pago simulado (validación en el local), sin integración con pasarelas de pago.
- Disponibilidad consultada por fecha y horario, sin estado de máquinas en tiempo real.
- Un solo local por instancia (sin multi-sede) y sin aplicación móvil.
---
## ⚙️ Funcionalidades
- Registro y autenticación por roles
- CRUD de máquinas y tarifas
- Consulta de disponibilidad por fecha y horario (bloques de 1 hora)
- Reserva de máquinas con ciclo de estado completo
- Validación de pago simulado y liberación por el operador
- Panel de ocupación categorizado (ocupación, demanda, ingresos, operativa)
---
## 🛠️ Stack tecnológico
| Capa | Tecnología |
|---|---|
| Control de versiones | Git + GitHub |
| Backend | Spring Boot 3 (Java 21) |
| Frontend | React (Vite) |
| Base de datos | MySQL 8 |
| Contenedores | Docker + Docker Compose |
| CI/CD | GitHub Actions |
| Despliegue | Render / Railway (AWS EC2 como meta) |
---
## 📁 Estructura del repositorio
```
Proyecto-LAN-CENTER/
├── backend/           # API REST — Spring Boot
├── frontend/          # SPA — React (Vite)
├── docs/              # Documentación del proyecto
├── docker-compose.yml
└── README.md
```
---
## 🚀 Cómo ejecutar
### Con Docker (recomendado)
```bash
docker compose up --build
```
- Frontend: http://localhost
- Backend: http://localhost:8080
### Desarrollo local (sin Docker)
**Backend** (requiere MySQL en `localhost:3306` con base de datos `lanreserve`):
```bash
cd backend
mvn spring-boot:run
```
**Frontend:**
```bash
cd frontend
npm install
npm run dev
```
---
## 🌿 Estrategia de ramificación (Git Flow)
- `main` — versión estable (protegida, solo se integra vía Pull Request).
- `develop` — rama de integración del equipo.
- `feature/<nombre>` — una rama por funcionalidad.
- `release/vX.Y.Z` — preparación de entrega por avance.
**Reglas de colaboración:**
- Cada funcionalidad se desarrolla en su rama `feature/` y se fusiona a `develop` vía Pull Request, con revisión de al menos un compañero.
- Los commits usan mensajes semánticos: `feat:`, `fix:`, `docs:`, `refactor:`.
- Los conflictos de merge se resuelven documentando la decisión en el PR.
- Cada avance se etiqueta con un tag/release (`v0.1.0` para el APF1).
---
## 🗺️ Roadmap (alineado al curso)
| Fase | Hito | Semana |
|---|---|---|
| Repo Git/GitHub, branching, PRs | **APF1** | 6 |
| Colaboración + issues + CI | **APF2** | 11 |
| CD + pipeline automatizado | **APF3** | 15 |
| Docker + cloud + demo final | **PROY** | 18 |
---
## 👤 Equipo
| Integrante | Código | GitHub | Módulo |
|---|---|---|---|
| Torres Ayala, Samuel Jeremy | U23259582 | [Samuel7-A](https://github.com/Samuel7-A) | BD / DevOps — modelo de datos, Docker, CI/CD, despliegue |
| Orellana Astovilca, Jose Fernando | U23268433 | [KennySor](https://github.com/KennySor) | Backend 2 — reservas, módulo Operador, lógica del panel |
| Evangelista Beldy Keny Andre | U23230437 | [Kaiilo1020](https://github.com/Kaiilo1020) | Backend 1 — autenticación, usuarios, módulo Administrador |
| Quiroz Leon, Sebastian Mathias | U23226705 | [limbar13](https://github.com/limbar13) | Frontend 1 — vistas de Cliente (búsqueda, reserva, historial) |
| Villacorta Latorre, Christian Aldair | U23241195 | [Christian-Aldair-Villacorta-Latorre](https://github.com/Christian-Aldair-Villacorta-Latorre) | Frontend 2 — vistas de Administrador y Operador |
| Mendez Morales, Benjamin | U22240797 | [benjamin46-hue](https://github.com/benjamin46-hue) | Documentación / QA — diagramas, casos de uso, pruebas |
> Asignación propuesta y rotativa: todos entienden el sistema completo y revisan los Pull Requests de los demás, aunque cada uno sea dueño de un módulo.
---
## 📌 Estado del proyecto
- [x] Definición de problemática, objetivos, roles y reglas de negocio
- [x] Modelo de datos (8 entidades)
- [ ] Backend Spring Boot (esqueleto inicial en desarrollo)
- [ ] Frontend React (esqueleto inicial en desarrollo)
- [ ] Despliegue con Docker y CI/CD
- [ ] APF1 — tag `v0.1.0`
