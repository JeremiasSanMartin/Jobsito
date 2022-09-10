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
        val v = LayoutInflater.from(parent.context).inflate(R.layout.postulados_card,parent,false)
        return PostuladosViewHolder(v)
    }

    override fun onBindViewHolder(holder: PostuladosAdapter.PostuladosViewHolder, position: Int) {
        holder.postuladosMail.text = postulados[position]
    }

    override fun getItemCount(): Int {
        return postulados.size
    }

    inner class PostuladosViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        var itemImage: ImageView
        var postuladosMail: TextView

        init {
            itemImage = itemView.findViewById(R.id.itemImage)
            postuladosMail = itemView.findViewById(R.id.postuladosMail)

            itemView.setOnClickListener {
                val position: Int = adapterPosition
                onItemClickListener.onItemClickedProfile(position)
            }
        }
    }
}