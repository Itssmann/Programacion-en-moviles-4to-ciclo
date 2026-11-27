# PROMPTS.md · AvisosCampus · Fase 2 (con IA)

Rama de trabajo: `con-ia` · Asistente de IA usado: Claude

Mejoras elegidas (mínimo 3, al menos una cambia el Repository o el ViewModel):

| # | Mejora | Capas que cambian |
| :---: | :--- | :--- |
| 1 | Dos canales: **Urgentes** (`IMPORTANCE_HIGH`) y **Avisos**; el recordatorio usa el urgente | Model + Repository + ViewModel + Notificador |
| 2 | Texto largo con `BigTextStyle` al expandir la notificación | Notificador |
| 3 | Receptor del cargador (`ACTION_POWER_CONNECTED` / `ACTION_POWER_DISCONNECTED`) con su propio switch | Receiver + ViewModel + View |
| 4 | Rediseño visual con Material 3 (tema propio, componentes reutilizables, historial en línea de tiempo) | View (ui/ y theme/) |

## Tabla de prompts

| Prompt que escribí | Qué respondió la IA | Qué cambié o descarté | Por qué |
| :--- | :--- | :--- | :--- |
| «Agrega a AvisosCampus un segundo canal "Urgentes" con IMPORTANCE_HIGH y haz que el recordatorio de 10 s use ese canal, sin romper MVVM.» | Propuso crear los dos canales en `crearCanal` con `createNotificationChannels` y elegir el canal dentro de `Notificador.mostrar` según el origen del aviso (`if (aviso.origen == Origen.RECORDATORIO)`). | Cambié la decisión: agregué `urgente: Boolean = false` a `Aviso` y a `registrar(...)` del Repository, y es el **ViewModel** quien pasa `urgente = true` en `recordarEn`. El Notificador solo lee `aviso.urgente`. | Qué es urgente es una regla de la app, no del sistema: le toca al ViewModel. Si el Notificador decide por el origen, la regla queda escondida en la capa que solo debe mostrar. Así el ViewModel sigue sin `Context` y la regla se puede probar sin Android. |
| «No sale Urgentes» (en Ajustes solo aparecía el canal Avisos) | Explicó que el código del canal estaba bien, pero que Android Studio había aplicado los cambios sin reiniciar la app, y `AvisosApp.onCreate()` (donde se crean los canales) solo se ejecuta cuando el proceso arranca desde cero. | Cerré la app y la volví a abrir con Run. Apareció el canal Urgentes y el recordatorio salió como notificación emergente. No cambié el código. | Los canales se crean una sola vez al iniciar la app. Además, la importancia de un canal ya creado no se puede cambiar, por eso Urgentes es un canal nuevo con otro id y no una modificación de Avisos. |
| «Haz que la notificación muestre el texto completo al expandirla usando BigTextStyle.» | Sugirió `.setStyle(NotificationCompat.BigTextStyle().bigText(aviso.texto))`. | Lo adapté para que el texto expandido agregue también el origen y la hora del aviso (`Origen: … · HH:mm:ss`). Mantuve `setContentText` para la vista contraída. | Con los textos del lab (cortos) `bigText` igual a `contentText` no se notaba ninguna diferencia; con origen y hora la mejora se ve y es útil. |
| «Agrega un BroadcastReceiver para el cargador con ACTION_POWER_CONNECTED y ACTION_POWER_DISCONNECTED y un switch propio.» | Primero propuso declarar el receptor en el `AndroidManifest.xml` con un `<intent-filter>`. | **Lo descarté.** Hice `CargadorReceiver(alCambiar)` registrado en un `DisposableEffect(escuchandoCargador)` con `ContextCompat.RECEIVER_NOT_EXPORTED` y quitado en `onDispose`, igual que el del modo avión. | Desde Android 8 los anuncios implícitos como `POWER_CONNECTED` no llegan a receptores del Manifest. Además, uno del Manifest no se puede conectar con el ViewModel ni apagar con el switch. |
| «Haz una mejora visual, métele Material 3 a la app para que se vea profesional» | Propuso un `ColorScheme` propio, un componente de tarjeta reutilizable, `CircularProgressIndicator` con `animateFloatAsState` para la cuenta y un historial con ícono y color por origen. Para los íconos sugirió agregar la librería `material-icons-extended`. | Descarté la librería de íconos y usé vectores propios en `res/drawable` (alarma, avión, rayo, escudo, historial). Desactivé los colores dinámicos y fijé `surfaceContainer*` en blanco, porque las tarjetas salían grises o lilas. Moví los componentes comunes a `Componentes.kt`. | La librería extendida pesa varios MB y solo necesitaba 7 íconos. Con colores dinámicos la app cambia de color según el fondo de pantalla del celular y pierde su identidad. El ViewModel no se tocó: el rediseño solo cambia la capa View. |
| «Revisa que ningún archivo de viewmodel/ use Context, NotificationManager o BroadcastReceiver y que todos los receptores se quiten.» | Confirmó que `AvisosViewModel` solo usa el Repository, `StateFlow`, `SharedFlow` y corrutinas, y que los dos `DisposableEffect` tienen `unregisterReceiver` en `onDispose`. | Sin cambios. Revisé que el `PendingIntent` siga con `FLAG_IMMUTABLE`. | Son las reglas de oro del lab: con eso no se filtra memoria y el ViewModel se puede probar sin Android. |

## Cómo probar las mejoras
1. **Dos canales:** desinstala la versión anterior (o borra los datos), abre la app y ve a Ajustes → Apps → AvisosCampus → Notificaciones: aparecen *Avisos* y *Urgentes*. Pulsa *Recordar en 10 s*: el recordatorio aparece como emergente en la parte superior.
2. **Texto largo:** baja el panel y expande una notificación: se ve el texto con el origen y la hora.
3. **Cargador:** activa *Escuchar el cargador*. En el emulador, ve a ⋯ (Extended controls) → Battery y cambia *Charger connection* entre *AC charger* y *None*: aparecen los avisos *Cargador · Conectado* y *Cargador · Desconectado* en la notificación y en el historial.
4. **Rediseño:** abre la app. Debe verse el encabezado azul con "Tu campus, siempre conectado", las tarjetas blancas con ícono y, al pulsar *Recordar en 10 s*, el anillo ámbar que se vacía con los segundos.
