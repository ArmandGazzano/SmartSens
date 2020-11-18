package com.ag.smartsens

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var bleActif = false

        bluetooth.setOnClickListener {
            if (!bleActif){
                bleActif = true
                bleC.visibility = View.VISIBLE
                bleD.visibility = View.INVISIBLE
            }else {
                bleActif = false
                bleC.visibility = View.INVISIBLE
                bleD.visibility = View.VISIBLE
            }
        }
    }
}