package com.example.jobsito

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_description.*
import kotlinx.android.synthetic.main.activity_profile_view.*

class DescriptionActivity : AppCompatActivity() {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_description)
        //toma el email de de la actividad anterior para mostrar el perfil
        val bundle = intent.extras
        val email = bundle?.getString("email")
        val descripcion = bundle?.getString("descripcion")
        val requisitos = bundle?.getString("requisitos")
        val turnos = bundle?.getString("turnos")
        Actualizar(email?: "", descripcion?:"",requisitos?:"",turnos?:"")

        backInicioButton.setOnClickListener{
            val intent = Intent(this, InicioActivity::class.java)
            intent.putExtra("email", auth.currentUser?.email)
            startActivity(intent)
        }
        profileButton.setOnClickListener{
            val intent = Intent(this, profileViewActivity::class.java)
            intent.putExtra("email", email)
            intent.putExtra("descripcion", descripcion)
            intent.putExtra("requisitos", requisitos)
            intent.putExtra("turnos", turnos)
            startActivity(intent)
        }
    }


    private fun Actualizar(email: String, descripcion: String, requisitos: String, turnos: String,) {

            DescriptionTextView.setText(descripcion)
            requisitosTextView.setText(requisitos)
            turnoTextView.setText(turnos)

    }
}