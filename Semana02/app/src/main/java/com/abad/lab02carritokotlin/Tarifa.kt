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
        println("Vehículo registrado: ${vehiculoActual.nombreCliente} - ${vehiculoActual.placa}")
    }

    println("\nTotal de vehículos registrados: ${listaVehiculos.size}")
}