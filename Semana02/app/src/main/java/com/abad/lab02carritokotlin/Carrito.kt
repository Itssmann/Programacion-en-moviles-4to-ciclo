package com.abad.lab02carritokotlin

/**
 * PROYECTO: CARRITO DE COMPRAS - REFACTORIZADO CON POO
 *
 * Esta clase demuestra los 4 pilares fundamentales de la Programación Orientada a Objetos:
 * 1. ABSTRACCIÓN: Clase base Producto que no puede ser instanciada.
 * 2. HERENCIA: Subclases que extienden Producto.
 * 3. ENCAPSULAMIENTO: Atributos privados y validación de datos.
 * 4. POLIMORFISMO: Cálculos de importe dinámicos según el tipo de objeto.
 */

/**
 * Representa la ABSTRACCIÓN de un artículo de venta.
 * Define las propiedades comunes y obliga a las subclases a implementar su lógica de costo.
 */
abstract class Producto(
    val nombre: String,
    val precio: Double,
    cantidadInicial: Int
) {
    /**
     * Ejemplo de ENCAPSULAMIENTO:
     * El atributo 'cantidad' no permite valores menores a 1 mediante un setter personalizado.
     * Esto protege la integridad de los datos del objeto.
     */
    var cantidad: Int = if (cantidadInicial >= 1) cantidadInicial else 1
        set(value) {
            if (value >= 1) field = value
        }

    /**
     * Ejemplo de POLIMORFISMO:
     * Método abstracto cuya implementación varía según la subclase (contrato).
     */
    abstract fun calcularImporte(): Double
}

/**
 * Representa la HERENCIA:
 * Electronico 'es un' Producto que hereda sus atributos y comportamiento base.
 */
class Electronico(
    nombre: String,
    precio: Double,
    cantidad: Int
) : Producto(nombre, precio, cantidad) {
    /**
     * Aplicación de POLIMORFISMO:
     * Implementa su propia lógica añadiendo un cargo por garantía.
     */
    override fun calcularImporte(): Double {
        val cargoGarantia = 0.05 // 5% cargo extra
        val subtotal = precio * cantidad
        return subtotal + (subtotal * cargoGarantia)
    }
}

/**
 * Representa la HERENCIA:
 * Accesorio 'es un' Producto con características propias.
 */
class Accesorio(
    nombre: String,
    precio: Double,
    cantidad: Int
) : Producto(nombre, precio, cantidad) {
    /**
     * Aplicación de POLIMORFISMO:
     * Implementa su propia lógica aplicando un descuento por volumen.
     */
    override fun calcularImporte(): Double {
        val subtotal = precio * cantidad
        // 10% descuento si compra más de 2 unidades
        return if (cantidad > 2) subtotal * 0.90 else subtotal
    }
}

/**
 * Ejemplo de ENCAPSULAMIENTO:
 * La clase Carrito gestiona una lista PRIVADA de productos.
 * Centraliza toda la lógica de negocio, ocultando la complejidad al usuario de la clase.
 */
class Carrito(val cliente: String) {
    // La lista está oculta (private) para que no sea modificada externamente sin control.
    private val items = mutableListOf<Producto>()

    /**
     * Agrega un objeto a la lista interna.
     */
    fun agregarProducto(producto: Producto) {
        items.add(producto)
        println("Producto agregado: ${producto.nombre}")
    }

    /**
     * Elimina un producto por su nombre.
     */
    fun eliminarProducto(nombre: String): Boolean {
        val eliminado = items.removeIf { it.nombre.equals(nombre, ignoreCase = true) }
        if (eliminado) println("Eliminando producto: $nombre")
        return eliminado
    }

    /**
     * Busca un producto en la colección privada.
     */
    fun buscarProducto(nombre: String): Producto? {
        return items.find { it.nombre.equals(nombre, ignoreCase = true) }
    }

    /**
     * Encuentra el producto con el precio base más alto.
     */
    fun obtenerProductoMasCaro(): Producto? {
        return items.maxByOrNull { it.precio }
    }

    /**
     * Ejemplo clave de POLIMORFISMO:
     * Recorre la lista de tipo 'Producto' y llama a 'calcularImporte()'.
     * No necesita saber si el producto es Electrónico o Accesorio para calcular el total correcto.
     */
    fun calcularSubtotal(): Double = items.sumOf { it.calcularImporte() }

    fun calcularIGV(): Double = calcularSubtotal() * 0.18
    fun calcularTotal(): Double = calcularSubtotal() + calcularIGV()

    /**
     * Encapsulamiento de lógica de negocio: el descuento se calcula internamente.
     */
    fun calcularDescuento(): Double {
        val total = calcularTotal()
        return when {
            total > 5000 -> total * 0.10
            total > 3000 -> total * 0.05
            else -> 0.0
        }
    }

    fun calcularTotalFinal(): Double = calcularTotal() - calcularDescuento()

    /**
     * Presentación de datos encapsulada.
     */
    fun mostrarDetalle() {
        println("--------- DETALLE DEL CARRITO ---------")
        items.forEachIndexed { index, p ->
            println(String.format("%d. %-20s x%d S/ %8.2f",
                index + 1, p.nombre, p.cantidad, p.calcularImporte()))
        }
        println("---------------------------------------")
    }

    fun totalItems(): Int = items.size
}

/**
 * Punto de entrada principal: Orquestador del flujo del programa.
 */
fun main() {
    println("=========================================")
    println(" CARRITO DE COMPRAS - TIENDA TECSUP ")
    println("=========================================")

    val miCarrito = Carrito("Luis Abad")
    println("Cliente: ${miCarrito.cliente}")
    println()

    // Agregando productos (Uso de Polimorfismo: Carrito acepta cualquier Producto)
    miCarrito.agregarProducto(Electronico("Laptop HP", 2500.0, 1))
    miCarrito.agregarProducto(Accesorio("Mouse Logitech", 45.5, 2))
    miCarrito.agregarProducto(Accesorio("Teclado Redragon", 120.0, 3))
    miCarrito.agregarProducto(Electronico("Monitor msi", 650.0, 1))

    println()
    miCarrito.mostrarDetalle()
    println("Cantidad de productos: ${miCarrito.totalItems()}")
    println()

    // Mostrando resultados financieros a través de los métodos del Carrito
    println(String.format("Subtotal: S/ %.2f", miCarrito.calcularSubtotal()))
    println(String.format("IGV (18%%): S/ %.2f", miCarrito.calcularIGV()))
    println(String.format("TOTAL A PAGAR: S/ %.2f", miCarrito.calcularTotal()))

    // Producto más caro
    miCarrito.obtenerProductoMasCaro()?.let {
        println("Producto mas caro: ${it.nombre} " + String.format("(S/ %.2f)", it.precio))
    }

    // Cálculos de descuento encapsulados
    val descuento = miCarrito.calcularDescuento()
    println(String.format("Descuento aplicado: S/ %.2f", descuento))
    println(String.format("TOTAL CON DESCUENTO: S/ %.2f", miCarrito.calcularTotalFinal()))

    // Búsqueda de productos
    println()
    val buscado = "Laptop HP"
    miCarrito.buscarProducto(buscado)?.let {
        println("Producto encontrado: ${it.nombre} - S/ ${it.precio}")
    } ?: println("Producto $buscado no encontrado")

    // Modificación del carrito
    println()
    miCarrito.eliminarProducto("Mouse Logitech")

    println("\n--- Carrito Actualizado ---")
    miCarrito.mostrarDetalle()
    println(String.format("Nuevo TOTAL: S/ %.2f", miCarrito.calcularTotalFinal()))
}
