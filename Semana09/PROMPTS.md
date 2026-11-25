# MuroCampus — Fase 2 (`con-ia`)

## Herramienta utilizada

En esta fase trabajé con **Claude (Claude Code)**. La guía sugiere Gemini; usé Claude porque fue el asistente con el que desarrollé el proyecto. Las reglas no cambian: la IA propone, yo reviso, pruebo en el emulador y corrijo lo que rompa MVVM + Repository.

## Mejoras realizadas

| Mejora | Capas que cambian | Commit |
|---|---|---|
| Rediseño Material 3 (tema, carga con esqueleto, deslizar para recargar, transiciones) | View + ViewModel | `style(lab09): rediseña MuroCampus con Material 3` |
| Búsqueda por título | ViewModel + View | `feat(lab09): agrega busqueda de publicaciones por titulo` |
| Borrar publicación con `@DELETE` y `Response<Unit>` | **API + Repository** + ViewModel + View | `feat(lab09): elimina publicaciones con DELETE` |
| Editar título con `@PATCH` | **API + Repository** + ViewModel + View | `feat(lab09): edita el titulo con PATCH` |
| Paleta esmeralda, encabezado con degradado, fila de autores y nombres | View | `docs(lab09): documenta prompts y mejoras realizadas con IA` |

## Prompts y decisiones

| Prompt que escribí | Qué respondió la IA | Qué cambié o descarté | Por qué |
|---|---|---|---|
| «Para la fase con IA quiero un diseño profesional con Material 3.» | Propuso `LargeTopAppBar` que se contrae, tarjetas esqueleto al cargar, `PullToRefreshBox`, avatares con color por usuario, transiciones con `AnimatedContent` y una paleta completa para claro y oscuro. | En modo oscuro las tarjetas salían **más oscuras** que el fondo y se veían hundidas; subí los tonos de `surfaceContainer*`. Para recargar sin borrar la lista agregamos `refrescar()` con un estado `refrescando` aparte. | En oscuro, lo que está "encima" debe ser más claro. Si recargar pusiera `Cargando`, la lista desaparecería mientras el usuario la mira. |
| (Revisión del ViewModel durante el rediseño) | Detectó que `cargaLista: Job?` estaba declarado **después** del bloque `init`. | Moví la declaración antes del `init`. | Kotlin inicializa en orden: `init` lanzaba la carga y luego `= null` borraba el `Job`, así que la primera carga no se podía cancelar. |
| «Agrega una búsqueda por título.» | Un `TextField` en la lista y `combine(_lista, _busqueda)` con `stateIn(WhileSubscribed(5_000))` para obtener `listaVisible`. Resaltado con `buildAnnotatedString`. | Mantuve el filtrado **local**, sin llamar a la API. | JSONPlaceholder no busca por título; filtrar lo ya descargado no gasta red y se combina con el filtro de autor. |
| «Agrega borrar con DELETE.» | `@DELETE("posts/{id}")` que devuelve `Response<Unit>`, diálogo de confirmación y Snackbar. | El Repository revisa `isSuccessful` y lanza `HttpException` a mano; la publicación se quita de la lista local. | Con `Response`, Retrofit no lanza error en 404/500: hay que revisarlo. Así cae en `Result.failure` y el ViewModel sigue sin conocer `HttpException`. JSONPlaceholder responde 200 pero no borra nada. |
| (Prueba del borrado en el emulador) | La primera versión limpiaba el mensaje **antes** de mostrarlo. | Ahora `showSnackbar()` va primero y `mensajeMostrado()` después. | Al borrar, el detalle se cierra: si el mensaje se limpiaba antes, se perdía y la lista nunca lo mostraba. |
| «Agrega editar el título con PATCH.» | `@PATCH("posts/{id}")` con `@Body CambioTitulo(title)`, una `ModalBottomSheet` y la respuesta en un Snackbar. | Uso el `Post` que **devuelve el servidor** para actualizar el detalle y la lista. Guardar se deshabilita si el título no cambió. | PATCH envía solo el campo que cambia (PUT enviaría todo). La respuesta del servidor es la fuente de verdad. |
| «Lo vio muy simple, cámbiale el color, que no sea azul.» | Paleta verde esmeralda con coral, encabezado con `Brush.linearGradient`, búsqueda dentro del encabezado, estadísticas de publicaciones y autores, y una fila de **autores en círculos** en lugar de los chips. | Corregí «1 autores» → «1 autor», el título «Comentarios» pegado al borde y un corte de color entre la barra del detalle y su cabecera (cambié a `verticalGradient`). | Son detalles que solo se ven en el emulador; el código compilaba igual. |
| «En lugar de Usuario 1, 2, 3 ponle nombres.» | Un archivo `ui/Autores.kt` con un nombre para cada `userId` y funciones `nombreDe`, `primerNombreDe` e `inicialesDe`. | Los nombres viven en la **capa de UI**; el modelo y la API siguen usando `userId`. Si llegara un id sin nombre, se muestra «Usuario N». | JSONPlaceholder solo da el número. Cambiar el modelo rompería la serialización; es un dato de presentación. |

## Verificación de las reglas de la guía

| Regla | Cómo lo comprobé | Resultado |
|---|---|---|
| Sin Retrofit, `PostApi`, `HttpException` ni `Context` en el ViewModel | Búsqueda en `viewmodel/` en cada commit | 0 coincidencias |
| La pantalla nunca llama a la API | Las pantallas solo usan `vm.*` | Cumple |
| Sin `catch` vacío ni `GlobalScope` | Revisión del código: todo pasa por `llamar { }` y `viewModelScope` | Cumple |
| URL con `https://` | `RetrofitClient` | Cumple |
| Al menos una mejora cambia el Repository o la API | DELETE y PATCH | Cumple |

## Pruebas en el emulador

- **Búsqueda:** «dolor» da 27 resultados en Todos y 4 con Diego Huamán, igual que la API.
- **DELETE:** `--> DELETE posts/1` y `<-- 200`; la lista baja a 99 y aparece «Publicación #1 eliminada (código 200)». En modo avión: «No se pudo eliminar: Sin conexión a internet».
- **PATCH:** el cuerpo enviado es solo `{"title":"Titulo editado con PATCH"}` y la respuesta es `200`; el título cambia en el detalle y en la lista.
- **Diseño:** probado en modo claro y oscuro, con carga, error sin conexión, deslizar para recargar y 0 cierres inesperados.

## Reflexión

La IA aceleró mucho el diseño y la parte repetitiva de Retrofit, pero varios errores solo aparecieron al probar: el `Job` que se borraba por el orden de inicialización, el mensaje que se perdía al cerrar el detalle y los colores del modo oscuro. Ninguno impedía compilar. Revisar cada cambio contra las reglas de MVVM y probarlo en el emulador fue lo que permitió encontrarlos.
