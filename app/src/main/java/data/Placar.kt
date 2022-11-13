package data

import java.io.Serializable

data class Placar(var nome_partida: String,var date: String, var has_timer: Boolean,var resultadoLongo:String ):Serializable{
    var resultados: ArrayList<List<Int>> = arrayListOf(arrayListOf<Int>(0,0))
}
