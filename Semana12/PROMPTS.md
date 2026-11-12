# PROMPTS DE INTELIGENCIA ARTIFICIAL — POKÉDEX ABAD

## 1. Información general

- **Proyecto:** Pokédex Abad
- **Curso:** Programación en Móviles
- **Semana:** 12
- **Tecnologías:** Kotlin, Jetpack Compose, Material Design 3, MVVM, Firebase Authentication, Firestore, Room, Retrofit y Coil.
- **Rama sin IA:** `sin-ia`
- **Rama con IA:** `con-ia`
- **Herramientas utilizadas:** Gemini 3.5 Flash-Lite (Android Studio) y OpenAI Codex.

### Objetivo

Utilizar herramientas de inteligencia artificial para mejorar la interfaz gráfica, la experiencia de usuario y la presentación de información de Pokédex Abad, conservando las funcionalidades desarrolladas previamente.

Las mejoras se realizaron sobre la versión existente del proyecto, sin reemplazar su arquitectura MVVM ni eliminar sus funcionalidades.

---

## 2. Prompt 1 — Integración de imágenes Pokémon con Coil

**Herramienta:** Gemini

**Prompt utilizado (resumen representativo):**

> Analiza el proyecto Pokédex Abad y mejora la visualización del catálogo Pokémon mediante la integración de imágenes reales utilizando Coil.
>
> Implementa un componente reutilizable para cargar imágenes desde las URLs existentes, incluyendo estados de carga y manejo de errores.
>
> Integra las imágenes en las tarjetas del catálogo sin alterar la arquitectura MVVM, la navegación, los favoritos ni la persistencia local.
>
> Trabaja exclusivamente en Semana12, rama con-ia. Verifica la compilación y las pruebas. No realices commits ni push.

**Resultado obtenido:**

- Integración de Coil 2.7.0.
- Creación del componente reutilizable `PokemonImage.kt`.
- Visualización de imágenes Pokémon en el catálogo.
- Compilación exitosa.

---

## 3. Prompt 2 — Rediseño del catálogo Pokémon

**Herramienta:** Gemini

**Prompt utilizado (resumen representativo):**

> Moderniza la pantalla del catálogo Pokémon utilizando Jetpack Compose y Material Design 3.
>
> Implementa tarjetas modernas con imágenes, nombres, números de Pokédex y etiquetas de tipos.
>
> Agrega una barra de búsqueda por nombre, filtros por tipo Pokémon y opciones de ordenamiento por número o nombre.
>
> Mejora los estados vacíos y conserva la lógica existente de Room, Retrofit, Firebase y favoritos.
>
> No modifiques otras semanas ni realices commits.

**Resultado obtenido:**

- Rediseño de `PokemonListScreen.kt`.
- Actualización de `PokemonViewModel.kt`.
- Búsqueda dinámica de Pokémon.
- Filtros por tipo: Todos, Fuego, Agua y Planta.
- Ordenamiento por número y nombre.
- Tarjetas modernas con imágenes y favoritos.

---

## 4. Prompt 3 — Bienvenida, Login y Registro

**Herramienta:** Gemini

**Prompt utilizado (resumen representativo):**

> Rediseña las pantallas de Bienvenida, Login y Registro de Pokédex Abad utilizando Material Design 3.
>
> Implementa una identidad visual coherente, formularios modernos con OutlinedTextField, validaciones visuales, botones redondeados, estados de carga y opciones para mostrar u ocultar contraseñas.
>
> Mejora el tema global mediante ColorScheme, Typography y Shapes.
>
> Mantén intactos Firebase Authentication, MVVM y las rutas de navegación existentes.

**Resultado obtenido:**

- Modernización de `LoginScreen.kt` y `RegistroScreen.kt`.
- Actualización de `Theme.kt`.
- Mejoras en validaciones y presentación de formularios.
- Interfaz de autenticación coherente con la temática Pokémon.

---

## 5. Prompt 4 — Imagen Pokémon en la bienvenida

**Herramienta:** Gemini

**Prompt utilizado (resumen representativo):**

> Modifica la pantalla de bienvenida para mostrar el título POKÉDEX ABAD en la parte superior, destacando ABAD en rojo.
>
> Coloca una imagen grupal de Bulbasaur, Pikachu, Charmander y Squirtle en el centro de la pantalla.
>
> Debajo, muestra un mensaje de bienvenida y los botones Iniciar sesión y Crear cuenta nueva.
>
> Utiliza la imagen proporcionada como recurso local `pokemon_bienvenida.png`, con ContentScale.Fit para evitar recortes.
>
> Conserva la navegación y utiliza Material Design 3.

**Resultado obtenido:**

- Incorporación de `pokemon_bienvenida.png`.
- Rediseño de la pantalla de bienvenida en `AppNavigation.kt`.
- Mejor distribución de los elementos.
- Imagen Pokémon visible sin deformaciones.

---

## 6. Prompt 5 — Dashboard, Favoritos y Perfil

**Herramienta:** Gemini

**Prompt utilizado (resumen representativo):**

> Rediseña las pantallas de Inicio, Favoritos y Perfil utilizando Material Design 3.
>
> En Inicio, implementa un dashboard Pokémon con saludo personalizado, estadísticas reales y accesos rápidos.
>
> En Favoritos, utiliza tarjetas modernas con imágenes Coil, filtros por tipo y estados vacíos.
>
> En Perfil, presenta los datos reales del usuario autenticado y agrega confirmación antes de cerrar sesión.
>
> Conserva Firebase, Room, la navegación y la arquitectura MVVM.

**Resultado obtenido:**

- Rediseño de `InicioScreen.kt`.
- Rediseño de `FavoritosScreen.kt`.
- Primera modernización de `PerfilScreen.kt`.
- Integración de estadísticas y favoritos.

---

## 7. Prompt 6 — Panel de Administración con Material Design 3

**Herramienta:** Gemini

**Prompt utilizado (resumen representativo):**

> Rediseña el panel de administración como un dashboard moderno utilizando Material Design 3.
>
> Implementa tarjetas con estadísticas reales, encabezado personalizado, acciones para sincronizar el catálogo Pokémon y limpiar la caché local.
>
> Agrega indicadores de progreso, mensajes de éxito o error y diálogos de confirmación.
>
> Mantén las restricciones de acceso mediante el rol ADMIN y conserva las operaciones existentes.

**Resultado obtenido:**

- Rediseño inicial de `AdminScreen.kt`.
- Tarjetas con estadísticas del sistema.
- Acciones administrativas mejor organizadas.
- Confirmación antes de limpiar la caché.

---

## 8. Prompt 7 — Mejoras visuales avanzadas

**Herramienta:** Gemini

**Prompt utilizado (resumen representativo):**

> Realiza una segunda iteración visual para Inicio, Perfil y Administración.
>
> Las pantallas actuales tienen demasiado espacio vacío. Necesito una reorganización sustancial, con identidad Pokémon, tarjetas de distintos tamaños y mejor aprovechamiento del espacio.
>
> En Inicio, utiliza una imagen protagonista y muestra una vista previa de Pokémon favoritos.
>
> En Perfil, mejora la presentación de la identidad del entrenador.
>
> En Administración, reorganiza las estadísticas y las acciones de gestión.
>
> Conserva todos los cambios locales y no realices commits.

**Resultado obtenido:**

- Dashboard de Inicio con imagen Pokémon.
- Estadísticas reales y favoritos recientes.
- Mejoras de presentación en Perfil y Administración.
- Navegación desde Inicio hacia los detalles Pokémon.

---

## 9. Prompt 8 — Rediseño de detalles Pokémon

**Herramienta:** Gemini

**Prompt utilizado (resumen representativo):**

> Rediseña la pantalla de detalles Pokémon para que muestre una imagen grande del Pokémon seleccionado utilizando Coil.
>
> Utiliza un contenedor con fondo adaptado al tipo principal, nombre destacado, número de Pokédex, etiquetas de tipos y botón de favoritos.
>
> Presenta altura y peso mediante tarjetas Material 3, habilidades en secciones compactas y estadísticas reales mediante barras de progreso.
>
> Conserva la navegación, MVVM y los datos existentes.

**Resultado obtenido:**

- Rediseño de `DetalleScreen.kt`.
- Imagen Pokémon grande y dinámica.
- Tarjetas de altura y peso.
- Barras visuales de estadísticas.
- Botón de favoritos funcional.

---

## 10. Prompt 9 — Rediseño avanzado con Codex

**Herramienta:** OpenAI Codex

**Prompt utilizado (resumen representativo):**

> Actúa como desarrollador Android senior especializado en Kotlin, Jetpack Compose, Material Design 3 y MVVM.
>
> Inspecciona el proyecto Pokédex Abad y rediseña sustancialmente Perfil y Administración, porque todavía presentan interfaces demasiado básicas.
>
> Convierte Perfil en un carnet de entrenador Pokémon con avatar, datos reales de Firebase, estadísticas de colección e imágenes de favoritos con Coil.
>
> Convierte Administración en un centro de control con cabecera destacada, estadísticas reales, distribución por tipos y tarjetas de sincronización y mantenimiento.
>
> Conserva todos los cambios locales existentes y verifica la compilación y las pruebas.
>
> No realices commits, push, merge ni cambios de rama.

**Resultado obtenido:**

- Nuevo diseño de `PerfilScreen.kt` y `AdminScreen.kt`.
- Actualización de `PerfilViewModel.kt` y `AdminViewModel.kt`.
- Incorporación del componente reutilizable `PanelPokemon.kt`.
- Ajustes en el modelo de sesión y autenticación.
- Integración con la navegación.
- Incorporación de pruebas relacionadas con Perfil y Administración.

Codex alcanzó su límite de uso durante la revisión final, por lo que la verificación pendiente se continuó con Gemini.

---

## 11. Prompt 10 — Revisión del trabajo de Codex

**Herramienta:** Gemini

**Prompt utilizado (resumen representativo):**

> Continúa el trabajo que dejó pendiente Codex en Pokédex Abad.
>
> Inspecciona los cambios locales realizados en Perfil, Administración, autenticación, sesión y navegación.
>
> No reemplaces el trabajo de Codex ni vuelvas a implementar las pantallas desde cero.
>
> Verifica que las estadísticas sean reales, que las imágenes de favoritos funcionen con Coil, que el cierre de sesión conserve su confirmación y que las operaciones administrativas respeten el rol ADMIN.
>
> Ejecuta assembleDebug y testDebugUnitTest.
>
> Conserva todos los cambios locales y no realices commits.

**Resultado obtenido:**

- Revisión de la integración de Perfil y Administración.
- Compilación exitosa.
- Prueba unitaria ejecutada correctamente.
- Conservación de los cambios locales.
- Identificación de verificaciones manuales pendientes.

---

## 12. Comparación entre la versión sin IA y con IA

| Aspecto | Versión sin IA | Versión con IA |
|---|---|---|
| Interfaz | Diseño funcional y sencillo | Diseño moderno con Material 3 |
| Catálogo | Listado básico | Tarjetas con imágenes, filtros y búsqueda |
| Bienvenida | Interfaz inicial sencilla | Portada con imagen Pokémon |
| Autenticación | Formularios funcionales | Formularios modernos y adaptables |
| Inicio | Pantalla de navegación básica | Dashboard con estadísticas y favoritos |
| Favoritos | Listado funcional | Tarjetas visuales con Coil |
| Perfil | Información básica de cuenta | Carnet de entrenador con colección |
| Administración | Acciones administrativas básicas | Centro de control con estadísticas y gráficos |
| Detalles | Información y estadísticas | Imagen grande, tarjetas y barras visuales |

## 13. Verificaciones realizadas

Durante el desarrollo se utilizaron:

- Compilación Gradle mediante `:app:assembleDebug`.
- Pruebas unitarias mediante `:app:testDebugUnitTest`.
- Ejecución de la aplicación en el emulador Android.
- Revisión visual de las pantallas.
- Comprobación de imágenes Pokémon y favoritos.
- Revisión de navegación y cierre de sesión.

Las herramientas reportaron compilaciones exitosas. La prueba unitaria reportada en la revisión final fue de 1 aprobada y 0 fallidas.

Las operaciones administrativas y las restricciones de acceso deben comprobarse manualmente con las cuentas correspondientes.

## 14. Conclusiones

1. La inteligencia artificial permitió mejorar considerablemente el diseño visual de Pokédex Abad mediante componentes modernos de Jetpack Compose y Material Design 3.
2. La integración de Coil mejoró la experiencia de navegación al mostrar imágenes reales de los Pokémon.
3. El uso de Gemini permitió implementar mejoras progresivas en distintas pantallas, manteniendo la arquitectura MVVM.
4. Codex permitió realizar un rediseño más profundo de Perfil y Administración, incorporando componentes reutilizables y una presentación más completa de los datos.
5. La revisión manual fue importante para identificar diseños demasiado simples, solicitar correcciones y comprobar los resultados obtenidos con las herramientas de IA.