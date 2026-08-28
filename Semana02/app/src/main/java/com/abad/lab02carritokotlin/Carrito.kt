package com.abad.lab02carritokotlin

// 1. ABSTRACCIÓN: Clase base con contrato para cálculo de importe
abstract class Producto(
    val nombre: String,
    val precio: Double,
    cantidadInicial: Int
) {
    // ENCAPSULAMIENTO: cantidad con validación
    var cantidad: Int = if (cantidadInicial >= 1) cantidadInicial else 1
        set(value) {
            if (value >= 1) field = value
        }

    abstract fun calcularImporte(): Double
}

// 2. HERENCIA + POLIMORFISMO: Electrónico con cargo por garantía
class Electronico(
    nombre: String,
    precio: Double,
    cantidad: Int
) : Producto(nombre, precio, cantidad) {
    override fun calcularImporte(): Double {
        val cargoGarantia = 0.05
        val subtotal = precio * cantidad
        return subtotal + (subtotal * cargoGarantia)
    }
}

// 2. HERENCIA + POLIMORFISMO: Accesorio con descuento por volumen
class Accesorio(
    nombre: String,
    precio: Double,
    cantidad: Int
) : Producto(nombre, precio, cantidad) {
    override fun calcularImporte(): Double {
        val subtotal = precio * cantidad
        return if (cantidad > 2) subtotal * 0.90 else subtotal
    }
}

// 3. ENCAPSULAMIENTO: Carrito controla el acceso y centraliza la lógica de cálculo
class Carrito(val cliente: String) {
    private val items = mutableListOf<Producto>()

    fun agregarProducto(producto: Producto) {
        items.add(producto)
        println("Producto agregado: ${producto.nombre}")
    }

    fun eliminarProducto(nombre: String): Boolean {
        return items.removeIf { it.nombre.equals(nombre, ignoreCase = true) }
    }

    fun buscarProducto(nombre: String): Producto? {
        return items.find { it.nombre.equals(nombre, ignoreCase = true) }
    }

    fun obtenerProductoMasCaro(): Producto? {
        return items.maxByOrNull { it.precio }
    }

    fun listarProductos(): List<Producto> {
        return items.toList()
    }

    // LÓGICA CENTRALIZADA (Encapsulamiento de cálculos)
    fun calcularSubtotal(): Double {
        // POLIMORFISMO: cada producto calcula su propio importe
        return items.sumOf { it.calcularImporte() }
    }

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

// --- Funciones globales anteriores: se mantienen por ahora, se retiran en el Paso 4 ---

fun calcularSubtotal(productos: List<Producto>): Double {
    var subtotal = 0.0
    for (p in productos) {
        subtotal += p.precio * p.cantidad
    }
    return subtotal
}

fun calcularIGV(subtotal: Double): Double {
    val igv = subtotal * 0.18
    return igv
}

fun calcularTotal(subtotal: Double, igv: Double): Double {
    val total = subtotal + igv
    return total
}

fun mostrarDetalle(productos: List<Producto>) {
    println("--------- DETALLE DEL CARRITO ---------")
    var i = 1
    for (p in productos) {
        val importe = p.precio * p.cantidad
        println(String.format("%d. %-20s x%d S/ %8.2f",
            i, p.nombre, p.cantidad, importe))
        i++
    }
    println("---------------------------------------")
}

fun calcularDescuento(total: Double): Double {
    return when {
        total > 5000 -> total * 0.10
        total > 3000 -> total * 0.05
        else -> 0.0
    }
}

fun buscarProducto(productos: List<Producto>, nombre: String): Producto? {
    return productos.find { it.nombre == nombre }
}

fun main() {
    println("=========================================")
    println(" CARRITO DE COMPRAS - TIENDA TECSUP ")
    println("=========================================")

    val nombreCliente = "Luis Abad"
    val miCarrito = Carrito(nombreCliente)
    println("Cliente: $nombreCliente")
    println()

    miCarrito.agregarProducto(Electronico("Laptop HP", 2500.0, 1))
    miCarrito.agregarProducto(Accesorio("Mouse Logitech", 45.5, 2))
    miCarrito.agregarProducto(Accesorio("Teclado Redragon", 120.0, 3))
    miCarrito.agregarProducto(Electronico("Monitor msi", 650.0, 1))

    mostrarDetalle(miCarrito.listarProductos())
    println("Cantidad de productos: ${miCarrito.totalItems()}")
    println()

    val subtotal = calcularSubtotal(miCarrito.listarProductos())
    val igv = calcularIGV(subtotal)
    val total = calcularTotal(subtotal, igv)

    println(String.format("Subtotal: S/ %.2f", subtotal))
    println(String.format("IGV (18%%): S/ %.2f", igv))
    println(String.format("TOTAL A PAGAR: S/ %.2f", total))

    val masCaro = miCarrito.listarProductos().maxByOrNull { it.precio }
    if (masCaro != null) {
        println("Producto mas caro: ${masCaro.nombre} " +
                String.format("(S/ %.2f)", masCaro.precio))
    }

    val descuento = calcularDescuento(total)
    val totalConDescuento = total - descuento
    println(String.format("Descuento aplicado: S/ %.2f", descuento))
    println(String.format("TOTAL CON DESCUENTO: S/ %.2f", totalConDescuento))

    println()
    val productoBuscado = buscarProducto(miCarrito.listarProductos(), "Laptop HP")
    if (productoBuscado != null) {
        println("Producto encontrado: ${productoBuscado.nombre} - S/ ${productoBuscado.precio}")
    } else {
        println("Producto no encontrado")
    }

    println()
    println("Eliminando producto: Mouse Logitech")
    miCarrito.eliminarProducto("Mouse Logitech")

    val nuevoSubtotal = calcularSubtotal(miCarrito.listarProductos())
    val nuevoIgv = calcularIGV(nuevoSubtotal)
    val nuevoTotal = calcularTotal(nuevoSubtotal, nuevoIgv)

    mostrarDetalle(miCarrito.listarProductos())
    println(String.format("Nuevo Subtotal: S/ %.2f", nuevoSubtotal))
    println(String.format("Nuevo IGV: S/ %.2f", nuevoIgv))
    println(String.format("Nuevo TOTAL: S/ %.2f", nuevoTotal))
}