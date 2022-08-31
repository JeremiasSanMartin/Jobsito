package com.example.jobsito

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_inicio.*
import kotlinx.android.synthetic.main.activity_inicio.perfilButton2
import kotlinx.android.synthetic.main.activity_inicio_em.*
import kotlinx.android.synthetic.main.card_post.*
import java.util.*

class InicioEmActivity : AppCompatActivity() {
    private var displayList = mutableListOf<Post>()
    private var filterList = mutableListOf<Post>()
    private var posts = mutableListOf<Post>()
    private val db = FirebaseFirestore.getInstance()
    private var tipo = String()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inicio_em)
        title = "Tus Publicaciones"

        //toma el email de google para enviarlo a otra pantalla
        val bundle = intent.extras
        val email = bundle?.getString("email")
        //toma el post de la base de datos y guarda todos en posts
        db.collection("posts").addSnapshotListener { value, error ->
            posts = value!!.toObjects(Post::class.java)
            posts.forEachIndexed { index, post ->
                post.uid = value.documents[index].id
            }

            //limpia la lista para que se resete al borrar
            displayList.clear()

            //guarda en una lista alternativa las publicaciones de la empresa
            for (emailEm in posts) {

                if (emailEm.userName!!.toLowerCase(Locale.getDefault()).contains(email!!)) {

                    displayList.add(emailEm)

                }
            }
            //guarda los datos de la lista alternativa en otra lista para usarla de filtro a la hora de buscar y que solo aparecan los datos de la empresa
            filterList.addAll(displayList)

            //aplica en el recycler view los datos de la lista
            rvEm.apply {
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(this@InicioEmActivity)
                adapter = PostAdapter(this@InicioEmActivity, displayList)
            }

        }

        //boton para moverse entre actividades
        perfilButton2.setOnClickListener {
            //boton para moverse entre pantallas
            val homeIntent = Intent(this, EmpresaActivity::class.java).apply {

                //envia el email
                putExtra("email", email)
            }
            startActivity(homeIntent)
        }
        //boton para ir a la pestaña de crear publicaciones
        publicarButton.setOnClickListener {
            val publicIntent = Intent(this, publicEmActivity::class.java).apply {

                //envia el email
                putExtra("email", email)
            }
            startActivity(publicIntent)
        }
    }

    //funcion de busqueda
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)

        //variable del search view
        var item: MenuItem = menu!!.findItem(R.id.action_search)

        //pregunta si el search view no esta vacio
        if (item != null) {
            var searchView = item.actionView as SearchView

            searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    return true
                }

                //usa la query de textChange
                override fun onQueryTextChange(newText: String?): Boolean {
                    if (newText!!.isNotEmpty()) {
                        displayList.clear()
                        var search = newText.toLowerCase(Locale.getDefault())

                        //comprueba si lo que se escribe esta en alguno de los campos
                        for (text in filterList) {
                            if (text.post!!.toLowerCase(Locale.getDefault()).contains(search)) {
                                displayList.add(text)
                            }
                            rvEm.adapter!!.notifyDataSetChanged()
                        }
                        //otros for para otras busquedas

                        //en caso de que sea nulo no muestra ningun post
                    } else {
                        displayList.clear()
                        displayList.addAll(filterList)
                        rvEm.adapter!!.notifyDataSetChanged()
                    }
                    return true
                }

            })
        }

        return super.onCreateOptionsMenu(menu)
    }
}