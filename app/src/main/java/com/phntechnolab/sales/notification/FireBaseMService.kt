package com.phntechnolab.sales.notification

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class FireBaseMService : FirebaseMessagingService() {
    @Inject
    lateinit var notificationService: NotificationService
    override fun onMessageReceived(message: RemoteMessage) {
        if (message.notification != null) {
            Timber.e(message.notification!!.title.toString())
            notificationService.apply {
                createNotification("Phn Notification")
                showBasicNotification(message.notification!!.title!!, message.notification!!.body!!)
            }
        }
    }
}