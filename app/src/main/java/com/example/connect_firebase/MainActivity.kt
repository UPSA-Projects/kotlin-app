package com.example.connect_firebase

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.connect_firebase.R
import com.google.firebase.auth.FirebaseAuth


class MainActivity : AppCompatActivity() {

    // Declarar FirebaeAuth
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Inicializar FirebaseAuth
        auth = FirebaseAuth.getInstance()

        // get reference to all views
        var emailEdt : EditText = findViewById(R.id.idEdtEmail)
        var pwdEdt : EditText = findViewById(R.id.idEdtPassword)
        var loginBtn : Button = findViewById(R.id.idBtnLogin)

        // Configurar el listener del botón
        loginBtn.setOnClickListener {
            // Obtener el usuario y la contraseña ingresados
            val userEmail = emailEdt.text.toString()
            val userPassword = pwdEdt.text.toString()

            // Validar que se hayan ingresado el usuario y la contraseña
            if (userEmail.isNotEmpty() && userPassword.isNotEmpty()) {
                // Llamar a la función para iniciar sesión
                signInUser(userEmail, userPassword)
            } else {
                // Mostrar un mensaje si falta el usuario o la contraseña
                showToast("Por favor, ingresa el usuario y la contraseña.")
            }
        }
    }

    private fun signInUser(email: String, password: String) {
        // Iniciar sesión con Firebase Authentication
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Si el inicio de sesión es exitoso
                    showToast("Inicio de sesión exitoso")

                    // Puedes crear un Intent para ir a la nueva actividad
                    val intent = Intent(this, DoorActivity::class.java)
                    intent.putExtra("email", email) // Puedes agregar información adicional al intent si es necesario
                    startActivity(intent)
                    finish() // Cierra la actividad actual si no deseas que el usuario vuelva atrás con el botón de retroceso
                } else {
                    // Si hay un error en el inicio de sesión
                    showToast("Error al iniciar sesión: ${task.exception?.message}")
                }
            }
    }

    private fun showToast(message: String) {
        // Mostrar un Toast con el mensaje proporcionado
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

}