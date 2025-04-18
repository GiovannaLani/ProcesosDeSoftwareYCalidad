![example workflow](https://github.com/GiovannaLani/ProcesosDeSoftwareYCalidad/actions/workflows/maven.yml/badge.svg)

# Viinted

## Descripción
Viinted es una aplicación web inspirada en Vinted, diseñada para la compra y venta de items de segunda mano. Esta aplicación ha sido desarrollada con un backend en Spring Boot y un frontend adaptable.

El proyecto ha sido realizado con fines educativos como parte de una asignatura, aplicando metodologías ágiles SCRUM y buenas prácticas de desarrollo de software.

## Tecnologías utilizadas
- **Backend:** Spring Boot, Java
- **Base de datos:** MySQL
- **Metodología:** SCRUM

## Instalación y Ejecución
### Requisitos previos
- Java 17+
- Maven
- MySQL

### Pasos para ejecutar el proyecto
1. Clonar el repositorio:
   ```sh
   git clone https://github.com/GiovannaLani/ProcesosDeSoftwareYCalidad.git
   ```
2. Configurar la base de datos en `application.properties`.
3. Ir a `dbsetup.sql` y ejecutar su contenido en MySQL.
   ```sh
   DROP USER IF EXISTS 'spq'@'%';
   CREATE USER IF NOT EXISTS 'spq'@'%' IDENTIFIED BY 'spq';

   DROP SCHEMA IF EXISTS restapidb;
   CREATE SCHEMA restapidb;

   GRANT ALL ON restapidb.* TO 'spq'@'%';
   FLUSH PRIVILEGES;
   ```
5. Compilar y ejecutar el servidor:
   ```sh
   mvn spring-boot:run
   ```
6. Acceder a la aplicación desde el navegador en `http://localhost:8081`

## Características
- Registro e inicio de sesión de usuarios
- Publicación de artículos en venta
- Búsqueda y filtrado de productos
- Sistema de valoraciones y comentarios
- Compra de items

 entre muchos otros...
  
## Creadores
Este proyecto ha sido desarrollado por:
- **Giovanna Lani** - [GiovannaLani](https://github.com/GiovannaLani)
- **Joana Artetxe** - [jArtetxe](https://github.com/jArtetxe)
- **Patricia Rodriguez** - [PatriciaRdrz](https://github.com/PatriciaRdrz)
- **Iñaki Ruiz de Alegría** - [InakiRuizDeAlegria](https://github.com/InakiRuizDeAlegria)
- **Sofía Fernandez** - [soferpel](https://github.com/soferpel)


