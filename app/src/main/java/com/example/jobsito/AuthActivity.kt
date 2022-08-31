package com.example.jobsito

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import kotlinx.android.synthetic.main.activity_auth.*
import pl.droidsonroids.gif.GifImageView
import java.security.Provider


class MainActivity : AppCompatActivity() {
    private val GOOGLE_SIGN_IN = 100
    private val db = FirebaseFirestore.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {


        //splash
        setTheme(R.style.Theme_Jobsito)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        //analytics event
        val analytics = FirebaseAnalytics.getInstance(this)
        val bundle = Bundle()
        bundle.putString("message", "integracion de Firebase completa")
        analytics.logEvent("InitScreen", bundle)

        //remote config
        val configSettings = remoteConfigSettings {
            //cada cuantos segundos se va a intentar recargar la configuracion
            minimumFetchIntervalInSeconds = 10
        }
        //accedemos a firebaseRemoteConfig
        val firebaseConfig = Firebase.remoteConfig
        //accedemos a ella y le asignamos nuestra configuracion
        firebaseConfig.setConfigSettingsAsync(configSettings)
        //configurar valores por defecto de nuestras variables remotas
        firebaseConfig.setDefaultsAsync(
            mapOf(
                "show_error_button" to false,
                "error_button_text" to ""
            )
        )


        //setup
        setup()
        session()
        notification()


    }


    //inicio de sesion una vez comprueba la cuenta de google
    private fun session() {
        val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE)
        val email = prefs.getString("email", null)

        if (email != null) {
            authLayout.visibility = View.INVISIBLE
            imageView2.visibility = View.INVISIBLE
            gifCarga.visibility = View.VISIBLE
            textView11.visibility = View.VISIBLE

            showHome(email)
        }
    }

    //comprueba si tenemos un id de dispositivo unico y en caso de que exista lo imprime en consola
    //nos da un token con el cual podemos enviar notificacion a un unico usuario
    private fun notification() {

        FirebaseInstanceId.getInstance().instanceId.addOnCompleteListener {
            it.result?.token?.let {
                println("este es el token del despositivo ${it}")
            }

        }

    }

    //funcion principal del inicio de la aplicacion
    private fun setup() {

        title = "Bienvenido a Jobsito"
        googleButton.setOnClickListener {

            //conprobacion de autenticacion por google
            val googleConf = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()

            val googleClient = GoogleSignIn.getClient(this, googleConf)
            googleClient.signOut()

            startActivityForResult(googleClient.signInIntent, GOOGLE_SIGN_IN)

        }
    }

    //en caso de error se muestra una alerta
    private fun showAlert() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage("Se ha producido un error autenticando al usuario")
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    //funcion para mostrar la pagina de inicio dependiendo si el usuario es una empresa, una persona, o es nueva en la app
    private fun showHome(email: String) {

        var tipo: String? = ""

        db.collection("users").document(email).collection("prueba").document("prueba").get()
            .addOnSuccessListener { documento ->
                tipo = documento.data?.get("tipo").toString()

                if (tipo == "trabajo") {

                    val inicioIntent = Intent(this, HomeActivity::class.java).apply {
                        putExtra("email", email)
                    }
                    startActivity(inicioIntent)
                } else if (tipo == "empresa") {
                    val empresaIntent = Intent(this, EmpresaActivity::class.java).apply {
                        putExtra("email", email)
                    }
                    startActivity(empresaIntent)
                } else {

                    val chooseIntent = Intent(this, chooseActivity::class.java).apply {
                        putExtra("email", email)
                    }
                    startActivity(chooseIntent)
                }
            }
    }

    //comprobacion de google una vez ingresada la cuenta
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == GOOGLE_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)

            try {


                val account = task.getResult(ApiException::class.java)

                if (account != null) {

                    val credential = GoogleAuthProvider.getCredential(account.idToken, null)

                    FirebaseAuth.getInstance().signInWithCredential(credential)
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                                showHome(account.email ?: "")
                            } else {
                                showAlert()
                            }
                        }

                }
            } catch (e: ApiException) {
                showAlert()
            }
        }
    }

}