package com.abad.avisoscampus.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class ModoAvionReceiver(
    private val alCambiar: (Boolean) -> Unit
) : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        // onReceive debe ser corto: solo lee el dato y lo entrega
        if (intent.action == Intent.ACTION_AIRPLANE_MODE_CHANGED) {
            alCambiar(intent.getBooleanExtra("state", false))
        }
    }
}
