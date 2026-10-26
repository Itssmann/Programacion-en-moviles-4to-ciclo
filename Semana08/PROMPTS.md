# PROMPTS.md - HuellitasVet

## Laboratorio Semana 08 - Mejoras con IA

**Curso:** Programación en Móviles  
**Proyecto:** HuellitasVet  
**Rama:** `con-ia`  
**Herramienta de IA:** Gemini en Android Studio

## 1. Objetivo

En esta segunda parte del laboratorio utilicé Gemini para implementar nuevas funcionalidades en HuellitasVet, tomando como base el proyecto desarrollado en la rama `sin-ia`.

La idea fue mejorar la aplicación sin perder las funciones anteriores, respetando la arquitectura MVVM y utilizando Room para guardar los datos.

## 2. Mejora 1: Gráfico de evolución del peso

**Prompt utilizado:**

> Implementa un gráfico de evolución del peso en la ficha de cada mascota, dentro de la pestaña Historial clínico. Utiliza los pesos registrados en las citas atendidas, ordenados por fecha. Dibuja el gráfico con Canvas de Jetpack Compose, sin librerías externas. Respeta MVVM y utiliza VetRepository para obtener los datos. No inventes registros ni modifiques otras pantallas. Compila el proyecto y no hagas commit.

**Cambios realizados:**

- Se creó el componente `GraficoLineas.kt`.
- Se agregaron consultas para obtener los pesos registrados.
- Se modificó el ViewModel de mascotas.
- Se agregó el gráfico al historial clínico.
- El gráfico utiliza los datos almacenados en Room.

**Prueba realizada:**

Se abrió la ficha de Luna y se comprobó que aparecía un registro de peso de 18.2 kg con su fecha correspondiente.

**Commit:**

`ia: agrega grafico de peso en la ficha de la mascota`

## 3. Mejora 2: Reprogramación de citas

**Prompt utilizado:**

> Agrega una opción para reprogramar citas pendientes o confirmadas desde Mis citas. El dueño debe poder cambiar la fecha y hora, pero no cuando falten menos de dos horas para la cita. No permitas horarios pasados ni ocupados. Conserva el ID y los demás datos de la cita. Actualiza Room mediante DAO, Repository y ViewModel. Muestra una confirmación antes de guardar y verifica que el cambio aparezca en la agenda del veterinario. No hagas commit.

**Cambios realizados:**

- Se agregó la opción de reprogramar citas.
- Se implementó la selección de una nueva fecha y hora.
- Se añadieron validaciones para evitar horarios ocupados.
- Se mantuvieron los datos originales de la cita.
- Se utilizó Room para guardar los cambios.

**Commit:**

`ia: agrega reprogramacion de citas`

## 4. Mejora 3: Búsqueda de pacientes

**Prompt utilizado:**

> Mejora la pantalla Pacientes del veterinario agregando una barra de búsqueda por nombre de mascota o dueño. La búsqueda debe funcionar mientras se escribe, ignorando mayúsculas y espacios sobrantes. Agrega filtros por especie: Todos, Perro, Gato y Otro. Permite combinar la búsqueda con los filtros. Implementa la lógica en PacientesViewModel utilizando StateFlow y conserva las tarjetas y la navegación existentes. No hagas commit.

**Cambios realizados:**

- Se agregó una barra de búsqueda en la pantalla de pacientes.
- Se implementaron filtros por especie.
- Se permitió combinar búsqueda y filtros.
- Se agregó un mensaje cuando no hay resultados.
- Se mantuvo la arquitectura MVVM.

**Commit:**

`ia: agrega busqueda de pacientes`

## 5. Mejora 4: Registro de usuarios

**Prompt utilizado:**

> Implementa el registro de usuarios en HuellitasVet para los roles Dueño y Veterinario. Crea una pantalla con nombre completo, correo, contraseña, confirmación y selección de rol. Valida los campos obligatorios, el formato del correo, la contraseña y los correos duplicados. Guarda las cuentas en Room y permite iniciar sesión con ellas. Respeta MVVM y conserva los usuarios de prueba existentes. No hagas commit.

**Prompts adicionales:**

> Corrige la pantalla de bienvenida para que el botón Comenzar abra directamente Registro y la opción Ya tengo una cuenta abra Login. Mantén el diseño actual y evita pantallas duplicadas.

> Haz que el inicio y el perfil muestren el nombre y correo reales del usuario autenticado. Si cambia de cuenta, los datos mostrados también deben cambiar. No utilices nombres escritos directamente en el código.

**Cambios realizados:**

- Se creó `RegistroScreen.kt`.
- Se creó `RegistroViewModel.kt`.
- Se agregó la validación de datos.
- Se implementó el registro en Room.
- Se modificó la navegación desde la pantalla de bienvenida.
- Se agregó el manejo de contraseñas mediante `PasswordUtils.kt`.
- Se actualizaron las consultas de usuarios.

**Problema encontrado:**

Durante las pruebas, la aplicación se cerraba al iniciar porque Room detectaba una diferencia entre la estructura esperada y la tabla `usuarios`.

El problema era que la migración creaba un índice único para el correo, pero este índice no estaba declarado en la entidad `Usuario`.

Se corrigió agregando el índice único en `Usuario.kt`, conservando la base de datos y los registros existentes.

**Commit:**

`ia: agrega registro de usuarios por rol`

## 6. Problemas durante el desarrollo

Durante la implementación del gráfico de peso, Gemini modificó algunos archivos de configuración de Gradle y aparecieron errores de compilación.

Para solucionarlo, se revisaron los cambios, se recuperaron los archivos de configuración necesarios y se volvió a compilar el proyecto.

También se revisaron los errores de Logcat para encontrar el problema de migración de Room durante el registro de usuarios.

Estas situaciones ayudaron a comprender que los cambios generados por IA deben revisarse y probarse antes de guardarlos.

## 7. Tecnologías utilizadas

- Kotlin
- Jetpack Compose
- Material Design 3
- Room Database
- DataStore
- MVVM
- StateFlow
- Canvas
- Git y GitHub
- Gemini en Android Studio

## 8. Conclusión

En esta fase utilicé Gemini como apoyo para agregar funcionalidades nuevas a HuellitasVet. Se trabajó en el gráfico de peso, la reprogramación de citas, la búsqueda de pacientes y el registro de usuarios.

También fue necesario revisar errores de compilación y de base de datos, porque no todos los cambios generados por la IA funcionaron correctamente al primer intento.

Con estas mejoras, la aplicación tiene más funciones que la versión inicial de `sin-ia` y permite comparar el trabajo base con la versión mejorada mediante IA.