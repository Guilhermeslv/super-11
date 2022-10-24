package ufc.smd.esqueleto_placar

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator

import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.core.content.getSystemService
import data.Placar
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.nio.charset.StandardCharsets

class PlacarActivity : AppCompatActivity() {

    lateinit var placar: Placar

    var game = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_placar)

        //Recupera da view anterior os dados do jogo cadastrado
        placar = getIntent().getExtras()?.getSerializable("placar") as Placar


        var tvGolsTime1 = findViewById<TextView>(R.id.GolsTime1)
        var tvGolsTime2 = findViewById<TextView>(R.id.GolsTime2)

        var tvNomeTime1 = findViewById<TextView>(R.id.NomeTime1)
        var tvNomeTime2 = findViewById<TextView>(R.id.NomeTime2)

        var nomeTimes = placar.nome_partida.split("x")

        tvNomeTime1.text = nomeTimes[0]
        tvNomeTime2.text = nomeTimes[1]

        //Log.v("PDM", placar.resultado[0].toString());

        tvGolsTime1.setOnClickListener{
            var golsTime1 = placar.resultado.last()[0]
            var golsTime2 = placar.resultado.last()[1]

            golsTime1++

            var novoPlacar = arrayOf(golsTime1, golsTime2)

            placar.resultado = placar.resultado.plus(novoPlacar)

            modificaPlacar()
        }

        tvGolsTime2.setOnClickListener{
            var golsTime1 = placar.resultado.last()[0]
            var golsTime2 = placar.resultado.last()[1]

            golsTime2++

            var novoPlacar = arrayOf(golsTime1, golsTime2)

            placar.resultado = placar.resultado.plus(novoPlacar)

            modificaPlacar()
        }

        //Mudar o nome da partida
//        val tvNomePartida = findViewById(R.id.tvNomePartida2) as TextView
//        tvNomePartida.text=placar.nome_partida
//        ultimoJogos()
    }

    fun modificaPlacar (){

        var tvGolsTime1 = findViewById<TextView>(R.id.GolsTime1)
        var tvGolsTime2 = findViewById<TextView>(R.id.GolsTime2)

        tvGolsTime1.text = placar.resultado.last()[0].toString()
        tvGolsTime2.text = placar.resultado.last()[1].toString()

    }


    fun vibrar (v:View){
        val buzzer = this.getSystemService<Vibrator>()
         val pattern = longArrayOf(0, 200, 100, 300)
         buzzer?.let {
             if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                 buzzer.vibrate(VibrationEffect.createWaveform(pattern, -1))
             } else {
                 //deprecated in API 26
                 buzzer.vibrate(pattern, -1)
             }
         }

    }

    fun saveGame(v: View) {

        val sharedFilename = "PreviousGames"
        val sp: SharedPreferences = getSharedPreferences(sharedFilename, Context.MODE_PRIVATE)
        var edShared = sp.edit()
        //Salvar o número de jogos já armazenados
        var numMatches= sp.getInt("numberMatch",0) + 1
        edShared.putInt("numberMatch", numMatches)

        //Escrita em Bytes de Um objeto Serializável
        var dt= ByteArrayOutputStream()
        var oos = ObjectOutputStream(dt);
        oos.writeObject(placar);

        //Salvar como "match"
        edShared.putString("match"+numMatches, dt.toString(StandardCharsets.ISO_8859_1.name()))
        edShared.commit() //Não esqueçam de comitar!!!

    }

    fun lerUltimosJogos(v: View){
        val sharedFilename = "PreviousGames"
        val sp: SharedPreferences = getSharedPreferences(sharedFilename, Context.MODE_PRIVATE)

        var meuObjString:String= sp.getString("match1","").toString()
        if (meuObjString.length >=1) {
            var dis = ByteArrayInputStream(meuObjString.toByteArray(Charsets.ISO_8859_1))
            var oos = ObjectInputStream(dis)
            var placarAntigo:Placar=oos.readObject() as Placar
            //Log.v("SMD26",placar.resultado[0])
        }
    }

    fun ultimoJogos () {
        val sharedFilename = "PreviousGames"
        val sp:SharedPreferences = getSharedPreferences(sharedFilename,Context.MODE_PRIVATE)
        var matchStr:String=sp.getString("match1","").toString()
       // Log.v("PDM22", matchStr)
        if (matchStr.length >=1){
            var dis = ByteArrayInputStream(matchStr.toByteArray(Charsets.ISO_8859_1))
            var oos = ObjectInputStream(dis)
            var prevPlacar:Placar = oos.readObject() as Placar
            Log.v("PDM22", "Jogo Salvo:"+ prevPlacar.resultado)
        }

    }
}