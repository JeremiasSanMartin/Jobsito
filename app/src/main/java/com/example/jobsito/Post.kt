package com.example.jobsito

import com.google.firebase.database.Exclude
import java.util.*
import kotlin.collections.ArrayList

//crea la clase que guarda los datos de los post
class Post(
    val post: String? = null,
    val date: Date? = null,
    val userName: String? = null,
    val likes: ArrayList<String>? = arrayListOf()
) {
    //ponemos el id para que firebase no agregue un uid
    @Exclude
    @set:Exclude
    @get:Exclude
    var uid: String? = null

    //constructor para que firebase nos de la informacion directamente
    constructor() : this(null, null, null, null)
}