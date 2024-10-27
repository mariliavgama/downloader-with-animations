package com.udacity

import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.app.NotificationCompat
import com.udacity.Constants.EXTRA_REPOSITORY
import com.udacity.Constants.EXTRA_STATUS

// Notification ID.
private const val NOTIFICATION_ID = 0

// extension function to send messages
/**
 * Builds and delivers the notification.
 */
fun NotificationManager.sendNotification(@DrawableRes notificationImageResId: Int,
                                         messageBody: String, repository: String, @StringRes statusResId: Int,
                                         applicationContext: Context) {
    // Create the content intent for the notification, which launches this activity

    // Create intent
    val contentIntent = Intent(applicationContext, MainActivity::class.java)
    // Create PendingIntent
    val contentPendingIntent = PendingIntent.getActivity(
        applicationContext,
        NOTIFICATION_ID,
        contentIntent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    // Add style
    val notificationImage = BitmapFactory.decodeResource(
        applicationContext.resources,
        notificationImageResId
    )

    val bigPicStyle = NotificationCompat.BigPictureStyle()
        .bigPicture(notificationImage)
        .bigLargeIcon(null as Bitmap?)

    // See details action
    val detailIntent = Intent(applicationContext, DetailActivity::class.java)

    detailIntent.putExtra(EXTRA_STATUS, statusResId)
    detailIntent.putExtra(EXTRA_REPOSITORY, repository)

    val detailPendingIntent: PendingIntent = TaskStackBuilder.create(applicationContext).run {
        // Add the intent, which inflates the back stack.
        addNextIntentWithParentStack(detailIntent)
        // Get the PendingIntent containing the entire back stack.
        getPendingIntent(0,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
    }

    // Get an instance of NotificationCompat.Builder
    // Build the notification
    val builder = NotificationCompat.Builder(
        applicationContext,
        applicationContext.getString(R.string.download_notification_channel_id)
    )

        // Set title, text and icon to builder
        .setSmallIcon(R.drawable.ic_assistant_black_24dp)
        .setContentTitle(applicationContext
            .getString(R.string.notification_title))
        .setContentText(messageBody)

        // Set content intent
        .setContentIntent(contentPendingIntent)
        .setAutoCancel(true)

        // Add style to builder
        .setStyle(bigPicStyle)
        .setLargeIcon(notificationImage)

        // Add detail action
        .addAction(
            R.drawable.ic_assistant_black_24dp,
            applicationContext.getString(R.string.notification_button),
            detailPendingIntent
        )
        // Set priority
        .setPriority(NotificationCompat.PRIORITY_MIN)
        // Call notify
        notify(NOTIFICATION_ID, builder.build())
}
