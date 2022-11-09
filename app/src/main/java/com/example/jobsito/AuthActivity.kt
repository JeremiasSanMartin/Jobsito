package com.example.jobsito

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import kotlinx.android.synthetic.main.activity_auth.*


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
            Texto.visibility = View.INVISIBLE
            gifCarga.visibility = View.VISIBLE
            textView11.visibility = View.VISIBLE
            logoanimado3.visibility = View.VISIBLE


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

            //comprobacion de autenticacion por google
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
        var datoPersona: String? = ""
        var datoEmpresa: String? = ""

        //comprueba el tipo de usuario o si no tienen ningun tipo
        db.collection("users").document(email).collection("prueba").document("prueba").get()
            .addOnSuccessListener { documento ->
                tipo = documento.data?.get("tipo").toString()

                //comprueba si la Persona ya ingreso todos los datos de su perfil
                db.collection("users").document(email).get()
                    .addOnSuccessListener { documento ->
                        datoPersona = documento.data?.get("dni").toString()

                        //comprueba si la Empresa ya ingreso todos los datos de su perfil
                        db.collection("users").document(email).get()
                            .addOnSuccessListener { documento ->
                                datoEmpresa = documento.data?.get("cuit").toString()
                 //si el tipo es igual a trabajo
                if (tipo == "trabajo") {

                    //si no ingresaste todos los campos del perfil, envia a la actividad modificar
                    if (datoPersona == ""){
                        val modificarIntent = Intent(this, ModificarActivity::class.java).apply {
                            putExtra("email", email)
                        }
                        startActivity(modificarIntent)
                    }else{
                        //en caso contrario envia al inicio
                        val inicioIntent = Intent(this, InicioActivity::class.java).apply {
                            putExtra("email", email)
                    }
                        startActivity(inicioIntent)
                    }
                //si el tipo es igual a empresa
                } else if (tipo == "empresa") {
                    //si no ingresaste todos los campos del perfil, envia a la actividad modificar
                    if (datoEmpresa == ""){
                        val modificarEmIntent = Intent(this, ModificarEmActivity::class.java).apply {
                            putExtra("email", email)
                        }
                        startActivity(modificarEmIntent)
                        //en caso contrario envia al inicio
                    }else{
                        val empresaIntent = Intent(this, InicioEmActivity::class.java).apply {
                            putExtra("email", email)
                        }
                        startActivity(empresaIntent)
                    }
                //si no tenes ningun tipo envia a la activity para elegir uno
                }else if (tipo == "BAN") {
                    val banIntent = Intent(this, BanActivity::class.java).apply {
                    }
                    startActivity(banIntent)
                }else {

                    val chooseIntent = Intent(this, chooseActivity::class.java).apply {
                        putExtra("email", email)
                    }
                    startActivity(chooseIntent)
                }
            }
    }
}


    }
    //override para que el boton hacia atras salga de la app
    private var backPressedTime: Long = 0
    lateinit var backToast: Toast
    override fun onBackPressed() {
        backToast =
            Toast.makeText(this, "Preciona atras otra vez para salir de la app.", Toast.LENGTH_LONG)
        if (backPressedTime + 2000 > System.currentTimeMillis()) {
            backToast.cancel()
            finishAffinity()
            return
        } else {
            backToast.show()
        }
        backPressedTime = System.currentTimeMillis()
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
                               // Toast.makeText(this, "Error en firebase", Toast.LENGTH_SHORT).show()
                            }
                        }

                }
            } catch (e: ApiException) {
                showAlert()
                //Toast.makeText(this, "Error en catch", Toast.LENGTH_SHORT).show()
            }
        }
    }

}