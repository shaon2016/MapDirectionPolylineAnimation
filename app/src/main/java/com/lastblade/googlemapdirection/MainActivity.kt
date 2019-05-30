package com.lastblade.googlemapdirection

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnOverLayMap.setOnClickListener {
            startActivity(Intent(this, MapsActivity::class.java))
        }
        btnRouteAnim.setOnClickListener {
            startActivity(Intent(this, RouteAnimActivity::class.java))
        }
    }
}
