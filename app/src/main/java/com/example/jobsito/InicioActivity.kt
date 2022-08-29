package com.example.jobsito

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.LinearLayout
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.activity_inicio.*
import java.util.*

class InicioActivity : AppCompatActivity() {
    private var displayList = mutableListOf<Post>()
    private var posts = mutableListOf<Post>()
    private val db = FirebaseFirestore.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inicio)
        //toma el email de google para enviarlo a otra pantalla
        val bundle = intent.extras
        val email = bundle?.getString("email")



        //toma el post de la base de datos y lo muestra con apply
        db.collection("posts").addSnapshotListener { value, error ->
            posts = value!!.toObjects(Post::class.java)
                posts.forEachIndexed{index, post ->
                    post.uid = value.documents[index].id
                }

            //guarda en una lista alternativa todos los datos para despues hacer la busqueda
            displayList.addAll(posts)
            rv.apply {
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(this@InicioActivity)
                adapter = PostAdapter(this@InicioActivity, displayList)
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

    //busqueda
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)

        //variable del search view
        var item: MenuItem = menu!!.findItem(R.id.action_search)

        //pregunta si el search view no esta vacio
        if (item != null){
            var searchView= item.actionView as SearchView

            searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
                override fun onQueryTextSubmit(query: String?): Boolean {
                    return true
                }
                //usa la query de textChange
                override fun onQueryTextChange(newText: String?): Boolean {
                    if (newText!!.isNotEmpty()){
                        displayList.clear()
                        var search = newText.toLowerCase(Locale.getDefault())

                        //comprueba si lo que se escribe esta en alguno de los emails
                        for (name in posts){
                            if (name.userName!!.toLowerCase(Locale.getDefault()).contains(search)){
                                displayList.add(name)
                            }
                            rv.adapter!!.notifyDataSetChanged()
                        }
                        //otros for para otras busquedas

                        //en caso de que sea nulo no muestra ningun post
                    }else{
                        displayList.clear()
                        displayList.addAll(posts)
                        rv.adapter!!.notifyDataSetChanged()
                    }
                    return true
                }

            })
        }

        return super.onCreateOptionsMenu(menu)
    }
    }
