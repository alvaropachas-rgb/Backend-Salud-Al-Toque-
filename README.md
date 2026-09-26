# 🩺 Salud al Toque

## Proyecto Final — CS 2031 Desarrollo Basado en Plataforma

**Curso:** CS 2031 Desarrollo Basado en Plataforma  
**Proyecto:** Salud al Toque  

**Backend:** Java + Spring Boot  
**Base de datos:** PostgreSQL  
**Integrantes:** 
- Alvaro David Pachas Chapeton
- Fabricio Alberto Olaguibel Romero
- Saul Morales Zumaeta
- Alexander Muñoz Zamora
- Jhon Dayron Blas Huete  
---

## Índice

1. [Introducción](#introducción)
2. [Identificación del Problema](#identificación-del-problema)
3. [Descripción de la Solución](#descripción-de-la-solución)
4. [Arquitectura del Proyecto](#arquitectura-del-proyecto)
5. [Modelo de Entidades](#modelo-de-entidades)
6. [DTOs y Mapeo](#dtos-y-mapeo)
7. [API REST y Endpoints](#api-rest-y-endpoints)
8. [Manejo de Errores](#manejo-de-errores)
9. [Seguridad y Autenticación](#seguridad-y-autenticación)
10. [Base de Datos y Ejecución](#base-de-datos-y-ejecución)
11. [Pruebas](#pruebas)
12. [GitHub y Gestión del Proyecto](#github-y-gestión-del-proyecto)
13. [Conclusiones y Trabajo Futuro](#conclusiones-y-trabajo-futuro)
14. [Apéndices](#apéndices)

---

# 1. Introducción

## Contexto

Salud al Toque es una plataforma orientada a facilitar la búsqueda y gestión de servicios médicos. El sistema busca conectar pacientes con profesionales de la salud, permitiendo consultar información profesional, especialidades, servicios médicos, disponibilidad y gestionar citas.

El backend fue desarrollado como una API REST utilizando Java y Spring Boot. La aplicación administra usuarios con diferentes roles, profesionales, pacientes, servicios médicos, especialidades, citas, disponibilidades, reseñas y favoritos.

## Objetivos del Proyecto

Los principales objetivos son:

- Permitir el registro y autenticación de usuarios.
- Gestionar pacientes y profesionales de la salud.
- Permitir consultar profesionales por especialidad, ubicación y precio.
- Gestionar los servicios ofrecidos por cada profesional.
- Registrar la disponibilidad de los profesionales.
- Permitir la creación y gestión de citas médicas.
- Permitir que los pacientes registren reseñas.
- Permitir guardar profesionales como favoritos.
- Proteger los recursos mediante autenticación y autorización.
- Mantener una arquitectura organizada y escalable.

### Resumen técnico

| Componente | Tecnología / implementación |
|---|---|
| Lenguaje | Java |
| Framework | Spring Boot |
| API | REST |
| Persistencia | JPA / Hibernate |
| Base de datos | PostgreSQL |
| Seguridad | Spring Security + JWT |
| Testing | JUnit 5 + Mockito + MockMvc + Testcontainers |
| Notificaciones | Spring Mail + Thymeleaf + Mailpit |
| Procesamiento asíncrono | `@Async` + `ThreadPoolTaskExecutor` |


---

# 2. Identificación del Problema

## Descripción del Problema

Encontrar profesionales de la salud y coordinar una atención puede requerir consultar diferentes fuentes de información. Además, el paciente necesita conocer datos como especialidad, ubicación, servicios disponibles, precios, horarios y valoración del profesional antes de solicitar una cita.

Desde el punto de vista del profesional, también es necesario administrar sus servicios, disponibilidad y citas.

## Justificación

Salud al Toque centraliza estas operaciones en una sola plataforma. De esta manera, el paciente puede consultar profesionales y servicios y posteriormente gestionar sus citas desde el mismo sistema.

La separación entre pacientes, profesionales, servicios, disponibilidad y citas permite representar de manera estructurada las relaciones existentes en el dominio.

---

# 3. Descripción de la Solución

## Funcionalidades Implementadas

### Autenticación

El sistema permite:

- Registro de pacientes.
- Inicio de sesión.
- Generación de tokens JWT.
- Validación de tokens.
- Autorización según roles.

### Gestión de profesionales

Los usuarios pueden consultar profesionales y aplicar filtros por:

- Especialidad.
- Ubicación.
- Precio máximo.

Cada profesional está relacionado con un usuario, una especialidad y sus servicios médicos.

Los administradores pueden crear, actualizar y eliminar profesionales, validando que el usuario asociado exista y tenga rol `PROFESSIONAL`.

### Servicios médicos

Cada profesional puede administrar los servicios médicos que ofrece. Los servicios contienen:

- Nombre.
- Descripción.
- Precio.
- Profesional asociado.

El precio de un servicio se almacena también en la cita cuando esta se genera, funcionando como un registro histórico del precio utilizado en dicha reserva.

### Disponibilidad

Los profesionales pueden registrar sus horarios de atención indicando:

- Día de la semana.
- Hora de inicio.
- Hora de finalización.

### Citas

Las citas relacionan:

- Paciente.
- Profesional.
- Servicio médico.
- Fecha.
- Hora.
- Estado.
- Notas.
- Precio.

Además, se implementa una validación para evitar la duplicación de una cita para el mismo profesional, fecha y hora.

### Reseñas

Los pacientes pueden registrar reseñas asociadas a una cita completada. Las reseñas contienen una calificación y un comentario y se relacionan con el paciente, profesional y cita correspondiente.

### Favoritos

Los pacientes pueden guardar profesionales como favoritos. Se evita que un mismo paciente registre dos veces al mismo profesional como favorito mediante una restricción de unicidad.

### Eventos y procesamiento asíncrono

El backend incorpora eventos de dominio y procesamiento asíncrono para las notificaciones:

- `UserRegisteredEvent`: se publica al registrar un usuario.
- `AppointmentCreatedEvent`: se publica al crear una cita.
- `ReviewCreatedEvent`: se publica al registrar una reseña.

El procesamiento asíncrono se habilita mediante `@EnableAsync` y un `ThreadPoolTaskExecutor` dedicado a notificaciones. Los hilos del executor utilizan el prefijo `notification-`.

El flujo de negocio probado es:

```text
Paciente
  │
  ├── Sign Up
  │     └── UserRegisteredEvent → @Async → correo
  │
  ├── Sign In
  │
  └── Crear Appointment
        └── AppointmentCreatedEvent → @Async → correo
                 │
                 ▼
Profesional
  │
  ├── Sign In
  ├── Aceptar Appointment
  └── Completar Appointment
                 │
                 ▼
Paciente
  │
  └── Crear Review
        └── ReviewCreatedEvent → @Async → correo
```

La reseña se registra después de que el profesional haya completado la cita.

---

# 4. Arquitectura del Proyecto

El backend está organizado como un **monolito modular**, con separación de responsabilidades por dominio. Cada funcionalidad principal mantiene sus propios componentes de aplicación, dominio, DTO e infraestructura cuando corresponde, mientras que las preocupaciones transversales se concentran en `config`, `auth`, `event`, `notification` y el manejo global de excepciones.

## 4.1 Estructura general

```text
src/main/java/com/example/sss001/
│
├── config/
│   ├── SecurityConfig.java
│   └── AsyncConfig.java
│
├── auth/
│   ├── application/
│   ├── components/
│   ├── domain/
│   └── dto/
│
├── user/
│   ├── application/       → Controllers y entrada HTTP
│   ├── domain/            → Entidades y lógica de negocio
│   ├── dto/               → Objetos de entrada/salida de la API
│   └── infrastructure/   → Repositories y persistencia
│
├── patient/
├── professional/
├── specialty/
├── medicalservice/
├── availability/
├── appointment/
├── review/
├── favorite/
├── event/
│   ├── Events de dominio
│   └── Listeners asíncronos
│
├── notification/
│   ├── EmailService
│   └── EmailServiceImpl
│
├── exceptions/
│   ├── ConflictException
│   ├── ForbiddenException
│   └── ResourceNotFoundException
│
└── GlobalExceptionHandler.java
```

La estructura real del código sigue este esquema en los módulos principales. `auth` añade `components/` para los elementos específicos de JWT, mientras que `event` y `notification` funcionan como módulos transversales.

## 4.2 Capas y responsabilidades

| Capa | Responsabilidad | Ejemplos |
|---|---|---|
| `application` | Recibir solicitudes HTTP y coordinar la operación | `AppointmentController`, `ReviewController` |
| `domain` | Representar entidades y ejecutar operaciones de negocio | `Appointment`, `AppointmentService`, `Review` |
| `dto` | Definir los datos expuestos por la API y las solicitudes recibidas | `AppointmentDTO`, `CreateAppointmentRequest` |
| `infrastructure` | Acceso y persistencia de datos mediante Spring Data JPA | `AppointmentRepository`, `ReviewRepository` |
| `auth/components` | Generación y validación de JWT | `JwtService`, `JwtAuthorizationFilter` |
| `config` | Configuración transversal de seguridad y procesamiento asíncrono | `SecurityConfig`, `AsyncConfig` |
| `event` | Publicación y procesamiento de eventos de dominio | `AppointmentCreatedEvent`, listeners |
| `notification` | Construcción y envío de correos mediante plantillas | `EmailServiceImpl` |

El flujo habitual de una solicitud REST es:

```text
Cliente
   │
   ▼
Controller
   │
   ▼
Service / Domain
   │
   ├──────────────► Event Publisher
   │                     │
   │                     ▼
   │              @Async Listener
   │                     │
   │                     ▼
   │              Notification / Email
   │
   ▼
Repository
   │
   ▼
PostgreSQL
```

La inyección de dependencias se realiza mediante constructores, lo que reduce el acoplamiento entre componentes y facilita las pruebas unitarias con Mockito.

## 4.3 Persistencia

Las entidades del dominio se persisten mediante **JPA/Hibernate** y repositories de Spring Data. La aplicación utiliza PostgreSQL como base de datos y carga datos iniciales mediante `data.sql`.

La configuración actual utiliza `spring.jpa.hibernate.ddl-auto=create-drop`, por lo que el esquema se genera al iniciar la aplicación y se elimina al finalizar. Esta configuración es adecuada para el entorno de desarrollo/pruebas documentado, pero debería revisarse para un entorno productivo.

## 4.4 Seguridad transversal

`SecurityConfig` configura Spring Security con sesiones `STATELESS`, deshabilita CSRF para la API y registra `JwtAuthorizationFilter` antes de `UsernamePasswordAuthenticationFilter`.

El acceso se divide entre endpoints públicos y endpoints autenticados. La autorización fina por rol se complementa mediante seguridad a nivel de método con `@EnableMethodSecurity` y las restricciones `@PreAuthorize` utilizadas por los controllers.

```text
Request
   │
   ▼
JwtAuthorizationFilter
   │
   ├── Token válido ──► Usuario autenticado
   │                         │
   │                         ▼
   │                    @PreAuthorize
   │                         │
   │                    ┌────┴────┐
   │                    ▼         ▼
   │                  Permit    Deny
   │                    │         │
   │                    ▼         ▼
   │                  2xx       403
   │
   └── Sin autenticación ───────► 401
```

Las contraseñas se protegen mediante `BCryptPasswordEncoder`.

## 4.5 Eventos y procesamiento asíncrono

La arquitectura desacopla las notificaciones del flujo HTTP mediante eventos de dominio y `@Async`.

```text
Operación de negocio
       │
       ▼
Publicación del evento
       │
       ├── UserRegisteredEvent
       ├── AppointmentCreatedEvent
       └── ReviewCreatedEvent
                    │
                    ▼
          notificationExecutor
                    │
                    ▼
              EmailService
                    │
                    ▼
          Thymeleaf + SMTP
                    │
                    ▼
                Mailpit
```

`AsyncConfig` define el executor `notificationExecutor` con:

- `corePoolSize = 2`
- `maxPoolSize = 5`
- `queueCapacity = 100`
- prefijo de hilos `notification-`
- espera de tareas pendientes durante el apagado

Los listeners registran en los logs el inicio y fin del procesamiento, incluyendo el nombre del hilo. Esto permite demostrar que las notificaciones se ejecutan fuera del hilo HTTP principal.

## 4.6 Manejo transversal de errores

`GlobalExceptionHandler`, basado en `@ControllerAdvice`, centraliza el tratamiento de excepciones y mantiene respuestas HTTP consistentes.

Las excepciones de dominio principales son:

- `ResourceNotFoundException`
- `ForbiddenException`
- `ConflictException`

Esta estrategia evita repetir lógica de manejo de errores en cada controller y permite distinguir, entre otros casos, recursos inexistentes (`404`), conflictos de negocio (`409`) y problemas de autorización (`403`).

## 4.7 Ventajas de la arquitectura

La organización actual aporta:

- **Separación de responsabilidades:** cada componente tiene una función concreta.
- **Modularidad:** las funcionalidades del dominio están agrupadas por contexto.
- **Mantenibilidad:** cambios en una funcionalidad pueden concentrarse en su módulo.
- **Testabilidad:** services y repositories pueden probarse de forma aislada y los controllers mediante `MockMvc`.
- **Escalabilidad:** los eventos permiten incorporar nuevas reacciones a una operación sin acoplarlas directamente al controller.
- **Seguridad centralizada:** JWT y reglas generales se configuran desde Spring Security.

Para la presentación final, la idea central de la arquitectura puede resumirse como:

> **API REST modular + JPA/PostgreSQL + JWT/Spring Security + eventos asíncronos para notificaciones.**
# 5. Modelo de Entidades

El sistema actualmente cuenta con las siguientes entidades principales:

| Entidad | Descripción |
|---|---|
| `User` | Información y credenciales del usuario |
| `Patient` | Información adicional del paciente |
| `Professional` | Información del profesional |
| `Specialty` | Especialidad médica |
| `MedicalService` | Servicio ofrecido por un profesional |
| `Appointment` | Cita médica |
| `Availability` | Horario disponible |
| `Review` | Reseña de un profesional |
| `Favorite` | Profesional guardado por un paciente |

## Relaciones principales

```mermaid
erDiagram
    USER ||--o| PATIENT : "puede ser"
    USER ||--o| PROFESSIONAL : "puede ser"

    SPECIALTY ||--o{ PROFESSIONAL : tiene

    PROFESSIONAL ||--o{ MEDICAL_SERVICE : ofrece
    PROFESSIONAL ||--o{ AVAILABILITY : posee
    PROFESSIONAL ||--o{ APPOINTMENT : atiende
    PROFESSIONAL ||--o{ REVIEW : recibe
    PROFESSIONAL ||--o{ FAVORITE : "es guardado"

    PATIENT ||--o{ APPOINTMENT : reserva
    PATIENT ||--o{ REVIEW : escribe
    PATIENT ||--o{ FAVORITE : guarda

    MEDICAL_SERVICE ||--o{ APPOINTMENT : corresponde

    APPOINTMENT ||--o| REVIEW : genera
```

Las relaciones se implementan mediante anotaciones JPA como `@OneToOne`, `@OneToMany` y `@ManyToOne`.

La entidad `Favorite` incorpora además una restricción de unicidad sobre la combinación `patient_id` y `professional_id`.

---

# 6. DTOs y Mapeo

El proyecto utiliza DTOs para evitar exponer directamente las entidades JPA en los endpoints.

Entre los DTO implementados se encuentran:

- `UserDTO`
- `ProfessionalDTO`
- `PatientDTO`
- `AppointmentDTO`
- `CreateAppointmentRequest`
- `SpecialtyDTO`
- `MedicalServiceDTO`
- `AvailabilityDTO`
- `ReviewDTO`
- `CreateReviewRequest`
- `FavoriteDTO`
- `SignInRequest`
- `SignUpRequest`
- `TokenResponse`

Esta separación permite definir estructuras específicas para las solicitudes y respuestas de la API.

Por ejemplo, las contraseñas de los usuarios no forman parte de `UserDTO`, evitando exponer información sensible.

Para algunas operaciones se utiliza ModelMapper, mientras que determinadas conversiones se realizan directamente en los servicios.

---

# 7. API REST y Endpoints

La API utiliza recursos en plural y verbos HTTP de acuerdo con las operaciones realizadas.

La colección de Postman del repositorio (`Salud_al_Toque_Postman_Collection.json`) cubre todos los endpoints listados a continuación. Se recomienda ejecutar en orden los requests "Sign In - Admin", "Sign In - Professional" y "Sign In" (paciente) de la carpeta `01 - Auth` para poblar las variables `{{adminToken}}`, `{{professionalToken}}` y `{{token}}` usadas por el resto de la colección.

## Autenticación

| Método | Endpoint | Descripción | Rol |
|---|---|---|---|
| `POST` | `/auth/signup` | Registrar paciente | Público |
| `POST` | `/auth/signin` | Iniciar sesión | Público |

## Usuarios

| Método | Endpoint | Descripción | Rol |
|---|---|---|---|
| `GET` | `/users` | Listar usuarios | `ADMIN` |
| `GET` | `/users/{id}` | Obtener usuario | `ADMIN` |
| `POST` | `/users` | Crear usuario | `ADMIN` |
| `DELETE` | `/users/{id}` | Eliminar usuario | `ADMIN` |

## Profesionales

| Método | Endpoint | Descripción | Rol |
|---|---|---|---|
| `GET` | `/professionals` | Listar y buscar profesionales | Público |
| `GET` | `/professionals/{id}` | Obtener profesional | Público |
| `POST` | `/professionals` | Crear profesional (valida rol `PROFESSIONAL` del usuario y especialidad) | `ADMIN` |
| `PUT` | `/professionals/{id}` | Actualizar ubicación, rating y especialidad | `ADMIN` |
| `DELETE` | `/professionals/{id}` | Eliminar profesional | `ADMIN` |

Los filtros disponibles incluyen:

```text
/professionals?specialty=Cardiologia
/professionals?location=Lima
/professionals?maxPrice=80
```

También pueden combinarse.

## Pacientes

| Método | Endpoint | Descripción | Rol |
|---|---|---|---|
| `GET` | `/patients` | Consultar pacientes | `ADMIN` |
| `GET` | `/patients/{id}` | Obtener paciente | `ADMIN` |
| `POST` | `/patients` | Crear paciente | `ADMIN` |
| `DELETE` | `/patients/{id}` | Eliminar paciente | `ADMIN` |
| `GET` | `/patients/me` | Obtener información del paciente autenticado | `PATIENT` |

## Especialidades

| Método | Endpoint | Descripción | Rol |
|---|---|---|---|
| `GET` | `/specialties` | Listar especialidades | Público |
| `GET` | `/specialties/{id}` | Obtener especialidad | Público |
| `POST` | `/specialties` | Crear especialidad | `ADMIN` |
| `DELETE` | `/specialties/{id}` | Eliminar especialidad | `ADMIN` |

## Servicios médicos

| Método | Endpoint | Descripción | Rol |
|---|---|---|---|
| `GET` | `/medical-services` | Listar servicios | Público |
| `GET` | `/medical-services/{id}` | Obtener servicio | Público |
| `GET` | `/medical-services/professional/{professionalId}` | Listar servicios de un profesional | Público |
| `POST` | `/medical-services` | Crear servicio (el profesional se deriva del JWT) | `PROFESSIONAL` |
| `DELETE` | `/medical-services/{id}` | Eliminar servicio (solo el dueño) | `PROFESSIONAL` |

## Disponibilidad

| Método | Endpoint | Descripción | Rol |
|---|---|---|---|
| `GET` | `/availabilities` | Listar disponibilidades | Público |
| `GET` | `/availabilities/{id}` | Obtener disponibilidad | Público |
| `GET` | `/availabilities/professional/{professionalId}` | Listar horarios de un profesional | Público |
| `POST` | `/availabilities` | Crear disponibilidad (el profesional se deriva del JWT) | `PROFESSIONAL` |
| `DELETE` | `/availabilities/{id}` | Eliminar disponibilidad (solo el dueño) | `PROFESSIONAL` |

## Citas

| Método | Endpoint | Descripción | Rol |
|---|---|---|---|
| `GET` | `/appointments` | Listar todas las citas | `ADMIN` |
| `GET` | `/appointments/{id}` | Obtener cita | `ADMIN` |
| `GET` | `/appointments/patient/{patientId}` | Listar citas de un paciente | `ADMIN` |
| `GET` | `/appointments/professional/{professionalId}` | Listar citas de un profesional | `ADMIN` |
| `GET` | `/appointments/my-appointments` | Listar citas del paciente autenticado | `PATIENT` |
| `GET` | `/appointments/my-professional-appointments` | Listar citas del profesional autenticado | `PROFESSIONAL` |
| `POST` | `/appointments` | Crear cita (valida disponibilidad y duplicados) | `PATIENT` |
| `PATCH` | `/appointments/{id}/accept` | Aceptar una cita (`PENDIENTE` → `ACEPTADA`) | `PROFESSIONAL` |
| `PATCH` | `/appointments/{id}/reject` | Rechazar una cita (`PENDIENTE` → `RECHAZADA`) | `PROFESSIONAL` |
| `PATCH` | `/appointments/{id}/complete` | Completar una cita (`ACEPTADA` → `COMPLETADA`) | `PROFESSIONAL` |
| `DELETE` | `/appointments/{id}` | Eliminar cita | `ADMIN` |

Las operaciones de aceptación, rechazo y completado requieren el JWT del profesional:

```text
Authorization: Bearer <professionalToken>
```

El flujo es:

```text
POST /appointments
        ↓
PATCH /appointments/{id}/accept
        ↓
PATCH /appointments/{id}/complete
        ↓
POST /reviews
```

El acceso a las citas se encuentra protegido para evitar que un paciente consulte información perteneciente a otros usuarios.

## Reseñas

| Método | Endpoint | Descripción | Rol |
|---|---|---|---|
| `GET` | `/reviews` | Listar reseñas | Público |
| `GET` | `/reviews/{id}` | Obtener reseña | Público |
| `GET` | `/reviews/professional/{professionalId}` | Listar reseñas de un profesional | Público |
| `POST` | `/reviews` | Crear reseña (solo sobre cita `COMPLETADA` del propio paciente, sin reseña previa; recalcula el rating del profesional) | `PATIENT` |

## Favoritos

| Método | Endpoint | Descripción | Rol |
|---|---|---|---|
| `GET` | `/favorites/my-favorites` | Obtener favoritos del paciente autenticado | `PATIENT` |
| `POST` | `/favorites/professional/{professionalId}` | Agregar favorito (409 si ya existe) | `PATIENT` |
| `DELETE` | `/favorites/{id}` | Eliminar favorito (solo el propio) | `PATIENT` |

---

# 8. Manejo de Errores

El proyecto cuenta con un `GlobalExceptionHandler` basado en `@ControllerAdvice`.

Se implementaron excepciones personalizadas como:

- `ResourceNotFoundException`
- `ForbiddenException`
- `ConflictException`

Además, el sistema maneja excepciones de Spring Security y errores relacionados con solicitudes HTTP.

Los principales códigos utilizados son:

| Código | Significado |
|---|---|
| `200` | Operación exitosa |
| `400` | Solicitud inválida |
| `401` | Usuario no autenticado |
| `403` | Usuario autenticado sin permisos |
| `404` | Recurso inexistente |
| `409` | Conflicto |
| `500` | Error interno |

Un ejemplo importante es la diferencia entre `401` y `403`: un usuario sin token no está autenticado, mientras que un usuario autenticado puede ser rechazado cuando intenta ejecutar una operación que su rol no permite.

---

# 9. Seguridad y Autenticación

La aplicación utiliza Spring Security junto con JWT.

El proceso de autenticación es:

```text
Usuario
   ↓
POST /auth/signin
   ↓
AuthenticationManager
   ↓
Validación de email/password
   ↓
JwtService
   ↓
Token JWT
```

Posteriormente, el cliente envía:

```text
Authorization: Bearer <token>
```

El `JwtAuthorizationFilter` intercepta la solicitud, valida el token y obtiene el usuario correspondiente mediante `CustomUserDetailsService`.

Las contraseñas son almacenadas utilizando `BCryptPasswordEncoder`.

Actualmente existen roles como:

```text
PATIENT
PROFESSIONAL
ADMIN
```

La autorización se utiliza para restringir operaciones sensibles. Por ejemplo, un paciente no puede crear disponibilidades o servicios médicos de un profesional. La colección de Postman mantiene tres tokens separados para verificar la autorización por rol en cada endpoint:

```text
token             → JWT del paciente (PATIENT)
professionalToken → JWT del profesional (PROFESSIONAL)
adminToken        → JWT del administrador (ADMIN)
```

La configuración utiliza sesiones `STATELESS`, por lo que la autenticación se basa en los tokens.

La clave JWT está configurada mediante una propiedad de aplicación y está preparada para utilizar variables de entorno en la configuración.

---

# 10. Base de Datos y Ejecución

El proyecto utiliza PostgreSQL y Docker Compose para facilitar el desarrollo local.

El contenedor utilizado es:

```text
postgres:16-alpine
```

La aplicación se conecta mediante:

```properties
spring.datasource.url=jdbc:postgresql://${DB_HOST:localhost}:${DB_PORT:5434}/${DB_NAME:sss001}
```

Para iniciar la base de datos:

```bash
docker compose up -d
```

Para ejecutar el backend:

```bash
./mvnw spring-boot:run
```

También puede ejecutarse directamente desde IntelliJ IDEA.

La aplicación utiliza `data.sql` para cargar datos iniciales de prueba, incluyendo usuarios, profesionales, especialidades, servicios, disponibilidad y una cita inicial.

### Mailpit para desarrollo

Mailpit permite capturar los correos enviados por el backend sin enviarlos a destinatarios reales.

El backend envía los mensajes al SMTP local de Mailpit mediante `localhost:1025`. Esto permite verificar las notificaciones generadas por `UserRegisteredEvent`, `AppointmentCreatedEvent` y `ReviewCreatedEvent`.


---

# 11. Eventos y Procesamiento Asíncrono

El backend utiliza Spring para ejecutar los listeners de eventos en segundo plano.

### Eventos implementados

| Evento | Se dispara cuando | Procesamiento |
|---|---|---|
| `UserRegisteredEvent` | Se registra un usuario | `@Async` |
| `AppointmentCreatedEvent` | Se crea una cita | `@Async` |
| `ReviewCreatedEvent` | Se crea una reseña | `@Async` |

La configuración utiliza un `ThreadPoolTaskExecutor` llamado `notificationExecutor`, con hilos identificables mediante `notification-*`.

En los logs se puede comprobar la separación entre la publicación y el listener:

```text
EVENT PUBLISH START ... thread=http-nio-...
EVENT PUBLISH END   ... thread=http-nio-...
ASYNC EVENT START   ... thread=notification-...
ASYNC EVENT END     ... thread=notification-...
```

Esto permite verificar que el procesamiento del evento se ejecuta fuera del hilo HTTP que atendió la solicitud.

---

# 12. Correo de Desarrollo con Mailpit

Mailpit se utiliza como servidor SMTP local durante las pruebas de integración.

### Levantar Mailpit

```bash
docker run -d \
  --name mailpit \
  -p 1025:1025 \
  -p 8025:8025 \
  axllent/mailpit
```

### Acceso

- SMTP: `localhost:1025`
- Interfaz web: `http://localhost:8025`

Después de iniciar Mailpit y reiniciar el backend, los correos generados por los listeners asíncronos pueden revisarse desde la interfaz web.

### Flujo completo de Postman

La carpeta `11 - Events & Async Integration Test` de la colección de Postman contiene el siguiente proceso:

```text
1. Backend reachable
2. Paciente - Sign Up
3. Paciente - Sign In
4. Crear Appointment
5. Verificar Appointment (GET /appointments/my-appointments)
6. Profesional - Sign In
7. Profesional - Accept Appointment
8. Profesional - Complete Appointment
9. Paciente - Create Review
10. Verificar Review
11. Async Evidence Checklist
```

Los JWT se mantienen separados:

```text
token             → JWT del paciente
professionalToken → JWT del profesional
adminToken        → JWT del administrador
```

De esta manera, la colección comprueba tanto la autorización por rol como el flujo de eventos.

---

# 13. Pruebas

Durante el desarrollo se implementaron pruebas de diferentes niveles utilizando JUnit 5, Mockito, Spring Boot Test, MockMvc y Testcontainers.

Se busca que cada módulo del dominio cuente con las tres capas de pruebas cuando aplica:

- **Repository test** (`@DataJpaTest` + Testcontainers): valida las consultas derivadas y personalizadas contra una base de datos PostgreSQL real.
- **Service test** (Mockito, `@ExtendWith(MockitoExtension.class)`): valida la lógica de negocio de la capa de servicio de forma aislada, mockeando el repositorio.
- **Controller test** (`@SpringBootTest` + `MockMvc` + Testcontainers): valida los endpoints REST de punta a punta, incluyendo autenticación JWT, autorización por rol (`@PreAuthorize`) y los códigos de error (`401`, `403`, `404`, `409`).

## Pruebas por módulo

| Módulo | Repository | Service | Controller |
|---|---|---|---|
| `auth` | — | `AuthServiceTest` | — |
| `user` | `UserRepositoryTest` | `UserServiceTest` | `UserControllerTest` |
| `patient` | `PatientRepositoryTest` | `PatientServiceTest` | `PatientControllerTest` |
| `professional` | `ProfessionalRepositoryTest` | `ProfessionalServiceTest` | `ProfessionalControllerTest` |
| `specialty` | `SpecialtyRepositoryTest` | `SpecialtyServiceTest` | `SpecialtyControllerTest` |
| `medicalservice` | `MedicalServiceRepositoryTest` | `MedicalServiceManagerTest` | `MedicalServiceControllerTest` |
| `availability` | `AvailabilityRepositoryTest` | `AvailabilityServiceTest` | `AvailabilityControllerTest` |
| `appointment` | `AppointmentRepositoryTest` | `AppointmentServiceTest` | `AppointmentControllerTest` |
| `review` | `ReviewRepositoryTest` | `ReviewServiceTest` | `ReviewControllerTest` |
| `favorite` | `FavoriteRepositoryTest` | `FavoriteServiceTest` | `FavoriteControllerTest` |

Adicional: `Sss001ApplicationTests` verifica que el contexto de Spring Boot cargue correctamente.

En total, el proyecto cuenta con **30 clases de test**.

## Qué valida cada capa

- **Service tests:** casos de éxito, casos en los que el repositorio no encuentra el recurso (`Optional.empty()` / `null`), y que los métodos del servicio delegan correctamente en el repositorio (`verify(repository)...`). El caso de `ProfessionalServiceTest` cubre además las 8 combinaciones de filtros del método `search()` (especialidad, ubicación, precio máximo y sus combinaciones).
- **Repository tests:** consultas derivadas (`findByX`, `existsByX`) contra los datos sembrados en `data.sql`, así como el guardado y recuperación de nuevas entidades.
- **Controller tests:** flujo HTTP completo con JWT real generado por `JwtService`, cubriendo:
  - `401 Unauthorized` cuando no se envía token.
  - `403 Forbidden` cuando el rol autenticado no tiene permiso (`@PreAuthorize`).
  - `404 Not Found` cuando el recurso no existe.
  - `409 Conflict` / reglas de negocio (p. ej. citas duplicadas, reseñas repetidas, rating fuera de rango).
  - Casos de éxito (`200 OK`, `201 Created`).

Las pruebas utilizan PostgreSQL mediante Testcontainers para acercar el entorno de testing al entorno real de ejecución.

El objetivo de estas pruebas es verificar tanto el funcionamiento esperado como los errores de autenticación, autorización, recursos inexistentes y conflictos de negocio.

## Ejecución de las pruebas

Requiere Docker corriendo localmente (Testcontainers levanta un contenedor `postgres:16-alpine` para los tests de repository y controller).

```bash
# Ejecutar toda la suite de pruebas
./mvnw test

# Ejecutar una clase de test específica
./mvnw test -Dtest=ReviewControllerTest

# Ejecutar todas las pruebas de un módulo (repository + service + controller)
./mvnw test -Dtest=com.example.sss001.review.*

# Ejecutar varias clases puntuales
./mvnw test -Dtest=PatientServiceTest,PatientRepositoryTest,ReviewServiceTest
```

---

# 14. GitHub y Gestión del Proyecto

El desarrollo del proyecto se realiza mediante Git y GitHub.

El repositorio permite mantener el historial de cambios y organizar la evolución del backend.

La documentación del proyecto se mantiene en `README.md`, mientras que la API está documentada mediante la colección de Postman `Salud_al_Toque_Postman_Collection.json` ubicada en la raíz del repositorio, la cual cubre todos los endpoints descritos en la sección 7, con tokens separados por rol (`token`, `professionalToken`, `adminToken`) y un flujo de integración completo de eventos.

---

# 15. Conclusiones y Trabajo Futuro

## Logros del Proyecto

Se desarrolló un backend funcional para una plataforma de servicios médicos, incorporando:

- Arquitectura modular.
- Persistencia con JPA.
- PostgreSQL.
- API REST.
- DTOs.
- Autenticación JWT.
- Spring Security.
- Roles y autorización.
- Manejo global de excepciones.
- Gestión de pacientes y profesionales.
- Citas médicas.
- Servicios y disponibilidad.
- Reseñas.
- Favoritos.
- Pruebas automatizadas.
- Eventos de dominio (`UserRegisteredEvent`, `AppointmentCreatedEvent`, `ReviewCreatedEvent`).
- Procesamiento asíncrono mediante `@Async`.
- Notificaciones de desarrollo mediante Mailpit.
- Flujo de citas: creación, aceptación, rechazo y completado.
- Flujo de reseñas posterior a una cita completada.

La solución permite representar las principales operaciones del dominio de Salud al Toque y proporciona una base preparada para ser consumida por una aplicación web o móvil.

## Aprendizajes Clave

El desarrollo permitió aplicar conceptos de arquitectura en capas, persistencia con JPA, diseño de APIs REST, seguridad mediante JWT, manejo de excepciones y pruebas automatizadas.

También permitió comprender la importancia de separar las entidades de persistencia de los objetos utilizados por la API mediante DTOs.

## Trabajo Futuro

Entre las mejoras previstas se encuentran:

- Implementar paginación.
- Añadir versionado de API.
- Incorporar Swagger/OpenAPI.
- Configurar CI/CD mediante GitHub Actions.
- Incorporar un sistema más completo de administración.

---

# 16. Apéndices

## A. Licencia

Actualmente la licencia del proyecto debe ser definida por el equipo.

**Licencia:** [Completar]

## B. Referencias

- CS 2031 — Desarrollo Basado en Plataforma.
- Laboratorio Semana 04 — Testing con Spring Boot.
- Laboratorio Semana 06 — Spring Security, JWT y roles/autorización.
- Documentación oficial de Spring Boot.
- Documentación oficial de Spring Security.
- Documentación de PostgreSQL.
- Documentación de Testcontainers.

## C. Variables de Entorno

Para producción se recomienda configurar los valores sensibles mediante variables de entorno:

```text
DB_HOST
DB_PORT
DB_NAME
DB_USER
DB_PASSWORD
JWT_SECRET
JWT_EXPIRATION
MAIL_HOST
MAIL_PORT
MAIL_USERNAME
MAIL_PASSWORD
```

No se deben almacenar contraseñas, claves JWT u otras credenciales directamente en el repositorio.

---

## Estado actual del proyecto

El backend se encuentra funcional para las operaciones principales del dominio. Actualmente incorpora eventos de dominio, procesamiento asíncrono con `@Async`, notificaciones de desarrollo mediante Mailpit, estados de aceptación, rechazo y completado de citas, reseñas posteriores a citas completadas y una colección Postman que cubre la totalidad de los endpoints de la API con autorización por rol mediante tres tokens separados.
