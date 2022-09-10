package com.example.jobsito

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_profile_postulados.*
import kotlinx.android.synthetic.main.activity_profile_view.*

class ProfilePostuladosActivity : AppCompatActivity() {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_postulados)

        val bundle = intent.extras
        val postulados = bundle?.getString("postulados")
        val email = bundle?.getString("email")

        Actualizar(email!!)
        atrasButton.setOnClickListener {
            //comprueba si sos una empresa para mostrar el boton de borrar
            onBackPressed()
        }
    }
    //funcion para actualizar los datos del usuario tomados de la BD
    private fun Actualizar(email: String) {
        db.collection("users").document(email).get().addOnSuccessListener {
            nombreTxt.setText(it.get("nombrecompleto") as String?)
            telefonoTxt.setText(it.get("phone") as String?)
            dniTxt.setText(it.get("dni") as String?)
            localidadTxt.setText(it.get("localidad") as String?)
            tituloTxt.setText(it.get("titulo") as String?)
            emailTxt.setText(email)
        }
    }
}