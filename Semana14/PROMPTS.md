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
- **Prueba de Persistencia**: Se ejecutaron cálculos de primos y cargas. Se cerró la app completamente desde la multitarea y se volvió a abrir. El historial de 5 registros y las barras de rendimiento se recuperaron intactos desde DataStore.
- **Prueba de Filtrado**: Se seleccionó "Primos" y luego "Cargas" en los `FilterChips`, comprobando que la lista responde al instante mostrando solo los elementos correspondientes.
- **Prueba de Borrado**: Se presionó "Limpiar", apareció el `AlertDialog` confirmando la acción, y al presionar "Borrar" el historial se vació mostrando el estado vacío de *"No hay registros guardados aún"*.
