package com.example.jobsito

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_home.*


import kotlinx.android.synthetic.main.activity_modificar.*


class ModificarActivity : AppCompatActivity() {
    //instancia conectada a la base de datos
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_modificar)


        //setup
        val bundle = intent.extras
        val email = bundle?.getString("email")
        setup(email ?: "")
        obtenerDatos(email ?: "")


    }

    private fun setup(email: String) {
        title = "Modificar perfil"
        emailTextView2.text = email
        //acceder a los botones guardar, recuperar y eliminar
        saveButton.setOnClickListener {
            //comprueba que ningun campo este vacio
            if (locationTextView.text.toString() == "" || fullNameTextView.text.toString() == ""
                || phoneTextView.text.toString() == "" || dniTextView.text.toString() == ""
                || tituloTextView.text.toString() == ""
            ) {

                Toast.makeText(this, "Hay campos vacios", Toast.LENGTH_SHORT).show()

            } else {

                //crea un documento por email
                db.collection("users").document(email).set(
                    hashMapOf(
                        "localidad" to locationTextView.text.toString(),
                        "nombrecompleto" to fullNameTextView.text.toString(),
                        "phone" to phoneTextView.text.toString(),
                        "dni" to dniTextView.text.toString(),
                        "titulo" to tituloTextView.text.toString()
                    )
                ).addOnSuccessListener {
                    Toast.makeText(this, "Datos moficados", Toast.LENGTH_SHORT).show()
                    val homeIntent = Intent(this, HomeActivity::class.java).apply {

                        //envia el email
                        putExtra("email", email)
                    }
                    startActivity(homeIntent)

                }
            }
        }


    }


    private fun obtenerDatos(email: String) {
        emailTextView2.text = email
        //obtiene el documento por email
        db.collection("users").document(email).get().addOnSuccessListener {
            locationTextView.setText(it.get("localidad") as String?)
            fullNameTextView.setText(it.get("nombrecompleto") as String?)
            phoneTextView.setText(it.get("phone") as String?)
            dniTextView.setText(it.get("dni") as String?)
            tituloTextView.setText(it.get("titulo") as String?)


        }
    }

}