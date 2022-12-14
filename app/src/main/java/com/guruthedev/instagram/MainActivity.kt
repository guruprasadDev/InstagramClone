package com.guruthedev.instagram

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import com.google.android.material.snackbar.Snackbar
import com.guruthedev.instagram.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    val CHANNEL_ID = "channelId"
    val CHANNEL_NAME = "channelName"
    val NOTIF_ID = 0


    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        createNotificationChannel()
        updatedPendingIntent()

        val cm: ConnectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val builder: NetworkRequest.Builder = NetworkRequest.Builder()

        cm.registerNetworkCallback(
            builder.build(),
            object : ConnectivityManager.NetworkCallback() {
                @SuppressLint("ShowToast")
                override fun onAvailable(network: Network) {
                    Log.i("MainActivity", "onAvailable!")
                    // check if NetworkCapabilities has TRANSPORT_WIFI
                    val isWifi: Boolean = cm.getNetworkCapabilities(network)?.hasTransport(
                        NetworkCapabilities.TRANSPORT_WIFI
                    ) == true
                }

                override fun onLost(network: Network) {
                    Log.i("MainActivity", "onLost!")
                    val snack = Snackbar.make(
                        binding.container,
                        "Connect to Internet",
                        Snackbar.LENGTH_LONG
                    )
                    snack.show()
                }
            }
        )
    }

    fun navigateTo(actionId: Int) {
        findNavController(R.id.main_nav_host_container).navigate(actionId)
    }

    fun updateBottomNavVisibility(show: Boolean) {
        binding.navView.visibility = if (show) VISIBLE else GONE
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun updatedPendingIntent() {
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = TaskStackBuilder.create(this).run {
            addNextIntentWithParentStack(intent)
            getPendingIntent(
                0,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT // setting the mutability flag
            )
        }
        val notify = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(getString(R.string.context_title))
            .setContentText(getString(R.string.context_text))
            .setSmallIcon(R.drawable.insta)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .build()
        val notificationManager = NotificationManagerCompat.from(this)

//        binding.notificationBtn.setOnClickListener {
//            notificationManager.notify(NOTIF_ID, notify)
//        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                lightColor = Color.BLUE
                enableLights(true)
            }
            val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }
}