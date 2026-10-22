# HuellitasVet - Semana 08

## Descripción

HuellitasVet es una aplicación móvil desarrollada en Android Studio con Kotlin y Jetpack Compose para el laboratorio de la Semana 08 del curso de Programación en Móviles.

La aplicación simula el funcionamiento de una clínica veterinaria y cuenta con dos perfiles: **Dueño** y **Veterinario**. Cada uno tiene sus propias pantallas y funciones.

Para guardar la información se utilizó Room, mientras que DataStore permite guardar la sesión y algunas preferencias de la aplicación.

## Tecnologías utilizadas

- Kotlin
- Jetpack Compose
- Material Design 3
- Room Database
- DataStore
- ViewModel
- Navigation Compose
- Android Studio
- Git y GitHub

## Funcionalidades realizadas

### Perfil Dueño

En este perfil el usuario puede:

- Iniciar sesión con su cuenta.
- Ver sus mascotas registradas.
- Agregar y editar mascotas.
- Consultar los datos y el historial clínico de una mascota.
- Revisar los servicios de la veterinaria.
- Solicitar citas seleccionando mascota, servicio, fecha y hora.
- Consultar sus citas y cancelarlas si cumple con el tiempo permitido.
- Cambiar el modo oscuro y configurar los recordatorios.
- Cerrar sesión.

### Perfil Veterinario

El veterinario cuenta con las siguientes funciones:

- Ver un panel con información de las citas e ingresos.
- Consultar los gráficos de citas por estado y servicio.
- Revisar la agenda de la clínica.
- Confirmar citas pendientes.
- Atender citas registrando el peso, diagnóstico y tratamiento.
- Buscar pacientes y consultar su información.
- Agregar, editar y activar o desactivar servicios.
- Configurar las notificaciones y la opción de aceptar citas.
- Cerrar sesión.

## Base de datos

Se utilizó **Room** para almacenar los datos de la aplicación.

Las principales tablas son:

- **Usuario:** guarda los datos de las personas que ingresan a la aplicación.
- **Mascota:** almacena los datos de las mascotas y su dueño.
- **Servicio:** contiene los servicios que ofrece la veterinaria.
- **Cita:** registra las reservas y la información de las atenciones.

También se utilizaron DAO, Repository y ViewModel para organizar mejor el código y trabajar con la información.

## DataStore

Se utilizó DataStore para guardar algunas preferencias, como:

- La sesión del usuario.
- El modo oscuro.
- Las notificaciones.
- La opción de aceptar citas.

De esta manera, las preferencias se mantienen aunque se cierre la aplicación.

## Diseño de la aplicación

Para realizar las pantallas se tomaron como referencia los mockups del PDF del laboratorio.

Se utilizaron dos colores principales:

- **Verde:** para el perfil Dueño.
- **Azul:** para el perfil Veterinario.

También se utilizaron componentes de Material Design 3, iconos y barras de navegación para facilitar el uso de la aplicación.

## Usuarios de prueba

| Perfil | Correo | Contraseña |
|---|---|---|
| Dueño | ana@correo.test | 1234 |
| Dueño | luis@correo.test | 1234 |
| Veterinario | vet@huellitas.test | admin |

## Cómo ejecutar el proyecto

1. Abrir la carpeta `Semana08` en Android Studio.
2. Esperar a que Gradle termine de sincronizar.
3. Iniciar un emulador o conectar un celular.
4. Ejecutar la aplicación.
5. Ingresar con alguna de las cuentas de prueba.

## Trabajo con GitHub

El laboratorio se desarrolló utilizando commits para registrar los avances de cada parte del proyecto.

En la rama `sin-ia` se trabajó la implementación de las pantallas, la base de datos, la navegación y las funcionalidades solicitadas en el laboratorio.

La rama `con-ia` se utilizará para realizar mejoras adicionales y comparar los resultados.

## Conclusión

En este laboratorio se logró desarrollar una aplicación de veterinaria utilizando Kotlin y Jetpack Compose. Se trabajó con Room para guardar los datos, DataStore para las preferencias y ViewModel para manejar la información de las pantallas.

También se implementaron las funciones de los dos perfiles y se mejoró el diseño tomando como referencia los mockups del laboratorio.

Este trabajo ayudó a comprender mejor cómo organizar un proyecto Android y cómo conectar las pantallas con una base de datos local.
