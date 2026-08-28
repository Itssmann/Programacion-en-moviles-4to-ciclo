package com.abad.lab02carritokotlin

// 1. ABSTRACCIÓN: Clase base con contrato para cálculo de importe
abstract class Producto(
    val nombre: String,
    val precio: Double,
    var cantidad: Int
) {
    abstract fun calcularImporte(): Double
}

// 2. HERENCIA + POLIMORFISMO: Electrónico con cargo por garantía
class Electronico(
    nombre: String,
    precio: Double,
    cantidad: Int
) : Producto(nombre, precio, cantidad) {

    override fun calcularImporte(): Double {
        val cargoGarantia = 0.05 // 5% de cargo adicional por garantía técnica
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
        // 10% de descuento si se compran más de 2 unidades del mismo accesorio
        return if (cantidad > 2) {
            subtotal * 0.90
        } else {
            subtotal
        }
    }
}

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
    val carrito = mutableListOf<Producto>()
    println("Cliente: $nombreCliente")
    println()

    carrito.add(Electronico("Laptop HP", 2500.0, 1))
    carrito.add(Accesorio("Mouse Logitech", 45.5, 2))
    carrito.add(Accesorio("Teclado Redragon", 120.0, 3))
    carrito.add(Electronico("Monitor msi", 650.0, 1))

    for (producto in carrito) {
        println("Producto agregado: ${producto.nombre}")
    }

    mostrarDetalle(carrito)
    println("Cantidad de productos: ${carrito.size}")
    println()

    val subtotal = calcularSubtotal(carrito)
    val igv = calcularIGV(subtotal)
    val total = calcularTotal(subtotal, igv)

    println(String.format("Subtotal: S/ %.2f", subtotal))
    println(String.format("IGV (18%%): S/ %.2f", igv))
    println(String.format("TOTAL A PAGAR: S/ %.2f", total))

    val masCaro = carrito.maxByOrNull { it.precio }
    if (masCaro != null) {
        println("Producto mas caro: ${masCaro.nombre} " +
                String.format("(S/ %.2f)", masCaro.precio))
    }

    val descuento = calcularDescuento(total)
    val totalConDescuento = total - descuento
    println(String.format("Descuento aplicado: S/ %.2f", descuento))
    println(String.format("TOTAL CON DESCUENTO: S/ %.2f", totalConDescuento))

    println()
    val productoBuscado = buscarProducto(carrito, "Laptop HP")
    if (productoBuscado != null) {
        println("Producto encontrado: ${productoBuscado.nombre} - S/ ${productoBuscado.precio}")
    } else {
        println("Producto no encontrado")
    }

    println()
    println("Eliminando producto: Mouse Logitech")
    carrito.removeIf { it.nombre == "Mouse Logitech" }

    val nuevoSubtotal = calcularSubtotal(carrito)
    val nuevoIgv = calcularIGV(nuevoSubtotal)
    val nuevoTotal = calcularTotal(nuevoSubtotal, nuevoIgv)

    mostrarDetalle(carrito)
    println(String.format("Nuevo Subtotal: S/ %.2f", nuevoSubtotal))
    println(String.format("Nuevo IGV: S/ %.2f", nuevoIgv))
    println(String.format("Nuevo TOTAL: S/ %.2f", nuevoTotal))
}