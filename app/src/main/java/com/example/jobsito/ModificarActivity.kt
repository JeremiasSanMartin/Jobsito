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
            if (validate()) {

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
    private fun validate(): Boolean{
        var isValid = false
        //valida si esta vacio
        if (fullNameTextView.text.toString().isBlank())
        {
            fullNameTextView.error = "Campo vacio"
        }
        else if (phoneTextView.text.toString().isBlank())
        {
            phoneTextView.error = "Campo vacio"
        }
        else if (locationTextView.text.toString().isBlank())
        {
            locationTextView.error = "Campo vacio"
        }
        else if (dniTextView.text.toString().isBlank() ){
            dniTextView.error = "Campo vacio"
        }
        else if (tituloTextView.text.toString().isBlank()){
            tituloTextView.setText("Ninguno")
        }
        //valida si el tamaño es correcto
        else if (fullNameTextView.text.toString().length < 10){
            fullNameTextView.error = "Nombre no valido"
        }
        else if (phoneTextView.text.toString().length < 9){
            phoneTextView.error = "Telefono no valido"
        }
        else if(locationTextView.text.toString().length < 10)
        {
            locationTextView.error = "Localidad no valida"
        }
        else if (dniTextView.text.toString().length < 8){
            dniTextView.error = "DNI no valido"
        }
        else if (tituloTextView.text.toString().length < 8){
            tituloTextView.error = "Titlulo muy corto"
        }
        else
        {
            isValid = true
        }
        return isValid
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