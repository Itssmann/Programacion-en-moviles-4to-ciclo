Prompts utilizados — Clínica SaludPlus

Commit 1 — Rediseño visual de la aplicación

Prompt 1. Mejora inicial de la interfaz

Estoy desarrollando una aplicación Android llamada **Clínica SaludPlus — App Paciente**, utilizando Kotlin, Jetpack Compose y Material 3, para un laboratorio de programación móvil de Tecsup.

Estoy trabajando en la rama `con-ia`, donde debo mejorar la aplicación utilizando inteligencia artificial. La rama `sin-ia` contiene la implementación manual y no debe modificarse.

Ya realizaste una primera mejora visual en los archivos `Color.kt`, `Theme.kt`, `Componentes.kt`, `HomeScreen.kt`, `EspecialidadesScreen.kt` y `MedicosScreen.kt`. Estos cambios todavía no tienen commit.

Ahora quiero que mejores y completes ese rediseño visual**, tomando como referencia las siete pantallas del documento del laboratorio: Splash, Registro, Inicio, Especialidades, Médicos, Fecha y hora, y Confirmación de cita.

Objetivo general

Conseguir una interfaz moderna, atractiva y coherente con una aplicación real de citas médicas. Utiliza una identidad visual azul y blanca, tarjetas redondeadas, iconos médicos, jerarquía visual clara y una distribución similar a la referencia del laboratorio.

Mejoras por pantalla

1. SplashScreen.kt
- Mejorar la pantalla de bienvenida con el nombre Clínica SaludPlus.
- Incorporar un icono o ilustración médica utilizando recursos disponibles.
- Destacar el botón Comenzar.
- Utilizar una composición centrada y agradable.

2. RegistroScreen.kt y LoginScreen.kt
- Mejorar visualmente los formularios.
- Incorporar iconos apropiados en los campos.
- Mejorar el espaciado, las etiquetas y los botones.
- Mantener los formularios funcionales y sus validaciones existentes.

3. HomeScreen.kt
- Mantener el encabezado de bienvenida que ya implementaste.
- Agregar cuatro accesos rápidos: Agendar cita, Mis citas, Mi perfil y Resultados.
- Mantener las especialidades destacadas mediante `LazyRow`.
- Mejorar las tarjetas, iconos, espaciado y jerarquía visual.
- Conservar el botón para ver todas las especialidades.
- No dejar botones decorativos: todos los accesos rápidos deben navegar correctamente.

4. EspecialidadesScreen.kt
- Mantener el listado mediante `LazyColumn`.
- Agregar un campo de búsqueda funcional para filtrar especialidades por nombre.
- Mantener las tarjetas con iconos y flechas.
- Mejorar el diseño del encabezado y la separación entre elementos.

5. MedicosScreen.kt
- Mantener el listado mediante `LazyColumn`.
- Mostrar avatar o ilustración, nombre, especialidad, experiencia y calificación.
- Mejorar el diseño de las tarjetas y el botón para seleccionar médico.
- No inventar fotografías de médicos reales.

6. FechaHoraScreen.kt
- Mejorar la selección de fecha y hora con un diseño similar a un calendario.
- Mantener `LazyVerticalGrid` para los horarios.
- Resaltar visualmente la fecha y el horario seleccionados.
- Mantener el botón Continuar deshabilitado hasta completar la selección.

7. ConfirmarCitaScreen.kt y CitaExitosaScreen.kt
- Mostrar un resumen de la cita con iconos y tarjetas.
- Destacar el médico, fecha, hora y estado de la reserva.
- Mejorar el botón de confirmación.
- Diseñar una pantalla de éxito más atractiva, con un icono de confirmación.

8. MisCitasScreen.kt, DetalleCitaScreen.kt, PerfilScreen.kt y ResultadosScreen.kt
- Aplicar la misma identidad visual azul y blanca.
- Mejorar el diseño de las tarjetas.
- Hacer que Perfil tenga un encabezado atractivo con avatar, nombre y datos del paciente.
- Mantener los datos y funciones existentes.
- Evitar que estas pantallas conserven colores gris/lila que no combinen con la nueva paleta.

9. Componentes.kt y ui/theme/
- Reutilizar componentes siempre que sea posible.
- Mantener una paleta de colores consistente.
- Mejorar la barra inferior y los indicadores de selección.
- Respetar Material 3.

Restricciones importantes

- No modificar la rama `sin-ia`.
- No modificar los modelos de datos ni `Repositorio.kt` en este commit.
- No implementar todavía una nueva gestión de sesión ni cambiar la autenticación.
- No agregar Firebase ni bases de datos.
- No eliminar las funciones existentes ni cambiar sus parámetros sin actualizar correctamente las llamadas.
- Puedes modificar `AppNavigation.kt` únicamente si es necesario para conectar los cuatro accesos rápidos de Inicio.
- Mantener `LazyRow`, `LazyColumn` y `LazyVerticalGrid`.
- No agregar comentarios al código.
- No incorporar dependencias innecesarias.
- Mantener una implementación comprensible para un estudiante de cuarto ciclo.
- No realizar commits ni push automáticamente.

Forma de trabajo

1. Revisa primero los archivos existentes y conserva las mejoras visuales que ya implementaste.
2. Realiza los cambios directamente sobre el proyecto actual.
3. Comprueba que las pantallas estén correctamente conectadas.
4. Evita dejar funciones sin utilizar o botones que no hagan nada.
5. Al finalizar, indícame qué archivos modificaste, qué mejoras realizaste.

Respuesta resumida de Gemini

Gemini informó que realizó un rediseño integral de la aplicación.
Se mejoraron las pantallas de bienvenida, registro, inicio de sesión, especialidades, médicos, selección de fecha y hora, confirmación, citas, resultados y perfil.

Commit IA 2 — Mejoras funcionales y calendario dinámico

Prompt 2 — Gestión de sesión, citas y validaciones de registro
Estoy desarrollando Clínica SaludPlus — App Paciente con Kotlin, Jetpack Compose, Material 3 y Navigation Compose.

Estoy trabajando en la rama `con-ia`. El primer commit asistido por IA se enfocó en el rediseño visual. Ahora necesito implementar mejoras funcionales, corregir errores encontrados en las pruebas y mantener el diseño existente.

Problemas detectados

1. Cuando registro otro usuario e inicio sesión con su cuenta, Inicio sigue mostrando "¡Hola, Luis!".
2. La pantalla Perfil también sigue mostrando los datos de Luis.
3. No existe una opción para cerrar sesión.
4. Si estoy completando el formulario de registro, abro Términos y condiciones y después regreso, los datos escritos desaparecen.
5. Las citas se registran con `usuarioId = 1`, independientemente de quién haya iniciado sesión.
6. El registro no valida correctamente los formatos del correo electrónico y del teléfono celular peruano.

1. Sesión del usuario

- Revisar `Usuario.kt`, `Repositorio.kt`, `LoginScreen.kt`, `RegistroScreen.kt`, `HomeScreen.kt`, `PerfilScreen.kt` y `AppNavigation.kt`.
- Implementar una sesión sencilla en memoria, compatible con Jetpack Compose.
- Al iniciar sesión, guardar el usuario autenticado.
- Mostrar el nombre real del paciente en Inicio.
- Mostrar en Perfil el nombre, correo y teléfono del paciente autenticado.
- No utilizar nombres ni IDs fijos para identificar al usuario actual.

2. Cerrar sesión

- Agregar un botón visible "Cerrar sesión" en Perfil.
- Al presionarlo, limpiar el usuario autenticado.
- Navegar a Login eliminando las pantallas privadas del historial.
- Impedir que el botón Atrás permita volver a Inicio después de cerrar sesión.

3. Conservar el formulario de registro

- Conservar todos los datos introducidos cuando el usuario abra Términos y condiciones y regrese.
- Revisar cómo se maneja el estado del formulario.
- Utilizar `rememberSaveable` o un ViewModel compartido con el alcance adecuado.
- Mantener las validaciones de los campos obligatorios y contraseñas.

4. Citas por paciente

- Registrar las citas utilizando el ID del usuario autenticado.
- Mostrar en Mis citas solamente las citas del paciente actual.
- Mantener el detalle de cada cita.
- Evitar que un usuario pueda consultar citas pertenecientes a otro paciente.

5. Bloqueo de horarios ocupados

- Impedir reservar el mismo médico, fecha y hora más de una vez, aunque sean usuarios diferentes.
- Mostrar visualmente los horarios ocupados y deshabilitar su selección.
- Mantener `LazyVerticalGrid`.
- Volver a validar la disponibilidad antes de confirmar una cita.
- Evitar registros duplicados por pulsaciones repetidas.

6. Validación del correo electrónico

- El campo debe ser obligatorio.
- Debe contener `@` y tener un formato válido, como `usuario@gmail.com`.
- No permitir correos incorrectos como `usuariogmail.com`, `usuario@` o `@gmail.com`.
- Mostrar un mensaje de error debajo del campo.
- Evitar registrar dos usuarios con el mismo correo electrónico.

7. Validación del teléfono celular peruano

- El campo debe ser obligatorio.
- Debe comenzar con `9`.
- Debe contener exactamente nueve dígitos numéricos.
- No permitir letras, espacios ni caracteres especiales.
- Limitar la entrada a un máximo de nueve dígitos.
- Mostrar mensajes de error claros cuando el número sea incorrecto.

8. Comportamiento general del registro

- Validar todos los campos antes de registrar al paciente.
- No permitir continuar con datos incorrectos.
- Mantener los valores escritos al regresar desde Términos y condiciones.
- Conservar el diseño azul y blanco existente.
- Mostrar los errores directamente debajo de los campos correspondientes.

Restricciones

- Trabajar únicamente en la rama `con-ia`.
- No modificar `sin-ia`.
- Mantener el diseño visual existente.
- No incorporar Firebase, Room ni bases de datos.
- Utilizar almacenamiento en memoria.
- No modificar innecesariamente los nombres de funciones, clases o paquetes.
- No agregar comentarios al código.
- Mantener una implementación comprensible para un estudiante de cuarto ciclo.
- No realizar commits ni push automáticamente.
- No crear todavía `PROMPTS.md`; se hará en el tercer commit.

Primero revisa el funcionamiento actual y después implementa las correcciones directamente en los archivos correspondientes.

Al finalizar, explica los archivos modificados.

Respuesta resumida de Gemini

Gemini implementó sesiones dinámicas, cierre de sesión, validaciones de correo y teléfono, conservación del formulario y bloqueo de horarios ocupados.

Prompt 3 - Calendario dinámico con LocalDate

Esta es la tercera instrucción que te envío para el segundo commit asistido por IA.

1. Calendario dinámico con LocalDate

Revisa `FechaHoraScreen.kt` y reemplaza cualquier lista de fechas fijas por fechas calculadas dinámicamente utilizando `java.time.LocalDate`.

El calendario debe cumplir exactamente lo siguiente:

- Mostrar los próximos 5 días hábiles a partir de la fecha actual del dispositivo.
- Excluir sábados y domingos.
- No mostrar fechas pasadas.
- Si hoy es un día hábil, incluirlo entre las fechas disponibles.
- Si hoy es sábado o domingo, comenzar desde el siguiente lunes.
- Mantener la selección visual de la fecha mediante tarjetas horizontales y `LazyRow`.
- Mostrar el día de la semana y el número del día en cada tarjeta.

2. Navegación mediante flechas

Agregar flechas de navegación para avanzar y retroceder entre grupos de cinco días hábiles.

- La flecha derecha permite avanzar al siguiente grupo de días hábiles.
- La flecha izquierda permite regresar al grupo anterior.
- No permitir retroceder antes del grupo correspondiente a la semana actual.
- Deshabilitar visualmente la flecha izquierda cuando el usuario esté en el primer grupo.
- Al cambiar de grupo, mantener una selección válida o reiniciarla si la fecha anterior ya no está visible.
- No utilizar fechas fijas ni cálculos basados en un mes específico.

3. Mes y año dinámicos

Agregar un encabezado que muestre el mes y año correspondiente a las fechas visibles.

Por ejemplo:

"Octubre 2026"

- Utilizar `java.time` y `Locale("es", "ES")` o un locale español equivalente.
- Actualizar automáticamente el encabezado al avanzar o retroceder.
- Manejar correctamente cambios de mes y de año.
- Si el grupo visible contiene días de dos meses distintos, mostrar un encabezado claro que represente ambos meses.

4. Recalcular horarios disponibles

Al seleccionar otra fecha:

- Actualizar los horarios disponibles de ese día.
- Reiniciar la hora seleccionada anteriormente.
- Consultar `Repositorio.estaHorarioOcupado(medicoId, fecha, hora)` para identificar reservas existentes.
- Mostrar los horarios ocupados con el indicador "Ocupado".
- Deshabilitar los horarios reservados.
- Mantener el uso obligatorio de `LazyVerticalGrid`.
- No permitir continuar sin seleccionar una fecha y un horario disponible.
- Si la fecha seleccionada es hoy, evitar ofrecer horarios que ya hayan pasado según la hora actual del dispositivo.

5. Formato de fecha en español en la Pantalla 7

Revisa `ConfirmarCitaScreen.kt`.

La fecha seleccionada debe mostrarse en un formato legible en español, por ejemplo:

"Martes 15 de septiembre de 2026"

Utiliza `DateTimeFormatter` y un locale español.

IMPORTANTE: conserva internamente la fecha en formato ISO `yyyy-MM-dd` para las rutas, las citas y las comprobaciones de disponibilidad. Utiliza el formato español únicamente para mostrar la fecha al paciente.

6. Compatibilidad con las funcionalidades existentes

- Mantener la navegación actual de `AppNavigation.kt`.
- Conservar el usuario autenticado y las citas asociadas a su ID.
- No romper el bloqueo de horarios ocupados.
- Mantener la validación final de disponibilidad antes de confirmar la reserva.
- Evitar reservas duplicadas.
- Conservar el diseño azul y blanco que ya implementaste.
- Mantener los componentes reutilizables existentes.
- No modificar innecesariamente otros archivos.

7. Restricciones

- Trabajar únicamente en `con-ia`.
- No modificar `sin-ia`.
- No agregar Firebase, Room ni bases de datos.
- No agregar dependencias innecesarias.
- No agregar comentarios al código.
- Mantener el código sencillo y comprensible para un estudiante de cuarto ciclo.
- No crear todavía `PROMPTS.md`; se realizará en el tercer commit.
- No realizar commits ni push automáticamente.

8. Comprobaciones

Al terminar, verifica especialmente estos casos:

1. El calendario muestra cinco días hábiles desde hoy.
2. No aparecen sábados ni domingos.
3. Las flechas avanzan y retroceden correctamente.
4. No se puede retroceder antes de la semana actual.
5. El mes y año cambian correctamente.
6. Cambiar de fecha reinicia la hora seleccionada.
7. Los horarios ocupados siguen bloqueados.
8. La pantalla de confirmación muestra la fecha en español.
9. El registro de citas sigue funcionando.

Implementa las mejoras directamente y explícame qué archivos modificaste.

Respuesta resumida de Gemini

Gemini implementó un calendario con `LocalDate`, cinco días hábiles, navegación por semanas, actualización de horarios y fechas en español.