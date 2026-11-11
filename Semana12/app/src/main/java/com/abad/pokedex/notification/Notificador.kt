package com.abad.pokedex.notification

fun interface Notificador {
    fun mostrar(titulo: String, texto: String)
}
