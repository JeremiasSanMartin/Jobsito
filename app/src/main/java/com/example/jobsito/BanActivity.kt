package com.example.jobsito

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class BanActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ban)
        title = "Cuenta suspendida"
    }
}