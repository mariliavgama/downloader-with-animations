package com.udacity

import android.Manifest.permission.POST_NOTIFICATIONS
import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
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
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.udacity.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private var downloadID: Long = 0

    private lateinit var notificationManager: NotificationManager
    private var selectedUrl: String? = null
    private var selectedRepository: String = ""
    private var notificationSuccessful = false
    private var requestPermissionLauncher: ActivityResultLauncher<String>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        notificationManager = ContextCompat.getSystemService(
            this,
            NotificationManager::class.java
        ) as NotificationManager

        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        binding.radioGroup.setOnCheckedChangeListener { group, checkedId ->
            val selectedRadioButton: RadioButton = findViewById(checkedId)
            selectedUrl = selectedRadioButton.tag.toString() // Get the URL from the selected radio button
            selectedRepository = selectedRadioButton.text.toString()
            // If the user selects a new radio, complete the animation to display the Download Button
            binding.customButton.completeAnimation()
        }

        binding.customButton.setOnClickListener {
            if (selectedUrl == null) {
                Toast.makeText(this, getString(R.string.select_url), Toast.LENGTH_SHORT).show()
            } else {
                download(selectedUrl ?: "", selectedRepository)
                binding.customButton.startAnimation()
            }
        }

        createChannel()
    }

    override fun onDestroy() {
        super.onDestroy()
        requestPermissionLauncher = null
    }

    override fun onStart() {
        super.onStart()
        requestPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                // Permission granted, proceed with using POST_NOTIFICATIONS
                if (notificationSuccessful) {
                    sendNotificationSuccess()
                } else {
                    sendNotificationFail()
                }
            } else {
                // Permission denied, show a message
                Toast.makeText(this, getString(R.string.permission_needed_message), Toast.LENGTH_SHORT).show()
            }
        }
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

                context?.let {
                    when (status) {
                        DownloadManager.STATUS_SUCCESSFUL -> {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                notificationSuccessful = true
                                requestPermissionLauncher?.launch(POST_NOTIFICATIONS)
                            } else {
                                sendNotificationSuccess()
                            }
                        } else -> {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                notificationSuccessful = false
                                requestPermissionLauncher?.launch(POST_NOTIFICATIONS)
                            } else {
                                sendNotificationFail()
                            }
                        }
                    }
                }
            }

            // After the download completes, wait 1 second and complete the animation
            val coroutineScope = CoroutineScope(Dispatchers.Main)
            coroutineScope.launch {
                delay(1000)
                binding.customButton.completeAnimation()
                cancel()
            }
        }
    }

    private fun sendNotificationSuccess() {
        notificationManager.sendNotification(
            R.drawable.ic_success,
            getText(R.string.notification_description_success).toString(),
            selectedRepository,
            R.string.status_success,
            this
        )
    }

    private fun sendNotificationFail() {
        notificationManager.sendNotification(
            R.drawable.ic_fail,
            getText(R.string.notification_description_failure).toString(),
            selectedRepository,
            R.string.status_fail,
            this
        )
    }

    private fun download(url: String, selectedRepository: String) {
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
                NotificationManager.IMPORTANCE_DEFAULT
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