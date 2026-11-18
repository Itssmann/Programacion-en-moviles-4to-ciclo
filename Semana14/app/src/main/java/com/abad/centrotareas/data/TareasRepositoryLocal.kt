package com.abad.centrotareas.data

import kotlinx.coroutines.delay
import java.io.IOException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

class TareasRepositoryLocal : TareasRepository {

    override suspend fun cargarPerfil(): String {
        delay(2000)
        return "Ana Torres (ficticia)"
    }

    override suspend fun cargarPedidos(
        simularError: Boolean
    ): List<String> {
        delay(3000)

        if (simularError) {
            throw IOException("Sin conexión")
        }

        return listOf(
            "Pedido 101",
            "Pedido 102",
            "Pedido 103"
        )
    }

    override suspend fun contarPrimos(hasta: Int): Int =
        withContext(Dispatchers.Default) {

            var cantidad = 0

            for (n in 2..hasta) {
                currentCoroutineContext().ensureActive()

                if (esPrimo(n)) {
                    cantidad++
                }
            }
            cantidad
        }

    override fun contarPrimosFlow(hasta: Int): Flow<PrimosProgress> = flow {
        var cantidad = 0
        val total = (hasta - 1).coerceAtLeast(1)
        var ultimoPorcentajeEmitido = -1

        for (n in 2..hasta) {
            currentCoroutineContext().ensureActive()

            if (esPrimo(n)) {
                cantidad++
            }

            val procesados = n - 1
            val porcentajeInt = (procesados * 100) / total

            // Evita emisiones excesivas: emite solo cuando el porcentaje cambia o al final
            if (porcentajeInt != ultimoPorcentajeEmitido || n == hasta || n == 2) {
                ultimoPorcentajeEmitido = porcentajeInt
                emit(
                    PrimosProgress(
                        procesados = procesados,
                        limite = hasta,
                        cantidadPrimos = cantidad,
                        porcentaje = procesados.toFloat() / total
                    )
                )
            }
        }
    }.flowOn(Dispatchers.Default)

    private fun esPrimo(n: Int): Boolean {
        if (n < 2) return false

        var d = 2

        while (d.toLong() * d <= n.toLong()) {
            if (n % d == 0) return false
            d++
        }
        return true
    }
}

