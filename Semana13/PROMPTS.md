# CampusGPS — Fase `con-ia`

## Herramientas utilizadas

- **Gemini** se utilizó para las mejoras funcionales iniciales, entre ellas los filtros, la persistencia de la selección y la adaptación del mapa al modo oscuro.
- **Codex** se utilizó para rediseñar la interfaz con Material 3, refinar la presentación del mapa e implementar y corregir la ficha informativa.

## Prompts de trabajo

Los textos que siguen son **resúmenes**. Los dos primeros se reconstruyeron a partir de los objetivos documentados y del código. Los demás se basan en las solicitudes de la conversación.

### Prompt 1 — Filtros por categoría y radio

Incorporar filtros por categoría y por radio de cercanía para explorar los lugares del campus. Mantener opciones para mostrar todas las categorías y para buscar sin límite de distancia.

### Prompt 2 — DataStore y modo oscuro de OpenFreeMap

Guardar la selección del lugar con DataStore para recuperarla al volver a abrir la aplicación. Usar el estilo oscuro de OpenFreeMap cuando el sistema esté en modo oscuro y el estilo claro en el modo claro.

### Prompt 3 — Rediseño con Jetpack Compose Material 3

Renovar la pantalla con una paleta índigo y turquesa, un encabezado con título y subtítulo, un contenedor redondeado para el mapa, acciones diferenciadas, filtros con `FilterChip` y tarjetas elevadas para los lugares. Adaptar la distribución al ancho disponible y reutilizar las dependencias existentes sin cambiar la lógica de la aplicación.

### Prompts de refinamiento — Paleta, mapa y distribución

- Recuperar el verde petróleo y el turquesa originales en ambos modos. Agrandar entre un 30 % y un 40 % los marcadores de los cinco lugares y distinguir el marcador de ubicación actual.
- Acercar los nombres a sus marcadores y reducir las superposiciones. Colocar las tarjetas de ubicación y lugar más cercano en paralelo cuando haya espacio y dar mayor visibilidad a los filtros.
- Quitar el título interno del mapa, mantener las esquinas redondeadas y usar una altura cercana a 280 dp en teléfonos. Hacer más discreta la etiqueta «Estás aquí» y presentar las categorías y radios en filas horizontales desplazables.

### Prompt 4 — Ficha informativa con `ModalBottomSheet`

Abrir una ficha al tocar una tarjeta de lugar. Mostrar nombre, categoría, distancia actual y una descripción breve para cada uno de los cinco lugares, evitando afirmaciones sobre horarios o servicios no confirmados. Ofrecer las acciones «Cerrar» y «Ver en el mapa» y reutilizar el enfoque de cámara existente.

### Prompt de corrección — Abrir la ficha sin mover el mapa

Mantener un estado temporal para la ficha, independiente de la selección persistida. Al abrirla o descartarla, conservar la selección y la cámara. Solo «Ver en el mapa» confirma la selección, cierra la ficha y enfoca el lugar mediante el flujo existente.

## Resultados y verificación

La aplicación incorpora filtros por categoría y radio, persistencia del lugar seleccionado y estilos de mapa según el modo del sistema. La interfaz final conserva la paleta verde petróleo y turquesa, y permite consultar la ficha de un lugar antes de decidir si se quiere enfocar en el mapa.

| Archivos principales | Responsabilidad |
| --- | --- |
| `PantallaMapa.kt`, `FichaLugar.kt` | Distribución, apertura y cierre de la ficha, y confirmación de la acción «Ver en el mapa». |
| `FiltroCategorias.kt`, `FiltroRadio.kt`, `ListaLugares.kt` | Filtros desplazables y tarjetas de lugares con distancia y estado de selección. |
| `MapaUbicacion.kt` | Estilos de OpenFreeMap, presentación de marcadores y enfoque de cámara. |
| `MapaViewModel.kt`, `PreferenciasRepositoryDataStore.kt` | Selección del lugar, solicitud de enfoque y persistencia del identificador. |
| `ui/theme/Color.kt`, `Theme.kt`, `Type.kt` | Paleta clara y oscura, formas y tipografía Material 3. |

La compilación de depuración se ejecutó con `.gradlew.bat assembleDebug` y terminó con `BUILD SUCCESSFUL` tras el rediseño, sus refinamientos y la corrección final. En el emulador se verificaron la apertura y el cierre de la ficha, la conservación de la selección al pulsar «Cerrar» y el cambio de selección al pulsar «Ver en el mapa». Durante la implementación de la ficha también se comprobó que esa acción volvía a centrar el mapa después de desplazarlo.

## Reflexión

La IA facilitó la exploración de alternativas de diseño y redujo el tiempo necesario para integrar componentes de Material 3. La primera versión de la ficha mostró una limitación concreta. Aunque compilaba, reutilizar la selección al abrirla producía un movimiento de cámara prematuro. La revisión del código y las pruebas de interacción permitieron detectar ese efecto y separar la consulta de la ficha de la acción de navegar.
