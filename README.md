# user-registration-service MicroService

Este proyecto expone una **API RESTful** para el registro y autenticación de usuarios utilizando **Spring Boot** bajo el enfoque de **Arquitectura Hexagonal (Ports & Adapters)**.  
Todos los endpoints intercambian datos en formato **JSON** y utilizan **JWT** para el acceso seguro a recursos protegidos.

---

## Acceso y ejecución local

Para compilar, probar y empaquetar el proyecto localmente, desde la **raíz del repositorio**, ejecutar los siguientes comandos:

```bash
mvn clean compile
mvn test
mvn verify
```

El comando `verify` también genera el reporte de cobertura con **JaCoCo**.

Ruta del reporte:
```
target/site/jacoco/index.html
```

---

## Base de Datos (H2 en memoria)

La aplicación utiliza una base de datos **H2 en memoria** durante el desarrollo.  
No requiere configuración adicional.

```
URL: http://localhost:8080/h2-console
Driver: org.h2.Driver
JDBC URL: jdbc:h2:mem:usersdb
Username: sa
Password: 
```

---

## Ejemplo de llamada - Registrar Usuario

**Request**

```bash
curl --location --request POST 'http://localhost:8080/api/users' --header 'Content-Type: application/json' --data-raw '{
  "name": "Alessandro Riega",
  "email": "alessandro.riega@gmail.com",
  "password": "password.123",
  "phones": [
    {
      "number": "1234567",
      "citycode": "1",
      "countrycode": "57"
    }
  ]
}'
```

**Response**

```json
{
  "id": "9bcb3c26-8e10-4e92-9c44-b2eac3dc3f10",
  "name": "Alessandro Riega",
  "email": "alessandro.riega@gmail.com",
  "phones": [
    {
      "number": "1234567",
      "citycode": "1",
      "countrycode": "57"
    }
  ],
  "created": "2025-11-10T15:42:13Z",
  "modified": "2025-11-10T15:42:13Z",
  "last_login": "2025-11-10T15:42:13Z",
  "token": "eyJhbGciOiJIUzUxMiJ9...",
  "isactive": true
}
```

---

## Ejemplo de llamada - Obtener Productos (Requiere JWT)

**Request**

```bash
curl --location --request GET 'http://localhost:8080/api/products' --header 'Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJqdWFuQHJvZHJpZ3Vlei5vcmciLCJleHAiOjE3MDAwMDAwMDB9.abc123XYZ'
```

**Response**

```json
[
  {
    "id": "P-1001",
    "name": "Teclado Mecánico",
    "price": 149.90
  },
  {
    "id": "P-1002",
    "name": "Mouse Gamer",
    "price": 89.50
  }
]
```

Si el token es inválido o ha expirado:

```json
{
  "mensaje": "No autorizado"
}
```

---

## Librerías Principales

```
* Java SDK 17
* Maven 3.x o superior
* Spring Boot 3.x
* Lombok
* Spring Data JPA (H2 in-memory)
* Spring Security (JWT)
* MapStruct
* Validation API (Jakarta)
```

---

## Librerías y Plugins de Test

```
* JUnit 5 
* Mockito + Mockito JUnit Runner
* AssertJ
* JaCoCo (Coverage)
```

---

## Ejecución de la Aplicación

Para iniciar la aplicación en local (desde la raíz del proyecto):

```bash
mvn spring-boot:run
```

La aplicación se levantará en el puerto por defecto:

```
http://localhost:8080
```

---

## Contrato Swagger (OpenAPI)

La documentación de la API se encuentra en el archivo:

```
src/main/resources/swagger/openapi.yaml
```

Este archivo define todos los endpoints, modelos y respuestas de la API.  
Puede abrirse manualmente en **SwaggerHub**, **Postman** o cualquier **visualizador OpenAPI** para navegar los endpoints de forma interactiva.

---

## Docker

Para generar y ejecutar la imagen Docker de este microservicio:

### 1. Crear el archivo .jar
Desde la **raíz del proyecto**, ejecutar:

```bash
mvn clean package -DskipTests
```

Esto generará el archivo:
```
target/user-registration-api-0.0.1.jar
```

### 2. Construir la imagen Docker
Ejecutar el siguiente comando desde el **directorio raíz** (donde está el pom.xml):

```bash
docker build -t user-registration-service:0.0.1 -f devops/Dockerfile .
```

Si deseas ejecutar el comando dentro del directorio `devops`, debes indicar el contexto del build apuntando a la raíz:

```bash
cd devops
docker build -t user-registration-service:0.0.1 -f Dockerfile ..
```

### 3. Levantar el contenedor
```bash
docker run -p 8080:8080 user-registration-service:0.0.1
```

### 4. Acceso
La aplicación estará disponible en:
```
http://localhost:8080
```

---

## Arquitectura del Proyecto

El sistema sigue el enfoque **Arquitectura Hexagonal (Ports & Adapters)**, separando responsabilidades en capas:

- **Application Layer** → Casos de uso (`RegisterUserService`)
- **Domain Layer** → Entidades (`User`, `Phone`) y Puertos (`UserRepositoryPort`, `TokenProviderPort`, `PasswordEncoderPort`)
- **Infrastructure Layer** → Adaptadores (`UserRepositoryAdapter`, `JwtServiceAdapter`, `BCryptPasswordEncoderAdapter`)
- **Database** → H2 (en memoria)
- **API Layer** → Controladores (`UserController`, `ProductController`)
