Lab04 - Mi Carrito TECSUP

Nombre: Luis Pablo Abad Anchiraico

Descripción
App de carrito de compras en Jetpack Compose que integra los labs
anteriores 2 y 3: modela productos con data class, captura datos con un
formulario, y presenta una lista dinámica con LazyColumn. Permite
agregar productos, eliminarlos, y calcula el subtotal,
IGV (18%) y total.

Capturas

Carrito vacío
![Carrito vacío](vacio.png)

Carrito con productos
![Carrito con productos](con_productos.png)

Preguntas 

¿Por qué la lista se declara con val y aún así podemos agregarle elementos?
val impide reasignar la variable a una lista distinta, pero no impide modificar el
contenido de la misma lista. Como mutableStateListOf crea una
lista mutable, sus métodos como add() y remove() cambian el
contenido interno sin necesitar reasignar la variable, por eso
val es suficiente.

¿Por qué mutableStateListOf y no una MutableList normal?
Una MutableList normal no está conectada al sistema de estado de
Compose: si le agregas o quitas elementos compose no se entera y
no redibuja la pantalla, por lo que los cambios no se verían
reflejados. MutableStateListOf es una lista observable, cada vez
que cambia su contenido compose detecta el cambio y actualiza el UI.

¿Qué hace weight(1f) en la LazyColumn?
Hace que la LazyColumn ocupe todo el espacio vertical disponible
que sobra dentro del column.