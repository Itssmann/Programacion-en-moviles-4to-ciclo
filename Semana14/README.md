## Observación del Paso 1: Congelamiento de pantalla

Al presionar el botón "Tarea larga (MAL)", la aplicación deja de responder durante aproximadamente 4 segundos.

El mensaje "Trabajando..." no llega a visualizarse porque Thread.sleep(4000) bloquea el hilo principal e impide que Android actualice la interfaz mientras se ejecuta la espera.

Si la espera fuera de 10 segundos, Android podría mostrar un aviso de que la aplicación no responde (ANR), debido al bloqueo prolongado del hilo principal.
