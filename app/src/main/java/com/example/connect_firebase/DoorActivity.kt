package com.example.connect_firebase

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.google.firebase.database.getValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.Objects
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

class DoorActivity : AppCompatActivity() {

    private var isDoorOpen = false

    // Firebase Real Time
    private lateinit var database: DatabaseReference
    private lateinit var valueEventListener: ValueEventListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_door)

        // Recuperar los datos de la intención
        val email = intent.getStringExtra("email")
        val groupId = intent.getStringExtra("groupId")

        // Obtener referencias a los TextViews en el layout
        val textViewEmail: TextView = findViewById(R.id.textViewEmail)
        val textViewGroupId: TextView = findViewById(R.id.textViewGroupId)

        // Establecer los valores en los TextViews
        textViewEmail.text = "Email: $email"
        textViewGroupId.text = "Group ID: $groupId"

        // Inicializar Firebase Realtime Database
        database = Firebase.database.reference

        // Crear el listener
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Get Post object and use the values to update the UI
                val post = dataSnapshot.getValue<Int>()
                isDoorOpen = post == 1
                toggleDoor(isDoorOpen)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
//                Log.w(TAG, "loadPost:onCancelled", databaseError.toException())
            }
        }

        // Agregar el listener a la referencia de la base de datos
        if (groupId != null) {
            database.child(groupId).addValueEventListener(postListener)
        }

        // Toggle Door Manually
        findViewById<Button>(R.id.buttonToggle).setOnClickListener {
            isDoorOpen = !isDoorOpen
            if (groupId != null) {
                database.child(groupId).setValue(if (isDoorOpen) 1 else 0)
                GlobalScope.launch(Dispatchers.IO) {
                    try {

                        val client = OkHttpClient()
                        val request = Request.Builder().url("http://192.168.0.177/data=${if (isDoorOpen) 1 else 0}").build()
                        client.newCall(request).execute()

                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }
        }

        findViewById<Button>(R.id.buttonQr).setOnClickListener {
            // Puedes crear un Intent para ir a la nueva actividad
            val intent = Intent(this@DoorActivity, QRScannerActivity::class.java)
            intent.putExtra("email", email)
            intent.putExtra("groupId", groupId)
            startActivity(intent)
//            finish()
        }

        findViewById<Button>(R.id.buttonSalir).setOnClickListener {
            // Puedes crear un Intent para ir a la actividad principal (MainActivity)
            val intent = Intent(this@DoorActivity, MainActivity::class.java)
            startActivity(intent)
            finish()  // Cierra la actividad actual
        }

    }

    fun toggleDoor(isDoorOpenT : Boolean) {
        // Actualizar la imagen de la puerta
        val doorImage = findViewById<ImageView>(R.id.imageViewDoor)
        doorImage.setImageResource(if (isDoorOpenT) R.drawable.door_open else R.drawable.door_close)

        // Actualizar el texto y color del botón
        val toggleButton = findViewById<Button>(R.id.buttonToggle)
        toggleButton.text = if (isDoorOpenT) "Cerrar Puerta" else "Abrir Puerta"
        toggleButton.backgroundTintList = ColorStateList.valueOf(
            ContextCompat.getColor(this, if (isDoorOpenT) R.color.doorOpen else R.color.doorClosed)
        )
    }

}
