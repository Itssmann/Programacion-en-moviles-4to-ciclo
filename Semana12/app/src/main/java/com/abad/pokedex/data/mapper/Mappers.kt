package com.abad.pokedex.data.mapper

import com.abad.pokedex.data.local.PokemonEntity
import com.abad.pokedex.domain.model.Pokemon
import org.json.JSONArray
import org.json.JSONObject

fun PokemonEntity.toDomain(
    favoritoUsuario: Boolean = esFavorito
): Pokemon {
    val listaTipos = JSONArray(tipos)
    val listaHabilidades = JSONArray(habilidades)
    val objetoEstadisticas = JSONObject(estadisticas)

    return Pokemon(
        nombre = nombre,
        imagenUrl = imagenUrl,
        tipos = (0 until listaTipos.length()).map {
            listaTipos.getString(it)
        },
        esFavorito = favoritoUsuario,
        altura = altura,
        peso = peso,
        habilidades = (0 until listaHabilidades.length()).map {
            listaHabilidades.getString(it)
        },
        estadisticas = objetoEstadisticas.keys()
            .asSequence()
            .associateWith {
                objetoEstadisticas.getInt(it)
            }
    )
}

fun Pokemon.toEntity(): PokemonEntity {
    val estadisticasJson = JSONObject()

    estadisticas.forEach { (nombre, valor) ->
        estadisticasJson.put(nombre, valor)
    }

    return PokemonEntity(
        nombre = nombre,
        imagenUrl = imagenUrl,
        tipos = JSONArray(tipos).toString(),
        esFavorito = esFavorito,
        altura = altura,
        peso = peso,
        habilidades = JSONArray(habilidades).toString(),
        estadisticas = estadisticasJson.toString()
    )
}
