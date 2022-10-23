package ufc.smd.esqueleto_placar

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Switch
import data.Placar

class ConfigActivity : AppCompatActivity() {
    var placar: Placar= Placar("Jogo sem Config","0x0", "20/05/20 10h",false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_config)

        openConfig()
    }

    //Abre as configurações do shared preferences
    fun openConfig()
    {

        val sharedFilename = "configPlacar"
        val sp:SharedPreferences = getSharedPreferences(sharedFilename,Context.MODE_PRIVATE)
        placar.nome_partida = sp.getString("matchname","Jogo Padrão").toString()
        placar.has_timer = sp.getBoolean("has_timer",false)

    }

    //Salva as configurações no shared preferences
    fun saveConfig(){

        val sharedFilename = "configPlacar"
        val sp:SharedPreferences = getSharedPreferences(sharedFilename,Context.MODE_PRIVATE)
        var edShared = sp.edit()

        edShared.putString("matchname",placar.nome_partida)
        edShared.putBoolean("has_timer",placar.has_timer)
        edShared.commit()

    }

    //Salva no objeto de placar as configurações adicionadas pelo usuário
    fun updatePlacarConfig(){
        val time1 = findViewById<EditText>(R.id.editTextGameName)
        val time2 = findViewById<EditText>(R.id.editTextGameName2)

        val partida = time1.text.toString() +" x "+ time2.text.toString()

        val sw = findViewById<Switch>(R.id.swTimer)
        placar.nome_partida = partida
        placar.has_timer = sw.isChecked
    }

//    fun initInterface(){
//        val tv= findViewById<EditText>(R.id.editTextGameName2)
//        tv.setText(placar.nome_partida)
//        val sw= findViewById<Switch>(R.id.swTimer)
//        sw.isChecked=placar.has_timer
//    }

    //Executa ao click do Iniciar Jogo
    fun openPlacar(v: View){

        updatePlacarConfig() //Pega da Interface e joga no placar
        saveConfig() //Salva no Shared preferences

        val intent = Intent(this, PlacarActivity::class.java).apply{
            putExtra("placar", placar)
        }
        startActivity(intent)

    }
}