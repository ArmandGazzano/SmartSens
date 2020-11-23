package com.ag.smartsens

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {


    private var bleActif = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bluetooth.setOnClickListener {
            if (!bleActif) {
                val intent = Intent(this, BleConnect::class.java)
                startActivity(intent)
                bleActif = true
                bleC.visibility = View.VISIBLE
                bleD.visibility = View.INVISIBLE
                bluetooth.text = "DECONNEXION BLUETOOTH"

            } else {
                bleActif = false
                bleC.visibility = View.INVISIBLE
                bleD.visibility = View.VISIBLE
                bluetooth.text = "CONNEXION BLUETOOTH"
            }
        }
    }
}