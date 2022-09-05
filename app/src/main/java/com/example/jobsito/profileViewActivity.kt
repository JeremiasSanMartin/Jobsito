package com.example.jobsito

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.activity_profile_view.*

class profileViewActivity : AppCompatActivity() {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_view)
        //toma el email de google para enviarlo a otra pantalla
        val bundle = intent.extras
        val email = bundle?.getString("email")
        Actualizar(email?: "")
        //boton para volver a la pagina de inicio
        backButton.setOnClickListener {
            //comprueba si sos una empresa para mostrar el boton de borrar
            val intent = Intent(this, InicioActivity::class.java)
            intent.putExtra("email", auth.currentUser?.email)
            startActivity(intent)
        }
    }
    //funcion para actualizar los datos del usuario tomados de la BD
    private fun Actualizar(email: String) {
        db.collection("users").document(email).get().addOnSuccessListener {
            nameEmTv.setText(it.get("nombreEmpresa") as String?)
            phoneTv.setText(it.get("phone") as String?)
            direTv.setText(it.get("direccion") as String?)
            cuitTv.setText(it.get("cuit") as String?)
            jobTv.setText(it.get("rubroTrabajo") as String?)
        }
    }
}


