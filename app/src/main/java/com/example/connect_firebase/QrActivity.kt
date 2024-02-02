package com.example.connect_firebase

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import com.google.firebase.Firebase
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.database
import com.google.zxing.integration.android.IntentIntegrator
import com.google.zxing.integration.android.IntentResult
import okhttp3.Request

class QRScannerActivity : Activity() {

    // Firebase Real Time
    private lateinit var database: DatabaseReference

    val handler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_door)

        // Iniciar el escaneo al abrir la actividad
        iniciarEscaneo()

    }

    private fun iniciarEscaneo() {
        // Iniciar la integración de ZXing para escanear
        val integrator = IntentIntegrator(this)
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)
        integrator.setPrompt("Escanea el código QR")
        integrator.setOrientationLocked(false) // Esto permite la rotación automática
        integrator.setBeepEnabled(false)
        integrator.initiateScan()
    }

    // Método llamado después de que se complete el escaneo
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        // Inicializar Firebase Realtime Database
        database = Firebase.database.reference

        val resultadoEscaneo = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (resultadoEscaneo != null) {
            if (resultadoEscaneo.contents != null) {
                // El código QR fue escaneado exitosamente
                val contenidoQR = resultadoEscaneo.contents
                Toast.makeText(this, "Contenido del QR: $contenidoQR", Toast.LENGTH_LONG).show()

                // Procesar el contenido del QR según tus necesidades
                database.child(contenidoQR).get().addOnSuccessListener {
                    database.child(contenidoQR).setValue(1)
                    Request.Builder().url("https://192.168.0.177/data=1")

                    handler.postDelayed({
                        database.child(contenidoQR).setValue(0)
                        Request.Builder().url("https://192.168.0.177/data=0")
                    }, 3000)

                }.addOnFailureListener {
//                    Log.e("firebase", "Error getting data", it)
                }




            } else {
                // Escaneo cancelado o fallido
                Toast.makeText(this, "Escaneo cancelado", Toast.LENGTH_LONG).show()
            }
            finish() // Cierra la actividad después del escaneo
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }
}
