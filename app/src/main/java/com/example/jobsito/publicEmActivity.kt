package com.example.jobsito

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.jobsito.databinding.ActivityPublicEmBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_modificar.*
import kotlinx.android.synthetic.main.activity_public_em.*
import java.util.*

class publicEmActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private lateinit var binding: ActivityPublicEmBinding
    private lateinit var turnosLista: ArrayAdapter<String>
    var turnosObtenidos = "No asignado"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_public_em)
        title = "Publica tu empleo"
        //toma el email de google para enviarlo a otra pantalla
        val bundle = intent.extras
        val email = bundle?.getString("email")

        //binding para la lista
        binding = ActivityPublicEmBinding.inflate(layoutInflater)
        setContentView(binding.root)
        turnosLista = ArrayAdapter<String>(this, R.layout.dropdown_item)
        turnosLista.addAll("No asignado","A eleccion","Rotativo","Mañana","Tarde","Noche",)
        binding.spinnerTurnos.onItemSelectedListener = this
        binding.spinnerTurnos.adapter = turnosLista


        publicButton.setOnClickListener {
            if (validate()) {
                //datos que se muestran princialmente
                val titleString = postTitleTextView.text.toString()
                val postString = postTextView.text.toString()
                val date = Date()
                val userName = email

                //datos detallados
                val description = postDescriptionTextView.text.toString()
                val requisitos = postRequisitosTextView.text.toString()
                val turnos = turnosObtenidos


                val post = Post(titleString,postString, date, userName, description,requisitos, turnos)
                db.collection("posts").add(
                    post
                )
                    .addOnSuccessListener {
                        finish()

                    }.addOnFailureListener {
                        println("error en crear el post")
                    }
            }


        }

        cancelButton.setOnClickListener {
            onBackPressed()
        }
    }

    //obtiene datos de la lista y los guarda en variables
    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        turnosObtenidos = turnosLista.getItem(position).toString()
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {

    }

    private fun validate(): Boolean{
        var isValid = false
        //valida si esta vacio
        if (postTitleTextView.text.toString().isBlank())
        {
            postTitleTextView.error = "Campo vacio"
        }
        else if (postTextView.text.toString().isBlank())
        {
            postTextView.error = "Campo vacio"
        }
        else if (postDescriptionTextView.text.toString().isBlank())
        {
            postDescriptionTextView.error = "Campo vacio"
        }
        else if (postRequisitosTextView.text.toString().isBlank() ){
            postRequisitosTextView.error = "Campo vacio"
        }
        //valida si el tamaño es correcto
        else if (postTitleTextView.text.toString().length < 5){
            postTitleTextView.error = "Nombre no valido"
        }
        else if (postTextView.text.toString().length < 5){
            postTextView.error = "Telefono no valido"
        }
        else if(postDescriptionTextView.text.toString().length < 10)
        {
            postDescriptionTextView.error = "Localidad no valida"
        }
        else if (postRequisitosTextView.text.toString().length < 8){
            postRequisitosTextView.error = "DNI no valido"
        }
        else
        {
            isValid = true
        }
        return isValid
    }
}