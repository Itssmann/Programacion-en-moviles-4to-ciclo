package com.abad.lab02carritokotlin

data class Vehiculo(
    val nombreCliente: String,
    val placa: String,
    val tipo: String,
    var horas: Int,
    val tarifaBase: Double,
    val clienteFrecuente: Boolean
)

fun main() {
    println("--- SISTEMA DE ESTACIONAMIENTO ---")

    print("Ingrese la cantidad de vehículos a registrar: ")
    var inputCantidad = readln()
    var cantidadTotal = inputCantidad.toIntOrNull() ?: 0

    while (cantidadTotal <= 0) {
        print("Por favor, ingrese un número mayor a 0: ")
        inputCantidad = readln()
        cantidadTotal = inputCantidad.toIntOrNull() ?: 0
    }

    val listaVehiculos = mutableListOf<Vehiculo>()

    for (i in 1..cantidadTotal) {
        println("\n--- REGISTRO VEHÍCULO #$i ---")

        print("Nombre del cliente: ")
        val nombre = readln()

        print("Placa: ")
        val placa = readln()

        var tipo = ""
        var tarifaBase = 0.0
        var tipoValido = false
        while (!tipoValido) {
            print("Tipo (Moto, Auto, Camioneta): ")
            tipo = readln().trim().lowercase()

            if (tipo == "moto") {
                tarifaBase = 2.0
                tipoValido = true
            } else if (tipo == "auto") {
                tarifaBase = 4.0
                tipoValido = true
            } else if (tipo == "camioneta") {
                tarifaBase = 10.0
                tipoValido = true
            } else {
                println("Error: Tipo no válido.")
            }
        }

        var horasIngresadas = 0
        while (horasIngresadas < 2) {
            print("Horas estacionado (Mínimo 2): ")
            val inputHoras = readln()
            horasIngresadas = inputHoras.toIntOrNull() ?: 0
            if (horasIngresadas < 2) {
                println("Error: No se permite menos de 2 horas.")
            }
        }

        print("¿Es cliente frecuente? (Sí/No): ")
        val frecuenteStr = readln().trim().lowercase()
        val esFrecuente = (frecuenteStr == "sí" || frecuenteStr == "si")

        val vehiculoActual = Vehiculo(nombre, placa, tipo, horasIngresadas, tarifaBase, esFrecuente)
        listaVehiculos.add(vehiculoActual)

        // Cálculo y formato de salida por vehículo
        println("\nTarifa basica \"${vehiculoActual.nombreCliente}\" - \"${vehiculoActual.placa}\"")
        println("hora     tarifa     recargo     importe")

        var subtotalVehiculo = 0.0

        for (h in 1..vehiculoActual.horas) {
            var porcentajeTexto = "0%"
            var recargoMonto = 0.0

            if (h == 3 || h == 4) {
                porcentajeTexto = "20%"
                recargoMonto = tarifaBase * 0.20
            } else if (h >= 5) {
                porcentajeTexto = "50%"
                recargoMonto = tarifaBase * 0.50
            }

            val importeHora = tarifaBase + recargoMonto
            subtotalVehiculo = subtotalVehiculo + importeHora

            println("%-8d %-10.2f %-11s %-10.2f".format(h, tarifaBase, porcentajeTexto, importeHora))
        }

        var totalConDescuento = subtotalVehiculo
        if (esFrecuente) {
            val descuento = subtotalVehiculo * 0.10
            totalConDescuento = subtotalVehiculo - descuento
            println("                    Subtotal:   %.2f".format(subtotalVehiculo))
            println("                    Desc. 10%%:  -%.2f".format(descuento))
        }

        println("                    total:      %.2f soles".format(totalConDescuento))
    }

    println("\nTotal de vehículos registrados: ${listaVehiculos.size}")
}