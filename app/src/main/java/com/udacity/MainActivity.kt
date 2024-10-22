package com.udacity

import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.udacity.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private var downloadID: Long = 0

    private lateinit var notificationManager: NotificationManager
    private lateinit var pendingIntent: PendingIntent
    private lateinit var action: NotificationCompat.Action

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        var selectedUrl: String? = null

        binding.radioGroup.setOnCheckedChangeListener { group, checkedId ->
            val selectedRadioButton: RadioButton = findViewById(checkedId)
            selectedUrl = selectedRadioButton.tag.toString() // Get the URL from the selected radio button
        }

        binding.customButton.setOnClickListener {
            if (selectedUrl == null) {
                Toast.makeText(this, getString(R.string.select_url), Toast.LENGTH_SHORT).show()
            } else {
                download(selectedUrl ?: "")
                binding.customButton.startAnimation()
            }
        }

        createChannel()
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1) ?: 0
            //DownloadManager.Query() is used to filter DownloadManager queries
            val query = DownloadManager.Query()

            query.setFilterById(id)
            val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager

            val cursor = downloadManager.query(query)

            if (cursor.moveToFirst()){
                val test = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)

                val status = cursor.getInt(test)

                when (status) {
                    DownloadManager.STATUS_SUCCESSFUL -> {
                        context?.let {
                            val notificationManager = ContextCompat.getSystemService(
                                context,
                                NotificationManager::class.java
                            ) as NotificationManager

                            notificationManager.sendNotification(
                                context.getText(R.string.notification_description).toString(),
                                context
                            )
                        }
                    }
                    DownloadManager.STATUS_FAILED -> {
                        Toast.makeText(context, "testing fail " + id, Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    private fun download(url: String) {
        val request =
            DownloadManager.Request(Uri.parse(url))
                .setTitle(getString(R.string.app_name))
                .setDescription(getString(R.string.app_description))
                .setRequiresCharging(false)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)

        val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        downloadID =
            downloadManager.enqueue(request)// enqueue puts the download request in the queue.
    }

    private fun createChannel() {
        // Create a channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                getString(R.string.download_notification_channel_id),
                getString(R.string.download_notification_channel_name),
                // Change importance
                NotificationManager.IMPORTANCE_HIGH
            ) // Disable badges for this channel
            .apply {
                setShowBadge(false)
            }

            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)
            notificationChannel.description = getString(R.string.download_notification_channel_description)

            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }
}