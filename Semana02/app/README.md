# Lab02 - Carrito de Compras en Kotlin

Nombre: Luis Abad

Descripción
Programa en Kotlin que simula un carrito de compras por consola.

Modelado de productos con data class
- Funciones para calcular subtotal, IGV (18%) y total.
- Reporte de detalle con columnas alineadas usando String.format
- Cálculo del producto más caro.
- Descuento automático según el monto total (5% sobre S/3000, 10% sobre S/5000).

¿Por qué nombre y precio son val pero cantidad es var?

Porque val es una variable solo de lectura y var es una variable la cual puede cambiar y como es una cantidad puede variar.

¿Qué pasaría si intentas cambiar el precio después de crear el producto?

Saldria error por1ue val no lo permitiria.
