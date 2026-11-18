# Registro de Prompts y Documentación del Trabajo con IA (Fase 2 - CentroTareas)

Este documento registra de manera transparente la interacción con la IA (Gemini/Android Agent), detallando las decisiones técnicas, la arquitectura implementada y la justificación de las soluciones adoptadas en las tres etapas de la Fase 2 del proyecto **CentroTareas (Semana 14)**.

---

## ETAPA 1: Progreso de Primos en Tiempo Real mediante `Flow`

### 1. Objetivo
Evolucionar la función de cálculo de números primos desde una ejecución de bloque cerrado a un flujo reactivo asíncrono (*Cold Stream*) que reporte en vivo la cantidad de números inspeccionados, el porcentaje de avance y los primos encontrados a la interfaz de usuario, manteniendo la CPU en `Dispatchers.Default` y soportando cancelación estructurada.

### 2. Prompts e Instrucciones Utilizadas
> *"Implementa un Flow desde TareasRepositoryLocal que emita el progreso del cálculo de primos (procesados, límite, cantidadPrimos, porcentaje). Consúmelo desde ViewModel mediante viewModelScope y expón el estado en StateFlow. Evita actualizaciones excesivas e innecesarias en la UI."*

### 3. Archivos Creados o Modificados
- `data/TareasRepository.kt`: Se definió `PrimosProgress` y el método `fun contarPrimosFlow(hasta: Int): Flow<PrimosProgress>`.
- `data/TareasRepositoryLocal.kt`: Implementación del `flow { ... }.flowOn(Dispatchers.Default)` con control de frecuencia de emisión (*throttling*) y verificación de cancelación mediante `currentCoroutineContext().ensureActive()`.
- `model/Estados.kt`: Se actualizó `PrimosUi.Calculando` para albergar campos de progreso (`procesados`, `limite`, `cantidad`, `progreso`).
- `viewmodel/TareasViewModel.kt`: Método `contar(hasta)` actualizado para suscribirse al `Flow` y actualizar el `StateFlow` de UI.
- `ui/PantallaTareas.kt`: Componente UI con `LinearProgressIndicator` y etiquetas numéricas dinámicas.

### 4. Explicación Técnica
Se utilizó el builder `flow { ... }` para emitir instancias de `PrimosProgress`. Para evitar sobrecargar a Compose con miles de recomposiciones por segundo cuando $N$ es grande (ej. $N = 100000$), se aplicó un filtro de frecuencia que solo emite datos cuando el porcentaje entero del avance cambia (1%, 2%, etc.) o en el límite final. El operador `flowOn(Dispatchers.Default)` garantiza que la iteración matemática corra en el pool de hilos de cómputo de la CPU.

### 5. Decisiones de Arquitectura
- **Capa Data**: Genera y emite el `Flow`. Es agnóstica de la UI.
- **Capa ViewModel**: Transforma el `Flow` frío en un `StateFlow` caliente mediante `viewModelScope.launch`, gestionando excepciones de cancelación (`CancellationException`).
- **Capa UI**: Se suscribe mediante `collectAsState()` y renderiza de forma declarativa sin contener lógica de negocio.

### 6. Alternativas Evaluadas y Descartadas
- *Devolver LiveData desde el Repository*: Descartado porque Kotlin Coroutines y `Flow` son el estándar moderno y nativo de Kotlin, ofreciendo operadores más potentes como `flowOn`.
- *Emitir en cada iteración del bucle `for`*: Descartado por generar lag de UI debido a más de 100,000 recomposiciones en Compose.

### 7. Pruebas Realizadas y Resultados
- **Prueba de Conteo**: Se ejecutó $N = 50000$. La barra avanzó progresivamente del 0% al 100% mostrando los primos encontrados incrementándose en vivo.
- **Prueba de Cancelación**: Se canceló el conteo a mitad del proceso. La corrutina se detuvo inmediatamente.

---

## ETAPA 2: Resiliencia, Control de Tiempos, Reintentos y UI de Cancelación/Errores

### 1. Objetivo
Incorporar tolerancia a fallos en las cargas de datos simuladas mediante reintentos automáticos con espera progresiva (*Exponential Backoff*), control de tiempo máximo con `withTimeout`, notificaciones flotantes con `SnackbarHostState` y la conservación del progreso parcial alcanzado al cancelar el flujo de primos.

### 2. Prompts e Instrucciones Utilizadas
> *"Implementa withTimeout(7000) y hasta 3 reintentos automáticos con Exponential Backoff (1s y 2s) en la carga de datos. Muestra visualmente el intento actual. Si el usuario cancela la búsqueda de primos, conserva el porcentaje alcanzado y los primos encontrados sin reiniciar el contador. Integra notificaciones Snackbar de un solo uso."*

### 3. Archivos Creados o Modificados
- `model/Estados.kt`: `PrimosUi.Cancelado` pasó a ser una `data class` con métricas finales. `CargaUi.Cargando` incorporó `intentoActual`, `maxIntentos` y `mensajeEspera`.
- `viewmodel/TareasViewModel.kt`: Implementación del bucle de reintentos con `withTimeout(7000)`, captura explícita de `TimeoutCancellationException` y manejo de `MutableSharedFlow` para notificaciones.
- `ui/PantallaTareas.kt`: Rediseño de la tarjeta de cancelación de primos (tono ámbar, barra en posición alcanzada e indicadores numéricos) y tarjeta de error de conexión con reintento. Restauración del `SnackbarHost` en el slot de `Scaffold`.

### 4. Explicación Técnica
- **Exponential Backoff**: Ante una `IOException`, el bucle `for (intento in 1..3)` suspende con `delay(1000)` en el primer fallo y `delay(2000)` en el segundo antes de volver a intentar.
- **`withTimeout(7000)`**: Establece una barrera temporal de 7 segundos. Como la carga secuencial requiere 5s ($2s + 3s$), el margen de 7s permite su finalización normal con `CargaUi.Exito` sin lanzar falsos timeouts.
- **Cancelación Estructurada**: Si se lanza `CancellationException` por acción explícita del usuario, la excepción se re-lanza inmediatamente sin ejecutar reintentos.
- **Snackbar de 1 solo uso**: Se emite mediante `MutableSharedFlow<String>()` y se consume en `LaunchedEffect(Unit)` en la UI para evitar avisos duplicados por recomposición.

### 5. Decisiones de Arquitectura
- El ViewModel coordina el control de reintentos e informa el progreso del intento actual a través de `CargaUi.Cargando`.
- El Repository se mantiene enfocado exclusivamente en realizar las llamadas a la fuente de datos.

### 6. Alternativas Evaluadas y Descartadas
- *Reintentar ante `CancellationException`*: Descartado rotundamente por violar los principios de Concurrencia Estructurada de Kotlin Coroutines.
- *Usar `StateFlow` para eventos de Snackbar*: Descartado porque `StateFlow` retiene el último valor y provocaría la reaparición infinita del Snackbar al recomponer la pantalla.

### 7. Pruebas Realizadas y Resultados
- **Prueba de Cancelación de Primos**: Se canceló al 43%. La tarjeta mostró `"Cancelado al 43% — 430000 números procesados — 36000 primos encontrados"` y la barra se detuvo en el 43%.
- **Prueba de Reintentos**: Con "Simular error" activado, la app ejecutó los 3 intentos con sus correspondientes esperas visuales (1s y 2s) antes de presentar la tarjeta de error final.
- **Prueba de Carga Exitosa**: Con "Simular error" desactivado, "Una por una" completó en ~5000 ms y "A la vez" en ~3000 ms con éxito.

---

## ETAPA 3: Persistencia con DataStore, Comparación de Rendimiento y Documentación

### 1. Objetivo
Añadir persistencia local reactiva utilizando Jetpack DataStore Preferences para registrar los últimos 5 resultados obtenidos y los tiempos de ejecución, implementar una sección de análisis comparativo de rendimiento entre cargas secuenciales y paralelas con conclusiones dinámicas, y proveer filtrado e higienización del historial con confirmación en diálogo de alerta.

### 2. Prompts e Instrucciones Utilizadas
> *"Implementa persistencia con DataStore Preferences para almacenar los últimos 5 resultados (primos y cargas) y tiempos. Crea una sección 'Comparación de rendimiento' con barras horizontales y cálculo dinámico del % de ahorro de tiempo. Crea la sección 'Historial de actividad' con FilterChips ('Todos', 'Primos', 'Cargas') y opción de limpieza mediante AlertDialog. Genera PROMPTS.md."*

### 3. Archivos Creados o Modificados
- `app/build.gradle.kts`: Adición de la dependencia `androidx.datastore:datastore-preferences:1.1.1`.
- `model/HistorialItem.kt` *(Nuevo)*: Enums `TipoOperacion`, `FiltroHistorial` y `data class HistorialItem`.
- `data/UserPreferencesRepository.kt` *(Nuevo)*: Gestor de DataStore Preferences que almacena/recupera las claves `historial_serializado`, `tiempo_secuencial` y `tiempo_paralelo`.
- `viewmodel/TareasViewModel.kt`: Inyección de `UserPreferencesRepository`, exposición de `historialFiltrado`, `tiempoSecuencial`, `tiempoParalelo`, métodos para registrar resultados automáticamente y `limpiarHistorial()`.
- `viewmodel/TareasViewModelFactory.kt` & `MainActivity.kt`: Actualizados para inyectar `UserPreferencesRepository`.
- `ui/PantallaTareas.kt`: Adición de la tarjeta de **Comparación de rendimiento** (barras comparativas y porcentaje de mejora calculado en tiempo real) y la tarjeta de **Historial de actividad** (FilterChips, registros compactos, estado vacío y `AlertDialog` de confirmación para borrado).
- `PROMPTS.md` *(Nuevo)*: Documentación integral de las tres etapas de la Fase 2.

### 4. Explicación Técnica
- **DataStore Preferences**: Utiliza la extensión `by preferencesDataStore(name = "centrotareas_user_prefs")`. Los ítems se serializan en formato delimitado `id::tipo::titulo::detalle::milisegundos::fechaHora` y se parsean reactivamente a través de `Flow<List<HistorialItem>>`.
- **Límite de 5 registros**: En cada nueva inserción, se toma la lista actual, se antepone el nuevo registro y se aplica `.take(5)` antes de guardar en DataStore.
- **Cálculo de Eficiencia**: Cuando existen registros de ambos tiempos en DataStore ($T_{secuencial}$ y $T_{paralela}$), se calcula dinámicamente:
  $$\text{Porcentaje de Ahorro} = \left( \frac{T_{secuencial} - T_{paralela}}{T_{secuencial}} \right) \times 100$$
  mostrando mensajes conclusivos exactos como *"La ejecución paralela fue un 40% más rápida (2000 ms ahorrados)"*.

### 5. Decisiones de Arquitectura
- **Persistencia en la capa Data**: `UserPreferencesRepository` encapsula todo el acceso a `DataStore`. El ViewModel no manipula archivos ni claves directamente.
- **Reactividad limpia**: El ViewModel expone el historial mediante `stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())`. La UI observa los cambios mediante `collectAsState()`.

### 6. Alternativas Evaluadas y Descartadas
- *Base de datos Room*: Descartada por ser sobredimensionada para almacenar únicamente 5 registros y un par de métricas de tiempo. DataStore Preferences es más liviano y óptimo.
- *SharedPreferences*: Descartada por ser una API obsoleta que ejecuta I/O bloqueante en el hilo principal y carece de soporte nativo para `Flow`.

### 7. Pruebas Realizadas y Resultados
- **Prueba de Persistencia de Tareas, Primos y Cargas (Exitosas y Fallidas)**: Se ejecutaron tareas con progreso completadas y canceladas, cálculos de primos, cargas exitosas y cargas fallidas con "Simular error". Se verificó que tanto la finalización al 100% como la cancelación manual de la tarea con progreso se registren en el historial de DataStore guardando el porcentaje alcanzado, tiempo transcurrido y timestamp. Los 5 registros más recientes se recuperaron intactos al reiniciar la app.
- **Prueba de Filtrado**: Se seleccionaron los `FilterChips` *"Todos"*, *"Tareas"*, *"Primos"* y *"Cargas"*, comprobando que cada filtro aisla correctamente sus operaciones correspondientes.
- **Prueba de Borrado**: Se presionó "Limpiar", apareció el `AlertDialog` confirmando la acción, y al presionar "Borrar" el historial se vació mostrando el estado vacío de *"No hay registros guardados aún"*.

---

## ETAPA 4: Rediseño de experiencia con Material Design 3 (18/11/2026)

### Objetivo y auditoría previa
Modernizar CentroTareas con identidad verde petróleo y turquesa, jerarquía tipográfica clara y componentes reutilizables. Se trabajó en `con-ia`, exclusivamente dentro de Semana14, conservando los cambios locales anteriores y sin commit ni push.

Antes de editar se inspeccionaron PantallaTareas, Color, Theme, Type, MainActivity, los modelos y TareasViewModel. La pantalla ya utilizaba Scaffold, TopAppBar, Card, Surface, botones, OutlinedTextField, Switch, indicadores de progreso, SnackbarHost, FilterChip y AlertDialog. Se refactorizó esa presentación y se conservaron sus acciones; no se añadió una segunda implementación funcional.

Estados observados: `mensaje`, `progreso`, `corriendo`, `primos`, `carga`, `simularError`, `historial`, `historialFiltrado`, `filtroHistorial`, `tiempoSecuencial` y `tiempoParalelo`; los eventos de Snackbar continúan llegando desde `eventosUi`. Se revisaron las variantes de PrimosUi y CargaUi y los estados EXITOSO, CANCELADO y FALLIDO del historial, incluido el indicador legado `esError`.

### Archivos del rediseño
- `app/src/main/java/com/abad/centrotareas/ui/PantallaTareas.kt`: reorganización de las cinco secciones y representación de todos los estados existentes.
- `app/src/main/java/com/abad/centrotareas/ui/ComponentesTareas.kt` (nuevo): secciones, iconos vectoriales, etiquetas de estado, avisos, métricas, progreso animado y filas de acciones adaptables.
- `app/src/main/java/com/abad/centrotareas/ui/theme/Color.kt`: paleta e indicadores semánticos de éxito, cancelación y error, con variantes oscuras.
- `app/src/main/java/com/abad/centrotareas/ui/theme/Theme.kt`: esquemas claros/oscuros y formas Material 3.
- `app/src/main/java/com/abad/centrotareas/ui/theme/Type.kt`: escala tipográfica coherente.
- `PROMPTS.md`: se añade esta sección sin sustituir la documentación anterior.

### Componentes y decisiones visuales
- Scaffold y TopAppBar con título y subtítulo; LazyColumn con secciones identificadas por claves, separación de 20 dp y ancho máximo de lectura de 760 dp.
- ElevatedCard para secciones; Surface para métricas y mensajes; Button, FilledTonalButton y OutlinedButton para distinguir acciones; FilterChip para el historial; Switch, OutlinedTextField, LinearProgressIndicator, SnackbarHost y AlertDialog.
- Fondo neutro, tarjetas claras, verde petróleo para identidad y turquesa para avance. El tema oscuro utiliza superficies oscuras y colores de texto contrastantes.
- Estados con palabras e iconos además de color: verde completado, ámbar cancelado y rojo fallido. Los iconos vectoriales comparten trazo y no requieren dependencias adicionales.
- Progreso numérico tomado directamente del estado; solo la barra en ejecución interpola durante 180 ms. Al cancelar se muestra inmediatamente el valor conservado por el ViewModel.
- FlowRow permite que acciones y filtros pasen a otra línea. Las métricas largas y la fecha/duración se distribuyen verticalmente para reducir desbordamientos. Los iconos son decorativos junto a texto accesible; el Switch tiene una descripción propia.
- Se conserva el manejo de insets del Scaffold, se consumen los insets aplicados y se añade imePadding. El manifiesto ya incluía adjustResize.
- El gráfico conserva los tiempos reales y la escala mínima de 5000 ms de la versión anterior. Se elimina únicamente el mínimo visual de 5% de las barras: un tiempo pendiente se representa con barra vacía, sin aparentar una medición.
- Se mantiene la fórmula `((secuencial - paralelo).toFloat() / secuencial * 100).toInt()`. Se identifica cuál modalidad ganó y se expresa correctamente un ahorro negativo o un empate. Se protege la presentación frente a un denominador cero.
- El diálogo de limpieza explica el comportamiento ya existente: borrar todo el historial, incluidos otros filtros, y los tiempos de comparación.

### Conservación de MVVM y datos reales
La UI observa StateFlow con collectAsState y envía las mismas acciones al ViewModel. Repository, ViewModel, DataStore, modelos, tiempos, Flow, Dispatchers.Default, cancelaciones, timeout y límite de intentos no se modificaron. El reintento continúa llamando a cargarALaVez, igual que antes del rediseño. El contador conserva remember/mutableIntStateOf y su incremento original.

Se conservaron el máximo de cinco registros, la compatibilidad del historial serializado y la prioridad de `esError` para registros antiguos. Las duraciones se muestran desde los modelos; la tarea con progreso no expone duración en su estado inmediato, por lo que remite al historial. PrimosUi.Listo solo expone cantidad y duración: no se inventa una cifra de procesados para ese estado. Durante el cálculo y tras cancelar, los procesados, límite, primos encontrados y porcentaje proceden directamente del estado.

### Verificaciones realizadas
- `:app:assembleDebug --offline --console=plain`: **BUILD SUCCESSFUL**, usando el JBR de Android Studio y la caché local `C:\Users\denis\.gradle`.
- El primer intento con el entorno Java aislado falló al descargar Gradle por certificados TLS. Se utilizó la distribución ya instalada, sin cambiar certificados, dependencias ni configuración del proyecto.
- Se corrigieron los argumentos del constructor TextStyle detectados por el compilador y se recompiló correctamente.
- Comparación SHA-256 antes/después de todos los archivos en data/, model/ y viewmodel/: idénticos, incluidos los cambios locales preexistentes.
- Inspección de ramas de renderizado para estados iniciales, activos, completados, cancelados y fallidos; revisión de las llamadas existentes al ViewModel y de los tiempos/historial observados.
- No se ejecutaron pruebas visuales ni instrumentadas en emulador en esta intervención. La compilación no sustituye la validación visual o con TalkBack. No se afirma haber ejecutado de nuevo las pruebas funcionales descritas en etapas anteriores.

### Pruebas visuales recomendadas para la revisión
1. Abrir en tema claro y oscuro, teléfono estrecho y tableta; probar orientación horizontal, fuente grande y navegación por gestos/botones. Revisar especialmente el encabezado con fuente máxima.
2. Iniciar la tarea, tocar el contador durante la ejecución y comprobar que responde. Completar y luego repetir cancelando: debe conservarse el porcentaje, con estado ámbar e historial cancelado.
3. Calcular primos con 1000; comprobar resultado y duración. Probar un límite mayor y cancelar durante el cálculo: verificar procesados, primos encontrados y porcentaje parcial. Abrir/cerrar el teclado y comprobar desplazamiento y acceso a acciones.
4. Ejecutar ambas modalidades de carga sin error. Comparar milisegundos reales, barras, porcentaje de ahorro y modalidad ganadora.
5. Activar Simular error: revisar intentos y esperas, mensaje final legible, Snackbar y botón Reintentar. Desactivar la simulación y reintentar. Comprobar que el fallo no sustituyó las estadísticas exitosas.
6. Revisar Todos/Tareas/Primos/Cargas, colores y etiquetas de los tres estados; verificar fecha/hora/duración. Reiniciar la app para revisar persistencia y límite de cinco registros.
7. Abrir Limpiar y cancelar: no debe borrar datos. Confirmar el borrado únicamente cuando se desee comprobar también el vaciado de los tiempos; revisar el estado vacío y filtros sin resultados.
8. Con TalkBack, recorrer botones, campo, Switch y filtros; comprobar nombres, orden de lectura y alcance táctil.

### Explicación para la evaluación oral
«Rediseñé la capa de presentación con Material Design 3. El tema centraliza colores, tipografía y formas; los componentes reutilizables mantienen consistencia entre secciones. Compose observa los StateFlow del ViewModel y representa sus estados, mientras los botones delegan las acciones. El cálculo, las corrutinas, los reintentos y DataStore siguen en sus capas originales. Las barras y el historial usan mediciones reales: la única interpolación es visual y no modifica el progreso del trabajo.»

---

## COMPLEMENTO: Integración del historial y corrección de cancelación con Gemini

### Objetivo y procedencia de la solicitud
**Resumen de la solicitud, no transcripción literal:** incorporar «Tarea con progreso» al historial persistente, registrar tanto la finalización como la cancelación y distinguir los resultados exitosos, cancelados y fallidos. No se dispone aquí del texto exacto de los prompts adicionales enviados a Gemini; la atribución de la corrección a Gemini procede de lo indicado por el usuario en esta actualización documental.

Este complemento amplía la etapa 3 y documenta cambios funcionales anteriores al rediseño de Codex. Las etapas 1, 2 y 3 y el registro de la etapa 4 se conservan íntegros.

### Tarea con progreso en DataStore
Se incorporó `TAREA_PROGRESO` como tipo de operación y `TAREAS` como filtro del historial. Al finalizar la tarea se crea un `HistorialItem` con identificador, tipo, título, detalle, duración real en milisegundos y fecha/hora. La finalización al 100% se registra como exitosa; la cancelación registra el porcentaje parcial alcanzado y el tiempo transcurrido, con estado cancelado.

El guardado utiliza `UserPreferencesRepository.agregarHistorial()`, igual que las demás operaciones. Se mantiene el límite de cinco registros recientes y la recuperación mediante DataStore Preferences. La interfaz observa el historial del ViewModel; no escribe directamente en DataStore.

### Problema del Job cancelado y solución de Gemini
Al cancelar la tarea, su `Job` ya estaba cancelado cuando se entraba en el bloque que manejaba `CancellationException`. Intentar guardar el historial desde ese contexto podía cancelar también la operación suspendida de persistencia e impedir que quedara el registro de la tarea interrumpida.

Gemini corrigió el guardado mediante `withContext(NonCancellable)`. Este contexto se limita al guardado del registro de cancelación, permitiendo completar esa operación aunque el Job original esté cancelado. Después se vuelve a lanzar la excepción de cancelación para conservar la cancelación estructurada. El mismo patrón está presente en el guardado de la cancelación del cálculo de primos.

La corrección no hace que la tarea continúe ejecutándose ni desactiva globalmente la cancelación. Protege únicamente la persistencia del resultado parcial. Tampoco convierte la cancelación voluntaria en un error de carga o en un motivo para reintentar.

### Estados persistidos y representación visual

| Estado del modelo | Significado | Presentación en el historial |
| --- | --- | --- |
| `EXITOSO` | La operación terminó correctamente. | Etiqueta «Completado», verde e icono de confirmación. |
| `CANCELADO` | El usuario interrumpió la operación. | Etiqueta «Cancelado», ámbar e icono de cancelación; se conserva el progreso parcial disponible. |
| `FALLIDO` | La operación terminó con error. | Etiqueta «Fallido», rojo e icono de advertencia; detalle del fallo legible. |

`HistorialItem` incorpora `estadoRegistro`, manteniendo el indicador anterior `esError`. La persistencia amplía el formato de los registros con esos campos y conserva la lectura de registros antiguos: si faltan estados explícitos, se utiliza la información de error disponible para decidir entre fallido y exitoso. En la presentación, un registro marcado con `esError` sigue representándose como fallido.

Los cambios funcionales de esta integración afectaron al modelo de historial, a `UserPreferencesRepository`, a `TareasViewModel` y a la presentación del historial. Ya existían al comenzar el rediseño de Codex. En esta actualización se documentan, sin editar esos archivos.

---

## COMPLEMENTO DE LA ETAPA 4: Rediseño realizado con Codex

### Objetivo y solicitud a la IA
**Resumen de la solicitud original, no cita textual:** modernizar completamente la presentación de CentroTareas con identidad propia y Material Design 3, auditando primero la interfaz y los estados existentes. Mejorar las tres tarjetas, la comparación y el historial; conservar la lógica y los cambios locales; trabajar en `con-ia` dentro de Semana14; documentar las decisiones y compilar sin hacer commit ni push, dejando la revisión visual al usuario.

La etapa 4 anterior contiene el inventario detallado de archivos, componentes, decisiones y verificaciones de esta intervención. Este complemento identifica expresamente la participación de Codex y describe el alcance de cada sección.

### Identidad visual y componentes
El tema utiliza verde petróleo como identidad principal y turquesa para acciones y progreso, con superficies neutras y variantes clara y oscura. Los estados semánticos usan verde, ámbar y rojo, acompañados de texto e iconos para que su interpretación no dependa solo del color.

Se centralizaron colores, tipografía y formas en `ui/theme/`. Se emplearon `Scaffold`, `TopAppBar`, `ElevatedCard`, `Surface`, `OutlinedTextField`, `Button`, `OutlinedButton`, `FilledTonalButton`, `Switch`, `LinearProgressIndicator`, `SnackbarHost`, `FilterChip` y `AlertDialog`. La organización utiliza `LazyColumn` y `FlowRow`; los iconos vectoriales no requieren nuevas dependencias.

### Rediseño de las tres tarjetas principales

| Tarjeta | Mejora de presentación | Comportamiento conservado |
| --- | --- | --- |
| Tarea con progreso | Encabezado con icono, estado Lista/Ejecutando/Completada/Cancelada, porcentaje grande, barra animada, descripción y contador «Tócame» destacado. | Los mismos métodos de inicio y cancelación, tiempos y contador. La duración real se consulta en el historial porque no se expone en el estado inmediato de esta tarjeta. |
| Contar números primos | Campo numérico con indicación del límite, estado del cálculo, porcentaje y superficies para procesados y primos encontrados; finalización verde y cancelación ámbar. | Progreso procedente del Flow, cálculo en Dispatchers.Default y conservación de resultados parciales. El estado final muestra cantidad y duración disponibles, sin inventar procesados. |
| Dos cargas | Acciones «Una por una» y «A la vez» diferenciadas, Switch con texto de activación, indicador de intento, mensajes de espera, éxito/error y botón Reintentar. | Tiempos originales, límite de intentos, withTimeout, Snackbar y llamada de reintento existente. |

### Comparación e historial
La comparación muestra barras horizontales, etiquetas y milisegundos reales de las últimas cargas exitosas, porcentaje de ahorro, modalidad más rápida y diferencia de tiempo. Mantiene la fórmula original; un resultado pendiente se muestra sin avance ficticio. También contempla empate, ahorro negativo y el aviso de que una ejecución fallida no reemplaza las estadísticas exitosas.

El historial presenta filtros Todos, Tareas, Primos y Cargas, iconos por operación, etiquetas de los tres estados, detalle, fecha/hora y duración. Conserva los cinco registros y su compatibilidad. El estado vacío orienta al usuario; el diálogo de limpieza explica que el borrado afecta a todos los filtros y también a los tiempos de comparación, conforme al comportamiento previo del repositorio.

### Accesibilidad, tipografía y organización
Se unificaron tamaños y jerarquías tipográficas, espaciados y formas. Las acciones y filtros pueden saltar de línea mediante FlowRow; las métricas largas se organizan verticalmente. Se añadió descripción accesible al Switch, se acompañaron los iconos decorativos con texto y se contemplaron los insets del sistema y del teclado.

Estas son medidas implementadas en el código, no una certificación de accesibilidad. La comprobación con TalkBack, fuente máxima, distintos tamaños de pantalla y temas claro/oscuro continúa en la lista de revisión visual de la etapa 4.

### Archivos y conservación de la arquitectura
Codex modificó `ui/PantallaTareas.kt`, `ui/theme/Color.kt`, `ui/theme/Theme.kt` y `ui/theme/Type.kt`; añadió `ui/ComponentesTareas.kt` y documentó el trabajo en `PROMPTS.md`. No añadió dependencias ni cambió las capas funcionales durante el rediseño.

La separación MVVM se conserva: Repository realiza las operaciones y encapsula el acceso a datos; el ViewModel coordina las corrutinas y expone estados; Compose observa StateFlow y delega las acciones. El Flow de primos, Dispatchers.Default, la cancelación, los reintentos y DataStore permanecen en sus capas originales. La interpolación de la barra es exclusivamente visual.

La corrección funcional de Gemini y el rediseño de Codex son intervenciones distintas: la primera sí incorporó persistencia y manejo de cancelación en las capas correspondientes; la segunda conservó esos cambios. La comprobación de hashes registrada en la etapa 4 confirmó que data/, model/ y viewmodel/ no cambiaron durante el rediseño.

---

## EVIDENCIA DE PRUEBAS: Validación manual y compilación

### Pruebas funcionales manuales registradas anteriormente
Las etapas 1, 2 y 3 ya contienen los siguientes resultados. Se conservan como evidencia documental previa; Codex no los ejecutó nuevamente. El registro original no identifica expresamente al ejecutor ni al dispositivo/emulador de cada prueba, por lo que no se atribuyen aquí nuevas verificaciones personales al usuario ni se certifica que estos escenarios se hayan repetido después del rediseño.

| Escenario | Resultado registrado | Fuente y alcance |
| --- | --- | --- |
| Conteo de primos y cancelación | Avance en tiempo real y detención al cancelar. | Etapa 1, pruebas realizadas. |
| Cancelación con resultados parciales | Conservación de porcentaje y métricas parciales. | Etapa 2, prueba de cancelación. |
| Cargas con error simulado | Tres intentos y esperas antes del error final. | Etapa 2, prueba de reintentos. |
| Cargas exitosas | Comparación de ejecución secuencial y paralela con tiempos aproximados documentados. | Etapa 2, prueba de carga exitosa; no son mediciones nuevas. |
| Historial persistente | Registro de tareas completadas/canceladas, primos y cargas exitosas/fallidas; recuperación de cinco registros al reiniciar. | Etapa 3, prueba de persistencia. |
| Filtros y limpieza | Selección por tipo y borrado confirmado con estado vacío. | Etapa 3, pruebas de filtrado y borrado. |

**Verificación del usuario en el emulador tras el rediseño:** pendiente de confirmación explícita en la evidencia disponible. La solicitud original dejó esta revisión al usuario. Los casos recomendados de la etapa 4 son una guía de validación, no resultados ya obtenidos. No se añaden capturas, dispositivos, fechas de ejecución ni resultados que no estén documentados.

### Comprobaciones realizadas por Codex
- En la intervención de rediseño se ejecutó `:app:assembleDebug --offline --console=plain` y se obtuvo **BUILD SUCCESSFUL**, después de corregir los errores de compilación de tipografía.
- Se revisaron las ramas de representación de estados y las llamadas al ViewModel, y se compararon hashes de las capas protegidas antes/después.
- La compilación verifica que el código se construye y genera el APK; no demuestra por sí sola persistencia en ejecución, respuesta táctil, contraste visual ni comportamiento con teclado o lector de pantalla.
- En aquella intervención no se ejecutaron pruebas visuales ni instrumentadas en el emulador. En esta actualización exclusivamente documental tampoco se ejecutaron pruebas nuevas ni se volvió a compilar.

## GUION BREVE PARA LA EVALUACIÓN ORAL

«Con Gemini incorporé la tarea con progreso al historial y corregí el registro de cancelaciones: como el Job ya estaba cancelado, el guardado suspendido necesitaba un bloque acotado con NonCancellable; después se vuelve a propagar la cancelación. Los estados EXITOSO, CANCELADO y FALLIDO permiten distinguir los resultados sin confundir una cancelación voluntaria con un error.

Con Codex rediseñé la presentación usando Material Design 3 y un tema verde petróleo y turquesa. La interfaz observa los estados del ViewModel y reutiliza componentes para progreso, métricas e historial. Se mantuvieron Repository, Flow, corrutinas y DataStore, y la comparación utiliza tiempos reales. La compilación fue comprobada por Codex; las pruebas manuales previas están registradas por separado y la revisión visual posterior debe confirmarse en el emulador.»
