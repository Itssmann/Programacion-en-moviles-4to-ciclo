package com.abad.lab02carritokotlin

/**
 * PROYECTO: CARRITO DE COMPRAS - REFACTORIZADO CON POO
 * Pilares aplicados: Abstracción, Herencia, Encapsulamiento y Polimorfismo.
 */

// 1. ABSTRACCIÓN: Clase base que define el contrato para todos los productos.
abstract class Producto(
    val nombre: String,
    val precio: Double,
    cantidadInicial: Int
) {
    // ENCAPSULAMIENTO: Controlamos que la cantidad nunca sea menor a 1.
    var cantidad: Int = if (cantidadInicial >= 1) cantidadInicial else 1
        set(value) {
            if (value >= 1) field = value
        }

    // Método abstracto: cada subclase decidirá cómo calcular su importe.
    abstract fun calcularImporte(): Double
}

// 2. HERENCIA + POLIMORFISMO: Electrónico con recargo por garantía.
class Electronico(
    nombre: String,
    precio: Double,
    cantidad: Int
) : Producto(nombre, precio, cantidad) {
    override fun calcularImporte(): Double {
        val cargoGarantia = 0.05 // 5% cargo extra
        val subtotal = precio * cantidad
        return subtotal + (subtotal * cargoGarantia)
    }
}

// 2. HERENCIA + POLIMORFISMO: Accesorio con descuento por volumen.
class Accesorio(
    nombre: String,
    precio: Double,
    cantidad: Int
) : Producto(nombre, precio, cantidad) {
    override fun calcularImporte(): Double {
        val subtotal = precio * cantidad
        // 10% descuento si compra más de 2 unidades
        return if (cantidad > 2) subtotal * 0.90 else subtotal
    }
}

// 3. ENCAPSULAMIENTO: Carrito centraliza la gestión y los cálculos.
class Carrito(val cliente: String) {
    private val items = mutableListOf<Producto>()

    fun agregarProducto(producto: Producto) {
        items.add(producto)
        println("Producto agregado: ${producto.nombre}")
    }

    fun eliminarProducto(nombre: String): Boolean {
        val eliminado = items.removeIf { it.nombre.equals(nombre, ignoreCase = true) }
        if (eliminado) println("Eliminando producto: $nombre")
        return eliminado
    }

    fun buscarProducto(nombre: String): Producto? {
        return items.find { it.nombre.equals(nombre, ignoreCase = true) }
    }

    fun obtenerProductoMasCaro(): Producto? {
        return items.maxByOrNull { it.precio }
    }

    // Cálculos centralizados
    fun calcularSubtotal(): Double = items.sumOf { it.calcularImporte() }
    fun calcularIGV(): Double = calcularSubtotal() * 0.18
    fun calcularTotal(): Double = calcularSubtotal() + calcularIGV()

    fun calcularDescuento(): Double {
        val total = calcularTotal()
        return when {
            total > 5000 -> total * 0.10
            total > 3000 -> total * 0.05
            else -> 0.0
        }
    }

    fun calcularTotalFinal(): Double = calcularTotal() - calcularDescuento()

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

// 4. MAIN: Punto de entrada refactorizado.
fun main() {
    println("=========================================")
    println(" CARRITO DE COMPRAS - TIENDA TECSUP ")
    println("=========================================")

    val miCarrito = Carrito("Luis Abad")
    println("Cliente: ${miCarrito.cliente}")
    println()

    // Agregando productos (Polimorfismo: el carrito acepta cualquier tipo de Producto)
    miCarrito.agregarProducto(Electronico("Laptop HP", 2500.0, 1))
    miCarrito.agregarProducto(Accesorio("Mouse Logitech", 45.5, 2))
    miCarrito.agregarProducto(Accesorio("Teclado Redragon", 120.0, 3))
    miCarrito.agregarProducto(Electronico("Monitor msi", 650.0, 1))

    println()
    miCarrito.mostrarDetalle()
    println("Cantidad de productos: ${miCarrito.totalItems()}")
    println()

    // Mostrando resultados financieros
    println(String.format("Subtotal: S/ %.2f", miCarrito.calcularSubtotal()))
    println(String.format("IGV (18%%): S/ %.2f", miCarrito.calcularIGV()))
    println(String.format("TOTAL A PAGAR: S/ %.2f", miCarrito.calcularTotal()))

    // Producto más caro
    miCarrito.obtenerProductoMasCaro()?.let {
        println("Producto mas caro: ${it.nombre} " + String.format("(S/ %.2f)", it.precio))
    }

    // Descuentos
    val descuento = miCarrito.calcularDescuento()
    println(String.format("Descuento aplicado: S/ %.2f", descuento))
    println(String.format("TOTAL CON DESCUENTO: S/ %.2f", miCarrito.calcularTotalFinal()))

    // Búsqueda
    println()
    val buscado = "Laptop HP"
    miCarrito.buscarProducto(buscado)?.let {
        println("Producto encontrado: ${it.nombre} - S/ ${it.precio}")
    } ?: println("Producto $buscado no encontrado")

    // Modificación y actualización
    println()
    miCarrito.eliminarProducto("Mouse Logitech")

    println("\n--- Carrito Actualizado ---")
    miCarrito.mostrarDetalle()
    println(String.format("Nuevo TOTAL: S/ %.2f", miCarrito.calcularTotalFinal()))
}
