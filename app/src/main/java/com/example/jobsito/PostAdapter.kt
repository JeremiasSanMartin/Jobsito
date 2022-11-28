package com.example.jobsito

import android.app.Activity
import android.app.MediaRouteButton
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Button
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
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
class PostAdapter(private val activity: Activity, private val dataset: List<Post>, private val onItemClickListener: OnItemClickListener) :
    RecyclerView.Adapter<PostAdapter.ViewHolder>() {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private var Email = auth.currentUser!!.email
    class ViewHolder(val layout: View) : RecyclerView.ViewHolder(layout)


    //sobrescribe la funcion
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        //crea el layout usando la targeta de base en la actividad "card_post"
        val layout = LayoutInflater.from(parent.context).inflate(R.layout.card_post, parent, false)

        return ViewHolder(layout)
    }

    //darle la cantidad de items que hay
    override fun getItemCount() = dataset.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        //crea variables que guardan los datos para luego pasarlos al layout de card_post
        val post = dataset[position]
        val likes = post.likes!!.toMutableList()
        var liked = likes.contains(auth.currentUser!!.email!!)

        //estos no los muestra en el card_post pero aun asi guarda los datos para poder utilizarlos como descipcion detallada
        var description = post.desciption
        var requisitos = post.requisitos
        var turnos = post.turnos

        //muestra la informacion del post igualando los datos que llegan al adaptador a los textfield del card_post

        holder.layout.likesCount.text = "${likes.size} Postulados"
        holder.layout.userCard.text = post.userName
        holder.layout.postCard.text = post.post
        holder.layout.titleCard.text = post.title

        //crea el formate de la fecha en dias/meses/años
        val sdf = SimpleDateFormat("dd/M/yyyy")

        holder.layout.dateCard.text = sdf.format(post.date)

        //muestra el color del like
        setColor(liked, holder.layout.likeBtn)

        //controla si le di like o no
        holder.layout.likeBtn.setOnClickListener {
            liked = !liked
            setColor(liked, holder.layout.likeBtn)

            //si le doy like se añade mi id en caso contrario se quita mi id
            if (liked) {
                //añade el like
                likes.add(auth.currentUser!!.email!!)
            }
            else {
                //quita el like
                likes.remove(auth.currentUser!!.email!! )
            }

            val doc = db.collection("posts").document(post.uid!!)

            //transacion para que no ocurra conflictos cuando varios usuarios dan like
            db.runTransaction {
                it.update(doc, "likes", likes)

                null
            }
        }
        //comprueba si sos una empresa para mostrar el boton de borrar
        var tipo: String? = ""

        db.collection("users").document(Email!!).collection("prueba").document("prueba")
            .get().addOnSuccessListener { documento ->
            tipo = documento.data?.get("tipo").toString()

            //si eres una empresa muestra solo el boton de borrar publicacion
            if (tipo.equals("empresa")) {
                holder.layout.delBtn.visibility = View.VISIBLE

                //si es una empresa envia el uid para los postulados
                holder.layout.setOnClickListener {
                    onItemClickListener.onItemClicked(uid = post.uid!!)
                }
            }else{
                //si eres una persona muestra solo el boton de postularse
                holder.layout.likeBtn.visibility = View.VISIBLE
                //y si es una persona simplemente envia la posicion para ver el perfil
                holder.layout.setOnClickListener {
                    onItemClickListener.onItemClickedProfile(position)
                }

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