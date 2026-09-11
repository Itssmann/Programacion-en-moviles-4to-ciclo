Lab03 - Registro de Producto con Jetpack Compose

Nombre: Abad Anchiraico Luis Pablo

Descripción:
App con interfaz gráfica hecha en Jetpack Compose que permite registrar
un producto (nombre, precio, cantidad). Al presionar agregar producto,
se muestra un card con el resumen y el importe calculado.

Capturas:

![Pantalla inicial](PantallaVacia.png)

![Pantalla final](PantallaConProductoRegistrado.png)

Pregunta:

¿Qué pasaría si declaras las variables de los campos SIN remember?

Al quitar remember y ejecutar la app no se podia escribir en los campos porque
sin este mutableStateOf crea una variable de estado nueva en cada recomposición.

![Pantalla sin remmember](PantallaSinRemember.png)

## Mejora con IA

| Prompt que usé | Qué generó Gemini | Qué acepté o corregí |
|---|---|---|
| "Estoy trabajando en PantallaRegistro, una pantalla de Jetpack Compose que registra un producto (nombre, precio, cantidad) y muestra una Card con el resumen al presionar AGREGAR PRODUCTO. Necesito agregar dos cosas, sin tocar la lógica de cálculo existente ni el diseño ya hecho: 1) Validación de campos vacíos: si al presionar AGREGAR PRODUCTO algún campo está vacío, mostrar un mensaje de error en rojo en vez de la Card. 2) Un botón LIMPIAR que vacíe los 3 campos y oculte la Card/mensaje de error. Dame el código completo de la función PantallaRegistro con estos cambios." | Agregó un estado `mensajeError`, validación con una lista de campos faltantes usando `isBlank()`, el mensaje de error en rojo, y un botón `OutlinedButton` para "LIMPIAR" que resetea todos los estados. | Acepté toda la lógica de validación tal cual, funcionaba correctamente. Corregí que Gemini escribió `androidx.compose.material3.OutlinedButton` directo en el código en vez de un import limpio arriba del archivo — lo cambié por un import normal para que quedara más ordenado y consistente con el resto del código. |
