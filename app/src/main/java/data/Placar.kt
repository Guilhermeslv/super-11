package data

import java.io.Serializable

data class Placar(var nome_partida: String, var resultado: Array<Array<Int>>, var resultadoLongo: String, var has_timer: Boolean):Serializable
