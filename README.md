# OAuth2 Authentication Service

⚠ **Este README aún se está actualizando, por lo que la información no está completa.** ⚠

Este proyecto implementa un sistema de autenticación con OAuth2 en Spring Boot utilizando JWT. Actualmente, permite a los usuarios autenticarse con Google, gestionando el acceso de manera segura mediante tokens. Se planea agregar soporte para Facebook y GitHub en futuras versiones.

## Características

- Autenticación mediante OAuth2 con Google.
- Generación y validación de tokens JWT.
- Integración con Spring Security para la gestión de autenticación y autorización.
- Arquitectura modular para facilitar la escalabilidad con otros proveedores.
- Integración con base de datos MySQL para gestión de usuarios.

## Requisitos

- Java 17+
- Spring Boot 3+
- Maven
- MySQL
- Cuenta de desarrollador en Google para OAuth2

## Instalación

1. Clona el repositorio:
   ```sh
   git clone https://github.com/codigo04/oauth2Service.git
   cd oauth2Service
   ```
2. Configura las credenciales de Google en `application.properties`:
   ```properties
   spring.security.oauth2.client.registration.google.client-id=TU_CLIENT_ID
   spring.security.oauth2.client.registration.google.client-secret=TU_CLIENT_SECRET
   ```
3. Configura la base de datos MySQL en `application.properties`:
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/tu_basedatos
   spring.datasource.username=tu_usuario
   spring.datasource.password=tu_contraseña
   spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
   spring.jpa.database-platform=org.hibernate.dialect.MySQLDialect
   ```
4. Ejecuta la aplicación:
   ```sh
   mvn clean install
   mvn spring-boot:run
   ```

## Uso

1. Accede a la URL de autenticación: `http://localhost:8080/oauth2/authorize/google`
2. Inicia sesión con Google y recibe un token JWT.
3. Usa el token para acceder a rutas protegidas.

## Futuras Mejoras

- Soporte para Facebook y GitHub.
- Implementación de refresh tokens.

## Licencia

Este proyecto está bajo la licencia MIT. Consulta el archivo `LICENSE` para más detalles.

