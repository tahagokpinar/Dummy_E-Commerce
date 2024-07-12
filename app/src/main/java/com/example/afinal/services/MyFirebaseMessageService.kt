package com.example.afinal.services

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessageService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        Log.d("Token", "Token: $token")
    }

    override fun onMessageReceived(message: RemoteMessage) {
        Log.d("Message from", "From: ${message.from}")
        Log.d("Message messageId", "Message ID: ${message.messageId}")
        Log.d("Message data", "Message data: ${message.data}")
        message.notification?.let {
            Log.d("Notification", "Notification body: ${it.body}")
        }
    }

}