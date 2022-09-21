package com.example.jobsito

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.activity_inicio.*
import kotlinx.android.synthetic.main.card_post.*
import java.util.*

class InicioActivity : AppCompatActivity(),OnItemClickListener {
    private var displayList = mutableListOf<Post>()
    private var posts = mutableListOf<Post>()
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inicio)
        //toma el email de google para enviarlo a otra pantalla
        val bundle = intent.extras
        val email = bundle?.getString("email")
        title = null
        //toma el post de la base de datos y lo muestra con apply
        db.collection("posts").orderBy("date", Query.Direction.DESCENDING).addSnapshotListener { value, error ->
            posts = value!!.toObjects(Post::class.java)
            posts.forEachIndexed { index, post ->
                post.uid = value.documents[index].id
            }
            //guarda en una lista alternativa todos los datos para despues hacer la busqueda
            displayList = value!!.toObjects(Post::class.java)
            displayList.forEachIndexed { index, post ->
                post.uid = value.documents[index].id
            }
            rv.adapter  = PostAdapter(this@InicioActivity,  posts, this)
            rv.apply {
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(this@InicioActivity)
            }


        }

        //boton para moverse entre pantallas
        perfilButton2.setOnClickListener {

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
        if (item != null) {
            var searchView = item.actionView as SearchView

            searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    return true
                }

                //usa la query de textChange
                override fun onQueryTextChange(newText: String?): Boolean {
                    if (newText!!.isNotEmpty()) {
                        posts.clear()
                        var search = newText.toLowerCase(Locale.getDefault())

                        //comprueba si lo que se escribe esta en alguno de los emails
                        for (name in displayList) {
                            if (name.title!!.toLowerCase(Locale.getDefault()).contains(search) || name.post!!.toLowerCase(Locale.getDefault()).contains(search)) {
                                posts.add(name)
                            }
                            rv.adapter!!.notifyDataSetChanged()
                        }

                        //en caso de que sea nulo no muestra ningun post
                    } else {
                        posts.clear()
                        posts.addAll(displayList)
                        rv.adapter!!.notifyDataSetChanged()
                    }
                    return true
                }

            })
        }

        return super.onCreateOptionsMenu(menu)
    }

    override fun onBackPressed() {
        val homeIntent = Intent(this, HomeActivity::class.java).apply {

            //envia el email
            putExtra("email", auth.currentUser?.email)
        }
        startActivity(homeIntent)
    }
    override fun onItemClicked(uid : String) {
        TODO("Not yet implemented")
    }

    override fun onItemClickedProfile(position: Int) {

        val intentPublic = Intent(this,profileViewActivity::class.java)
        intentPublic.putExtra("email", posts[position].userName)
        startActivity(intentPublic)
    }
}
