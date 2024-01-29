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

class DoorActivity : AppCompatActivity() {

    private var isDoorOpen = false

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
    }

    fun toggleDoor(view: View) {
        // Cambiar el estado de la puerta
        isDoorOpen = !isDoorOpen

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