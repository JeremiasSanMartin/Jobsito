package com.example.jobsito

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.activity_inicio.*
import java.util.*

class InicioActivity : AppCompatActivity() {
    private val db = FirebaseFirestore.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inicio)
        //toma el email de google para enviarlo a otra pantalla
        val bundle = intent.extras
        val email = bundle?.getString("email")



        //toma el post de la base de datos y lo muestra con apply
        db.collection("posts").addSnapshotListener { value, error ->
                val posts = value!!.toObjects(Post::class.java)

                posts.forEachIndexed{index, post ->
                    post.uid = value.documents[index].id
                }

            rv.apply {
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(this@InicioActivity)
                adapter = PostAdapter(this@InicioActivity, posts)
            }

        }





        title = null


        //boton para moverse entre pantallas
        perfilButton2.setOnClickListener{

            val homeIntent = Intent(this, HomeActivity::class.java).apply {

                //envia el email
                putExtra("email", email)
            }
            startActivity(homeIntent)
        }

        }
    }
