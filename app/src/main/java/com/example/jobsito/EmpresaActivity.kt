package com.example.jobsito

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_empresa.*
import kotlinx.android.synthetic.main.activity_empresa.inicioButton
import kotlinx.android.synthetic.main.activity_empresa.modificarButton
import kotlinx.android.synthetic.main.activity_empresa.publicarButton
import kotlinx.android.synthetic.main.activity_home.emailTextView
import kotlinx.android.synthetic.main.activity_home.logOutButton
import kotlinx.android.synthetic.main.activity_home.fullNameShowTextView
import kotlinx.android.synthetic.main.activity_home.phoneShowTextView
import kotlinx.android.synthetic.main.activity_inicio_em.*

class EmpresaActivity : AppCompatActivity() {
    private val db = FirebaseFirestore.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_empresa)
        //setup
        val bundle = intent.extras
        val email =bundle?.getString("email")
        setup(email?:"")

        val myHandler = Handler(Looper.getMainLooper())
        myHandler.post(object : Runnable {
            override fun run() {
                Actualizar(email?:"")
                myHandler.postDelayed(this, 100 /*0,5 segundos*/)
            }
        })

        //guarda los datos del inicio de sesion del usuario hasta que este mismo decida cerrar la sesion
        val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
        prefs.putString("email",email)
        prefs.apply()

        //boton que envia a la pantalla de modificar el perfil
        modificarButton.setOnClickListener {
            val intent = Intent(this, ModificarEmActivity::class.java).apply {

                putExtra("email", email)
            }
            startActivity(intent)
        }

        //boton de movimiento entre pantallas
        inicioButton.setOnClickListener{
            val inicioIntent = Intent(this, InicioEmActivity::class.java).apply {

                //envia el email
                putExtra("email", email)
            }

            startActivity(inicioIntent)
        }
        //boton para ir a la pestaña de crear publicaciones
        publicarButton.setOnClickListener{
            val publicIntent = Intent(this, publicEmActivity::class.java).apply {

                //envia el email
                putExtra("email", email)
            }
            startActivity(publicIntent)
        }
    }




    //funcion para actualizar los datos del usuario tomados de la BD
    private fun Actualizar(email:String) {
        emailTextView.text = email
        db.collection("users").document(email).get().addOnSuccessListener {
            direccionTextView.setText(it.get("direccion")as String?)
            fullNameShowTextView.setText(it.get("nombreEmpresa")as String?)
            phoneShowTextView.setText(it.get("phone")as String?)
            cuitTextView.setText(it.get("cuit")as String?)
            contactEmailTextView.setText(it.get("emailcontacto")as String?)
            rubroShowTextView.setText(it.get("rubroTrabajo")as String?)
        }
    }
    //override para que el boton hacia atras salga de la app
    private var backPressedTime:Long = 0
    lateinit var backToast: Toast
    override fun onBackPressed() {
        backToast = Toast.makeText(this, "Preciona atras otra vez para salir de la app.", Toast.LENGTH_LONG)
        if (backPressedTime + 2000 > System.currentTimeMillis()) {
            backToast.cancel()
            finishAffinity()
            return
        } else {
            backToast.show()
        }
        backPressedTime = System.currentTimeMillis()
    }
    private fun setup(email:String){
        title = "Perfil"
        emailTextView.text = email




        logOutButton.setOnClickListener{

            //borrar datos los datos anteriores al cerrar sesion
            val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
            prefs.clear()
            prefs.apply()
            FirebaseAuth.getInstance().signOut()
            val Intent = Intent(this, MainActivity::class.java).apply {

            }
            startActivity(Intent)

        }





    }
}