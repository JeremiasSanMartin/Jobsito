package com.example.jobsito

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_inicio_em.*
import kotlinx.android.synthetic.main.activity_postulados.*
import kotlinx.android.synthetic.main.card_post.*

class PostuladosActivity : AppCompatActivity(), OnItemClickListener {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private lateinit var recyclerView: RecyclerView
    private lateinit var recyclerAdapter: PostuladosAdapter

    private var postuladosList = mutableListOf<String>()
    private var mostrarList = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_postulados)
        //toma los postulados de la actividad anterior para mostrarlos en la lista
        val bundle = intent.extras
        val postulados = bundle?.getString("postulados")

        title= "Tus postulados"


        //muestra la lista de postulados
         db.collection("posts").document(postulados!!).addSnapshotListener { value, error ->
             //obtencion de los likes de la base de datos y los convierte en una mutable list
             postuladosList = value!!.get("likes") as MutableList<String>
             println(postuladosList)

             //lo añade a una lista general
             mostrarList.addAll(postuladosList)
             rvPostulados.apply {
                 setHasFixedSize(true)
                 layoutManager = LinearLayoutManager(this@PostuladosActivity)

             }
         }

        mostrarList.addAll(postuladosList)
        recyclerView = findViewById(R.id.rvPostulados)
        recyclerAdapter = PostuladosAdapter(mostrarList, this)

        recyclerView.adapter = recyclerAdapter




        volverButton.setOnClickListener{
            val intent = Intent(this,InicioEmActivity::class.java)
            intent.putExtra("email",auth.currentUser?.email)
            startActivity(intent)
        }
    }

    override fun onItemClicked(uid : String) {
    }

    override fun onItemClickedProfile(position: Int) {
        val intent = Intent(this,ProfilePostuladosActivity::class.java)
        intent.putExtra("email", postuladosList[position])
        startActivity(intent)
    }
}