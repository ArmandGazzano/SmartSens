package com.ag.smartsens

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*

var bleActif = false

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bluetooth.setOnClickListener {
            if (!bleActif){
                val intent = Intent(this, BleConnect::class.java)
                startActivity(intent)
                bleActif = true
                bleC.visibility = View.VISIBLE
                bleD.visibility = View.INVISIBLE
                bluetooth.text = "DECONNEXION BLUETOOTH"

            }else {
                bleActif = false
                bleC.visibility = View.INVISIBLE
                bleD.visibility = View.VISIBLE
                bluetooth.text = "CONNEXION BLUETOOTH"
            }
        }

        simulation.setOnClickListener {
            if (bleActif){

            }else {
                Toast.makeText(this, "Veuillez vous connecter en bluetooth", Toast.LENGTH_SHORT).show()
            }
        }
    }
}