package com.example.connect_firebase

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
import java.util.Objects

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
//        database.child("kDkvYHedDoPjuYp6AiMx").setValue(1)

        // Crear el listener
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Get Post object and use the values to update the UI
                val post = dataSnapshot.getValue<Int>()

                if (post != null) {
                    toggleDoor(post)
                }

                // Aquí puedes actualizar tu interfaz de usuario con los datos obtenidos
                // por ejemplo, actualizar una TextView con post.title
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
    }

    fun toggleDoor(isOpen : Int) {

        isDoorOpen = isOpen == 1

        // Cambiar el estado de la puerta
//        isDoorOpen = !isDoorOpen

        // Actualizar la imagen de la puerta
        val doorImage = findViewById<ImageView>(R.id.imageViewDoor)
        doorImage.setImageResource(if (isDoorOpen) R.drawable.door_open else R.drawable.door_close)

        // Actualizar el texto y color del botón
        val toggleButton = findViewById<Button>(R.id.buttonToggle)
        toggleButton.text = if (isDoorOpen) "Cerrar Puerta" else "Abrir Puerta"
        toggleButton.backgroundTintList = ColorStateList.valueOf(
            ContextCompat.getColor(this, if (isDoorOpen) R.color.doorOpen else R.color.doorClosed)
        )
    }
}