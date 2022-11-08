package com.example.jobsito

import android.content.Intent
import android.opengl.Visibility
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.android.synthetic.main.activity_choose.*
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.activity_modificar.*

class chooseActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose)


        //toma el email de google para enviarlo a otra pantalla
        val bundle = intent.extras
        val email = bundle?.getString("email")
        setup(email ?: "")


    }

    private fun setup(email: String) {
        //comprueba que radio button esta seleccionado y muestra la pantalla correspondiente dandole una categoria al usuario
        // en caso de que no se elija ninguno imprime un mensaje
        continuarButton.setOnClickListener {
            //si el radio buton de persona esta elegido se le añade al usuario el tipo "trabajo"
            if (personaRadioButton.isChecked) {
                db.collection("users").document(email).collection("prueba")
                    .document("prueba").set(
                        hashMapOf("tipo" to "trabajo")
                    )

                val intent = Intent(this, ModificarActivity::class.java).apply {

                    //envia el email
                    putExtra("email", email)
                }

                // Temas (Topics)
                //este codigo subscribe al usuario al tema "Empleado" para enviar notificaciones personalizadas
                FirebaseMessaging.getInstance().subscribeToTopic("Empleado")

                Toast.makeText(this, "ingreso como Persona", Toast.LENGTH_SHORT).show()
                startActivity(intent)

            //si el radio buton de empresa esta elegido se le añade al usuario el tipo "empresa"
            } else if (empresaRadioButton.isChecked) {
                db.collection("users").document(email).collection("prueba")
                    .document("prueba").set(
                        hashMapOf("tipo" to "empresa")
                    )
                val empresaIntent = Intent(this, ModificarEmActivity::class.java).apply {

                    //envia el email
                    putExtra("email", email)
                }

                //este codigo subscribe al usuario al tema "Empresa" para enviar notificaciones personalizadas
                FirebaseMessaging.getInstance().subscribeToTopic("Empresa")

                Toast.makeText(this, "ingreso como Empresa", Toast.LENGTH_SHORT).show()
                startActivity(empresaIntent)

                //en caso contrario avisa que se debe ingresar uno
            } else {
                Toast.makeText(this, "Debe seleccionar un tipo", Toast.LENGTH_SHORT).show()
            }


        }


    }
}
