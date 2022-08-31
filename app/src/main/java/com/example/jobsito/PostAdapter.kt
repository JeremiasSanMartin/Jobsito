package com.example.jobsito

import android.app.Activity
import android.app.MediaRouteButton
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.abt.FirebaseABTesting
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.installations.Utils
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.card_post.*
import kotlinx.android.synthetic.main.card_post.view.*
import java.text.SimpleDateFormat
import java.util.*

//adaptador para usar el post en el recycle view, esta es la clase donde viene la informacion de los post
class PostAdapter(private val activity: Activity, private val dataset: List<Post>) :
    RecyclerView.Adapter<PostAdapter.ViewHolder>() {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    class ViewHolder(val layout: View) : RecyclerView.ViewHolder(layout)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layout = LayoutInflater.from(parent.context).inflate(R.layout.card_post, parent, false)

        return ViewHolder(layout)
    }

    //darle la cantidad de items que hay
    override fun getItemCount() = dataset.size

    //muestra la informacion del post
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val post = dataset[position]
        val likes = post.likes!!.toMutableList()
        var liked = likes.contains(auth.uid)

        //añade los datos al post
        holder.layout.likesCount.text = "${likes.size} Likes"
        holder.layout.userCard.text = post.userName
        holder.layout.postCard.text = post.post

        val sdf = SimpleDateFormat("dd/M/yyyy")

        holder.layout.dateCard.text = sdf.format(post.date)

        //muestra el color del like
        setColor(liked, holder.layout.likeBtn)

        //controla si le doy like o no
        holder.layout.likeBtn.setOnClickListener {
            liked = !liked
            setColor(liked, holder.layout.likeBtn)

            //si le doy like se añade mi id en caso contrario se quita mi id
            if (liked) likes.add(auth.uid!!)
            else likes.remove(auth.uid)

            val doc = db.collection("posts").document(post.uid!!)

            //transacion para que no ocurra conflictos cuando varios usuarios dan like
            db.runTransaction {
                it.update(doc, "likes", likes)

                null
            }
        }


        //funcion para el boton de compartir, este solo comparte un texto plano
        holder.layout.shareBtn.setOnClickListener {
            val sendIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, post.post)
                type = "text/plain"
            }

            val shareIntent = Intent.createChooser(sendIntent, null)
            activity.startActivity(shareIntent)
        }
        //comprueba si sos una empresa para mostrar el boton de borrar
        var tipo: String? = ""

        db.collection("users").document(post.userName!!).collection("prueba").document("prueba")
            .get().addOnSuccessListener { documento ->
            tipo = documento.data?.get("tipo").toString()

            if (tipo.equals("empresa")) {
                println(tipo)
                holder.layout.delBtn.visibility = View.VISIBLE
            }
        }

        //hace la accion de eliminar el post de la base de datos al tocarse el boton
        holder.layout.delBtn.setOnClickListener {
            db.collection("posts").document(post.uid!!).delete().addOnSuccessListener {

            }.addOnFailureListener {
                println("error")
            }
        }
    }

    //da el color si se dio like
    private fun setColor(liked: Boolean, likeButton: Button) {
        if (liked) likeButton.setTextColor(ContextCompat.getColor(activity, R.color.colorPrimary))
        else likeButton.setTextColor(Color.BLACK)
    }

}