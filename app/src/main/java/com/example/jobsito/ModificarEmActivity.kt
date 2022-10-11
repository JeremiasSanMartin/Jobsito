package com.example.jobsito

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_modificar.*
import kotlinx.android.synthetic.main.activity_modificar.saveButton
import kotlinx.android.synthetic.main.activity_modificar_em.*

class ModificarEmActivity : AppCompatActivity() {
    //instancia conectada a la base de datos
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_modificar_em)

        //setup
        val bundle = intent.extras
        val email = bundle?.getString("email")
        setup(email ?: "")
        obtenerDatos(email ?: "")
    }

    private fun setup(email: String) {
        title = "Modificar perfil"
        emailTextViewEm.text = email
        //acceder a los botones guardar, recuperar y eliminar
        saveButton.setOnClickListener {
            //comprueba que ningun campo este vacio
            if (validateEmpty()|| validateLenght()) {

                //crea un documento por email
                db.collection("users").document(email).set(
                    hashMapOf(
                        "direccion" to direccionTextViewEm.text.toString(),
                        "nombreEmpresa" to nameTextViewEm.text.toString(),
                        "phone" to phoneTextViewEm.text.toString(),
                        "cuit" to cuitTextViewEm.text.toString(),
                        "rubroTrabajo" to rubroTextViewEm.text.toString()
                    )
                ).addOnSuccessListener {
                    Toast.makeText(this, "Datos moficados", Toast.LENGTH_SHORT).show()
                    val homeIntent = Intent(this, EmpresaActivity::class.java).apply {

                        //envia el email
                        putExtra("email", email)
                    }
                    startActivity(homeIntent)
                }
            }
        }


    }
    private fun validateLenght(): Boolean{
        var isValid = false
        if (direccionTextViewEm.text.toString().length < 20)
        {
            direccionTextViewEm.error = "Direccion no valida"
        }
        else if (nameTextViewEm.text.toString().length < 30){
            nameTextViewEm.error = "Nombre no valido"
        }
        else if (cuitTextViewEm.text.toString().length != 11){
            cuitTextViewEm.error = "CUIT no valido"
        }
        else if (phoneTextViewEm.text.toString().length < 9){
            phoneTextViewEm.error = "Telefono no valido"
        }
        else
        {
            isValid = true
        }
        return isValid
    }
    private fun validateEmpty(): Boolean{
        var isValid = false
        if (direccionTextViewEm.text.toString().isBlank())
        {
            direccionTextViewEm.error = "Campo vacio"
        }
        else if (nameTextViewEm.text.toString().isBlank()){
            nameTextViewEm.error = "Campo vacio"
        }
        else if (cuitTextViewEm.text.toString().isBlank()){
            cuitTextViewEm.error = "Campo vacio"
        }
        else if (phoneTextViewEm.text.toString().isBlank() ){
            phoneTextViewEm.error = "Campo vacio"
        }
        else if (rubroTextViewEm.text.toString().isBlank()){
            rubroTextViewEm.setText("Ninguno")
        }
        else{
            isValid = true
        }
        return isValid
    }
    private fun obtenerDatos(email: String) {
        //obtiene el documento por email
        db.collection("users").document(email).get().addOnSuccessListener {
            direccionTextViewEm.setText(it.get("direccion") as String?)
            nameTextViewEm.setText(it.get("nombreEmpresa") as String?)
            phoneTextViewEm.setText(it.get("phone") as String?)
            cuitTextViewEm.setText(it.get("cuit") as String?)
            rubroTextViewEm.setText(it.get("rubroTrabajo") as String?)


        }
    }
}