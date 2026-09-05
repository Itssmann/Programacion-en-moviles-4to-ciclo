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
