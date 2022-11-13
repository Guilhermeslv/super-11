package ufc.smd.esqueleto_placar

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator

import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Chronometer
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.content.getSystemService
import androidx.core.view.isVisible
import com.google.android.material.floatingactionbutton.FloatingActionButton
import data.Placar
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.nio.charset.StandardCharsets
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.ArrayList

class PlacarActivity : AppCompatActivity() {

    lateinit var placar: Placar
    lateinit var timer: Chronometer
    lateinit var timerButton: FloatingActionButton
    private lateinit var reverter :FloatingActionButton
    private lateinit var btnSave :FloatingActionButton
    private val sharedFilename = "PreviousGames"

    var game = 0

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_placar)
        Log.v("Placar Activy Criado","kkkkkkkk")

        //Recupera da view anterior os dados do jogo cadastrado
        placar = getIntent().getExtras()?.getSerializable("placar") as Placar
        btnSave = findViewById(R.id.btSalvarPlacar)
        reverter = findViewById(R.id.btVerJogos)

        timer = findViewById<Chronometer>(R.id.c_meter)
        timerButton = findViewById<FloatingActionButton>(R.id.pauseCrono)
        var tvGolsTime1 = findViewById<TextView>(R.id.GolsTime1)
        var tvGolsTime2 = findViewById<TextView>(R.id.GolsTime2)

        var tvNomeTime1 = findViewById<TextView>(R.id.NomeTime1)
        var tvNomeTime2 = findViewById<TextView>(R.id.NomeTime2)

        var nomeTimes = placar.nome_partida.split("x")
        val hastimer = placar.has_timer

        if(!hastimer){
            timer.isVisible = false
            timerButton.isVisible = false
        }else{
            timer.stop()
        }

        tvNomeTime1.text = nomeTimes[0]
        tvNomeTime2.text = nomeTimes[1]

        //Log.v("PDM", placar.resultado[0].toString());

        tvGolsTime1.setOnClickListener{
            var golsTime1 = placar.resultados.last()[0]
            var golsTime2 = placar.resultados.last()[1]

            golsTime1++

            var novoPlacar = listOf(golsTime1, golsTime2)

            modificaPlacar(novoPlacar)
            renderizarPlacar()
        }

        tvGolsTime2.setOnClickListener{
            var golsTime1 = placar.resultados.last()[0]
            var golsTime2 = placar.resultados.last()[1]

            golsTime2++

            var novoPlacar = listOf(golsTime1, golsTime2)

            modificaPlacar(novoPlacar)
            renderizarPlacar()
        }

        reverter.setOnClickListener {
            reverterPlacar()
        }

        btnSave.setOnClickListener{
            saveGame()
            lerUltimosJogos()
            backToMain()

        }
        //Mudar o nome da partida
//        val tvNomePartida = findViewById(R.id.tvNomePartida2) as TextView
//        tvNomePartida.text=placar.nome_partida
//        ultimoJogos()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun modificaPlacar (novoPlacar:List<Int>){
        Log.v("Pronto para inserir >>> ","$novoPlacar")
        //Limit with offset
        if(placar.resultados.size >= 10){
            Log.v("Insert into front >>> ","$novoPlacar")
            placar.resultados.add(novoPlacar)
            placar.resultados.removeFirst()
        }else{
            placar.resultados.add(novoPlacar)
        }
        Log.v("Items","Valores >>> ${placar.resultados}")
        //Update time
        var current = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")
        val formatted = current.format(formatter)
        placar.date = formatted
    }

    fun renderizarPlacar(){
        var time1 = findViewById<TextView>(R.id.GolsTime1)
        var time2 = findViewById<TextView>(R.id.GolsTime2)

//        time1.text = placar.resultados.last()[0].toString()
        time1.text = placar.resultados.last()[0].toString()
        time2.text = placar.resultados.last()[1].toString()
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

    fun reverterPlacar(){
        var lastScore = placar.resultados.lastOrNull()
        if(placar.resultados.size > 1){

            placar.resultados.removeLastOrNull()
        }
        Log.v("Revertendo","$lastScore")
        renderizarPlacar()
    }

    fun saveGame() {
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
    fun backToMain(){
        val intent = Intent(this, MainActivity::class.java).apply{
        }
        startActivity(intent)
    }
    fun lerUltimosJogos(){
        val sp: SharedPreferences = getSharedPreferences(sharedFilename, Context.MODE_PRIVATE)

        var numMatches= sp.getInt("numberMatch",0)
        if(numMatches === 0){
            return
        }
        Log.v("SMD26", numMatches.toString())
        for(i:Int in  1..numMatches){
            var meuObjString:String= sp.getString("match"+i,"vazio")!!
            if (!meuObjString.equals("vazio")) {
                var dis = ByteArrayInputStream(meuObjString.toByteArray(Charsets.ISO_8859_1))
                var oos = ObjectInputStream(dis)
                var placarAntigo:Placar=oos.readObject() as Placar
                Log.v("SMD26", placarAntigo.resultados.toString())
            }
        }

    }

    fun ultimoJogos () {
        val sp:SharedPreferences = getSharedPreferences(sharedFilename,Context.MODE_PRIVATE)
        var matchStr:String=sp.getString("match1","").toString()
       // Log.v("PDM22", matchStr)
        if (matchStr.length >=1){
            var dis = ByteArrayInputStream(matchStr.toByteArray(Charsets.ISO_8859_1))
            var oos = ObjectInputStream(dis)
            var prevPlacar:Placar = oos.readObject() as Placar
            Log.v("PDM22", "Jogo Salvo:"+ prevPlacar.resultados)
        }

    }

    // ic_media_pause
    // ic_media_play
    fun PauseTimer(v: View){
        if(timer.isActivated) {
            timer.stop()
            timerButton.setImageResource(R.drawable.play_buttton)
        }else{
            timer.start()
            timerButton.setImageResource(R.drawable.pause)
        }
    }
}