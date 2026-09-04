package com.abad.lab02carritokotlin

class Vehiculo(
    val nombreCliente: String,
    val placa: String,
    val tipo: String,
    var horas: Int,
    val tarifaBase: Double,
    val clienteFrecuente: Boolean
)

fun main() {
    println("--- SISTEMA DE ESTACIONAMIENTO ---")

    print("Ingrese el aforo máximo del estacionamiento: ")
    var inputAforo = readln()
    var aforoMaximo = inputAforo.toIntOrNull() ?: 0

    while (aforoMaximo <= 0) {
        print("Por favor, ingrese un aforo mayor a 0: ")
        inputAforo = readln()
        aforoMaximo = inputAforo.toIntOrNull() ?: 0
    }

    val listaVehiculos = mutableListOf<Vehiculo>()
    var contadorMotos = 0
    var contadorAutos = 0
    var contadorCamionetas = 0
    var contadorTrailers = 0
    var acumuladoHoras = 0
    var gananciaTotalDia = 0.0

    var continuar = "si"

    while (listaVehiculos.size < aforoMaximo && continuar == "si") {
        val contadorVehiculoActual = listaVehiculos.size + 1
        println("\n--- REGISTRO VEHÍCULO #$contadorVehiculoActual (Aforo actual: ${listaVehiculos.size}/$aforoMaximo) ---")

        print("Nombre del cliente: ")
        val nombre = readln()

        print("Placa: ")
        val placa = readln()

        var tipo = ""
        var tarifaBase = 0.0
        var tipoValido = false
        while (!tipoValido) {
            print("Tipo (Moto, Auto, Camioneta, Trailer): ")
            tipo = readln().trim().lowercase()

            if (tipo == "moto") {
                tarifaBase = 2.0
                tipoValido = true
                contadorMotos = contadorMotos + 1
            } else if (tipo == "auto") {
                tarifaBase = 4.0
                tipoValido = true
                contadorAutos = contadorAutos + 1
            } else if (tipo == "camioneta") {
                tarifaBase = 10.0
                tipoValido = true
                contadorCamionetas = contadorCamionetas + 1
            } else if (tipo == "trailer") {
                tarifaBase = 20.0
                tipoValido = true
                contadorTrailers = contadorTrailers + 1
            } else {
                println("Error: Tipo no válido.")
            }
        }

        var horasIngresadas = 0
        while (horasIngresadas < 2 || horasIngresadas > 24) {
            print("Horas estacionado (Entre 2 y 24): ")
            val inputHoras = readln()
            horasIngresadas = inputHoras.toIntOrNull() ?: 0
            if (horasIngresadas < 2 || horasIngresadas > 24) {
                println("Error: El tiempo permitido debe estar entre 2 y 24 horas.")
            }
        }

        print("¿Es cliente frecuente? (Sí/No): ")
        val frecuenteStr = readln().trim().lowercase()
        val esFrecuente = (frecuenteStr == "sí" || frecuenteStr == "si")

        val vehiculoActual = Vehiculo(nombre, placa, tipo, horasIngresadas, tarifaBase, esFrecuente)
        listaVehiculos.add(vehiculoActual)

        println("\nTarifa básica \"${vehiculoActual.nombreCliente}\" - \"${vehiculoActual.placa}\"")
        println("hora     tarifa     recargo     importe")

        var subtotalVehiculo = 0.0

        for (h in 1..vehiculoActual.horas) {
            var porcentajeTexto = "0%"
            var recargoMonto = 0.0

            if (h in 3..5) {
                porcentajeTexto = "20%"
                recargoMonto = tarifaBase * 0.20
            } else if (h in 6..10) {
                porcentajeTexto = "40%"
                recargoMonto = tarifaBase * 0.40
            } else if (h >= 11) {
                porcentajeTexto = "50%"
                recargoMonto = tarifaBase * 0.50
            }

            val importeHora = tarifaBase + recargoMonto
            subtotalVehiculo = subtotalVehiculo + importeHora

            println("%-8d %-10.2f %-11s %-10.2f".format(h, tarifaBase, porcentajeTexto, importeHora))
        }

        println("                    Subtotal:   %.2f".format(subtotalVehiculo))

        var montoConDescuentos = subtotalVehiculo

        if (esFrecuente) {
            val descuentoFrecuente = subtotalVehiculo * 0.10
            montoConDescuentos = montoConDescuentos - descuentoFrecuente
            println("                    Desc. 10%% (Frecuente): -%.2f".format(descuentoFrecuente))
        }

        if (subtotalVehiculo > 500) {
            val descuentoMonto = subtotalVehiculo * 0.20
            montoConDescuentos = montoConDescuentos - descuentoMonto
            println("                    Desc. 20%% (>500):       -%.2f".format(descuentoMonto))
        }

        val igv = montoConDescuentos * 0.18
        val totalFinal = montoConDescuentos + igv

        println("                    IGV (18%%):               +%.2f".format(igv))
        println("                    Total:                   %.2f soles".format(totalFinal))

        acumuladoHoras = acumuladoHoras + horasIngresadas
        gananciaTotalDia = gananciaTotalDia + totalFinal

        if (listaVehiculos.size == aforoMaximo) {
            println("\n¡El estacionamiento ha alcanzado su aforo máximo!")
            continuar = "no"
        } else {
            print("\n¿Desea registrar otro vehículo? (Sí/No): ")
            val respuesta = readln().trim().lowercase()

            if (respuesta == "no" || respuesta == "n") {
                var menuPausa = true
                while (menuPausa) {
                    println("\n--- REGISTRO PAUSADO ---")
                    println("1. Seguir registrando (Siguiente vehículo)")
                    println("2. Ver resumen del día y finalizar")
                    print("Seleccione una opción (1 o 2): ")
                    val opcionPausa = readln().trim()

                    if (opcionPausa == "1") {
                        continuar = "si"
                        menuPausa = false
                    } else if (opcionPausa == "2") {
                        continuar = "no"
                        menuPausa = false
                    } else {
                        println("Error: Opción inválida. Elija 1 o 2.")
                    }
                }
            } else {
                continuar = "si"
            }
        }
    }

    println("\n==========================================")
    println("RESUMEN DEL DÍA")
    println("==========================================")
    println("Aforo total del local: $aforoMaximo")
    println("Cantidad total de vehículos registrados: ${listaVehiculos.size}")
    println(" - Motos: $contadorMotos")
    println(" - Autos: $contadorAutos")
    println(" - Camionetas: $contadorCamionetas")
    println(" - Trailers: $contadorTrailers")
    println("Cantidad total de horas acumuladas: $acumuladoHoras")
    println("Ganancia total del día: S/. %.2f".format(gananciaTotalDia))
    println("==========================================")
}