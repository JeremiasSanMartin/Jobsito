package com.example.jobsito

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_public_em.*
import java.util.*

class publicEmActivity : AppCompatActivity() {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_public_em)
        title = "Publica tu empleo"
        //toma el email de google para enviarlo a otra pantalla
        val bundle = intent.extras
        val email = bundle?.getString("email")

        publicButton.setOnClickListener {
            if (postTextView.text.toString() == "" || postTitleTextView.text.toString() == ""
                || postDescriptionTextView.text.toString() == ""|| postRequisitosTextView.text.toString() == ""
                || postTurnoTextView.text.toString() == "") {
                Toast.makeText(this, "Hay campos vacios", Toast.LENGTH_SHORT).show()
            } else {
                //datos que se muestran princialmente
                val titleString = postTitleTextView.text.toString()
                val postString = postTextView.text.toString()
                val date = Date()
                val userName = email

                //datos detallados
                val description = postDescriptionTextView.text.toString()
                val requisitos = postRequisitosTextView.text.toString()
                val turnos = postTurnoTextView.text.toString()


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
}