package com.example.jobsito

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_inicio.*
import kotlinx.android.synthetic.main.activity_inicio.perfilButton2
import kotlinx.android.synthetic.main.activity_inicio_em.*

class InicioEmActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inicio_em)

        title = "Inicio"

        //toma el email de google para enviarlo a otra pantalla
        val bundle = intent.extras
        val email = bundle?.getString("email")
        perfilButton2.setOnClickListener{
            //boton para moverse entre pantallas
            val homeIntent = Intent(this, EmpresaActivity::class.java).apply {

                //envia el email
                putExtra("email", email)
            }
            startActivity(homeIntent)
        }
        //boton para ir a la pestaña de crear publicaciones
        publicarButton.setOnClickListener{
            val publicIntent = Intent(this, publicEmActivity::class.java).apply {

                //envia el email
                putExtra("email", email)
            }
            startActivity(publicIntent)
        }
    }
}