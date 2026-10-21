# Changelog

Todos los cambios relevantes de LanReserve. El formato sigue
[Keep a Changelog](https://keepachangelog.com/es-ES/1.1.0/) y el proyecto usa
versionado semantico ([SemVer](https://semver.org/lang/es/)).

## [0.2.0] - 2026-10-21

Avance de Proyecto Final 2 (APF2). Primera version funcional de punta a punta:
un cliente reserva desde la web y el operador ve esa reserva en el local y la
valida.

### Agregado

- **Autenticacion de usuarios** con contrasenas cifradas con BCrypt y roles
  (`CLIENTE`, `OPERADOR`, `ADMIN`). Registro web e inicio de sesion.
- **Modelo de datos** con las entidades `Usuario`, `Maquina` y `Reserva`, y las
  reservas de doble canal (`WEB` y `PRESENCIAL`).
- **Gestion de maquinas** con codigo, especificaciones y estado
  (`DISPONIBLE`, `OCUPADA`, `MANTENIMIENTO`), con seis maquinas de ejemplo.
- **Disponibilidad para el cliente**: vista de las maquinas con su estado.
- **Flujo de reserva del cliente**: `GET` y `POST /cliente/reservar`. La reserva
  nace en estado `PENDIENTE_PAGO` y canal `WEB`.
- **Historial de reservas del cliente**: `GET /cliente/historial`, con busqueda
  por celular.
- **Consola del operador**: lista las reservas reales con el nombre del cliente y
  el codigo de la maquina, valida el pago y marca el no-show cambiando el estado
  en la base de datos.
- **Registro presencial**: el operador da de alta clientes en el mostrador.
- **Panel de administracion**: `GET /admin/panel` con la ocupacion del local, el
  conteo de reservas por estado, los clientes registrados y el monto cobrado.
- **Pagina de inicio** con los accesos de cliente, operador, administrador y
  cuenta.
- **Tema dark gaming** en las vistas de cliente.
- **Integracion continua** con GitHub Actions: compila y ejecuta las pruebas en
  cada push y en cada Pull Request hacia `main` o `develop`.

### Cambiado

- El `README.md` describe la etapa APF2, el stack real del proyecto y los comandos
  de ejecucion con el wrapper de Maven incluido (`mvnw`).

### Corregido

- La aplicacion no arrancaba porque faltaba configurar el origen de datos de MySQL
  y el de pruebas. Se agrego H2 para las pruebas automatizadas.
- La vista de disponibilidad comparaba el estado de la maquina contra un valor que
  no correspondia al enum.
- La consola del operador mostraba la tabla siempre vacia y los botones
  `Validar Pago` y `Marcar No-Show` no cambiaban el estado de la reserva.

### Documentacion

- Informe del APF2 con el reparto de roles, la estrategia de Git aplicada y la
  resolucion de un conflicto de fusion real.
- Documentacion del flujo de los casos de uso CU05 a CU12.

### Pendiente para la siguiente version

- Validacion de solapamiento de horarios en las reservas (issue #21).
- Despliegue con Docker y CD (issue #22).
- Codigo de activacion de cuenta de un solo uso (issue #11).
- Ticket de reserva con codigo QR.
- Mapa visual del local para elegir la maquina.
