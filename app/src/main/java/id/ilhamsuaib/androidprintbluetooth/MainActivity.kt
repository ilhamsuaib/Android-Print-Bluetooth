package id.ilhamsuaib.androidprintbluetooth

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import id.ilhamsuaib.androidprintbluetooth.nota.NotaActivity
import id.ilhamsuaib.androidprintbluetooth.spbu.SpbuActivity
import kotlinx.android.synthetic.main.activity_main.*

/**
 * Created by @ilhamsuaib on 23/10/18.
 * github.com/ilhamsuaib
 */

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnNota.setOnClickListener {
            startActivity<NotaActivity>()
        }
        btnSpbu.setOnClickListener {
            startActivity<SpbuActivity>()
        }
    }

    private inline fun<reified T: Activity> startActivity() {
        startActivity(Intent(this, T::class.java))
    }
}