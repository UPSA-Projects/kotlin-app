package com.example.connect_firebase

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.example.connect_firebase.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await


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
        lifecycleScope.launch {
            try {
                // Iniciar sesión con Firebase Authentication
                val task = auth.signInWithEmailAndPassword(email, password).await()

                // Si el inicio de sesión es exitoso
                showToast("Inicio de sesión exitoso")

                // Obtener el ID del grupo
                val groupId = findGroupByEmail(email)

                // Puedes crear un Intent para ir a la nueva actividad
                val intent = Intent(this@MainActivity, DoorActivity::class.java)
                intent.putExtra("email", email)
                intent.putExtra("groupId", groupId)
                startActivity(intent)
                finish() // Cierra la actividad actual si no deseas que el usuario vuelva atrás con el botón de retroceso
            } catch (e: Exception) {
                // Si hay un error en el inicio de sesión
                showToast("Error al iniciar sesión: $e")
            }
        }
    }

    // Función para encontrar el ID del grupo por correo electrónico
    suspend fun findGroupByEmail(userEmail: String): String? {
        return try {
            // Referencia a la colección 'Group'
            val groupCollection = FirebaseFirestore.getInstance().collection("group")

            // Obtener todos los documentos de la colección 'Group'
            val groupQuerySnapshot = groupCollection.get().await()

            // Iterar sobre los documentos de 'Group'
            for (groupDoc in groupQuerySnapshot.documents) {
                // Obtener la referencia de la subcolección 'User' dentro de cada documento 'Group'
                val userCollection = groupDoc.reference.collection("user")

                // Buscar si el correo electrónico existe en la subcolección 'User'
                val userQuerySnapshot = userCollection.whereEqualTo("email", userEmail).get().await()

                // Si se encuentra el correo electrónico, devolver el ID del grupo
                if (userQuerySnapshot.documents.isNotEmpty()) {
                    return groupDoc.id
                }
            }

            // Si el correo electrónico no se encuentra en ningún grupo
            null
        } catch (e: Exception) {
            println("Error al buscar el grupo por correo electrónico: $e")
            null
        }
    }

    private fun showToast(message: String) {
        // Mostrar un Toast con el mensaje proporcionado
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

}