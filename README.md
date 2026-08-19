# Proyecto-LAN-CENTER
LanReserve 🖥️
Plataforma web para la reserva de máquinas en centros de cómputo y lan centers. Permite a los clientes consultar disponibilidad y reservar su máquina con antelación, evitando llegar al local y encontrarlo lleno.

🎯 Problemática
Los centros de cómputo de gran escala (100+ máquinas) pierden clientes por una mala experiencia: el usuario debe ir físicamente al local para saber si hay espacio, y a menudo llega y está lleno. Esto genera subutilización de equipos, pérdida de ingresos y un débil enganche con el público. Además, al mantener los equipos encendidos gran parte del día, el negocio necesita maximizar la ocupación para ser rentable.

Solución: una web donde el cliente ve y reserva su máquina (día + horario) de forma anticipada, y el negocio organiza mejor su operación y analiza su ocupación.

👥 Roles
Rol	Descripción
Cliente	Consulta disponibilidad, reserva, cancela y ve sus reservas
Operador	Valida pagos en el local, libera máquinas, marca no-shows
Administrador	Gestiona máquinas, tarifas, horarios, operadores y el panel de ocupación


⚙️ Funcionalidades principales
Registro y autenticación por roles
CRUD de máquinas y tarifas
Consulta de disponibilidad por fecha y horario (bloques de 1 hora)
Reserva de máquinas con ciclo de estado completo
Validación de pago y liberación por el operador
Panel de ocupación categorizado (ocupación, demanda, ingresos, operativa)


## 🧠 Reglas de negocio clave

1. **La ocupación es sagrada:** una reserva `PAGADO` no puede ser pisada por nadie.
2. **Extender = nueva reserva:** la estadía se extiende creando una reserva para el
   bloque siguiente (si está libre), nunca alargando la actual.
3. **Reubicación:** si no se puede extender en la misma máquina, se busca otra libre;
   si no hay ninguna, el cliente termina su hora.
4. **Cancelación libre hasta 2 horas antes** del inicio; después, solo el operador.
5. **No-show:** tras 15 min de gracia sin asistencia, el operador marca la reserva
   como `VENCIDA` y la máquina queda libre.
6. **Disponibilidad derivada:** una máquina está libre si no está en mantenimiento
   y no tiene reservas activas (PENDIENTE_PAGO / PAGADO) que se solapen en ese horario.

## 🛠️ Stack tecnológico

| Capa | Tecnología |
|---|---|
| Frontend | React (Vite) |
| Backend | Spring Boot (Java) |
| Base de datos | MySQL |
| Contenedores | Docker + Docker Compose |
| CI/CD | GitHub Actions |
| Control de versiones | Git + GitHub |
| Despliegue | Render / Railway (AWS EC2 como meta) |

## 🗺️ Roadmap (alineado al curso)

| Fase | Hito | Semana |
|---|---|---|
| Repo Git/GitHub, branching, PRs | **APF1** | 6 |
| Colaboración + issues + CI | **APF2** | 11 |
| CD + pipeline automatizado | **APF3** | 15 |
| Docker + cloud + demo final | **PROY** | 18 |


## 🚀 Cómo correrlo (próximamente)
