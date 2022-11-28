package com.example.jobsito

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.view.menu.ActionMenuItemView
import androidx.recyclerview.widget.RecyclerView

class PostuladosAdapter(postuladosList: MutableList<String>, private val onItemClickListener: OnItemClickListener):  RecyclerView.Adapter<PostuladosAdapter.PostuladosViewHolder>(){

    private var postulados: MutableList<String> = postuladosList

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PostuladosAdapter.PostuladosViewHolder {
        //crea la vista usando la targeta creada para los postulados (postulados_card)
        val v = LayoutInflater.from(parent.context).inflate(R.layout.postulados_card,parent,false)
        return PostuladosViewHolder(v)
    }

    override fun onBindViewHolder(holder: PostuladosAdapter.PostuladosViewHolder, position: Int) {
        //obtiene la posicion de postulados
        holder.postuladosMail.text = postulados[position]
    }

    override fun getItemCount(): Int {
        //obtiene la cantidad de postulados
        return postulados.size
    }

    inner class PostuladosViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){

        //crea las variables con las id del incono en la targeta de postulados
        var itemImage: ImageView
        //y el texto del email en la targeta de postulados
        var postuladosMail: TextView

        //inicia usando estas variables
        init {
                                                //incono en la targeta de postulados
            itemImage = itemView.findViewById(R.id.itemImage)
                                                    //y el texto del email en la targeta de postulados
            postuladosMail = itemView.findViewById(R.id.postuladosMail)

            //click listener que envia la posicion para poder ver el perfil del postulado
            itemView.setOnClickListener {
                val position: Int = adapterPosition
                onItemClickListener.onItemClickedProfile(position)
            }
        }
    }
}