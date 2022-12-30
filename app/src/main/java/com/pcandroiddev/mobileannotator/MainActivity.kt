package com.pcandroiddev.mobileannotator

import android.Manifest
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.*
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.speech.v1.*
import com.pcandroiddev.mobileannotator.databinding.ActivityMainBinding
import com.pcandroiddev.mobileannotator.util.RecordUtil
import java.util.*
import java.util.concurrent.TimeUnit
import com.google.protobuf.ByteString
import com.pcandroiddev.mobileannotator.util.Constants.Companion.BUFFER_TIME
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths

class MainActivity : AppCompatActivity() {


    private lateinit var binding: ActivityMainBinding

    private val allowPermission =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            it?.let {
                if (it[Manifest.permission.RECORD_AUDIO] == true) {
                    Toast.makeText(this, "Permission Granted To Record", Toast.LENGTH_SHORT).show()
                }
                if (it[Manifest.permission.WRITE_EXTERNAL_STORAGE] == true) {
                    Toast.makeText(
                        this,
                        "Permission Granted To Write to External Storage",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                if (it[Manifest.permission.READ_EXTERNAL_STORAGE] == true) {
                    Toast.makeText(
                        this,
                        "Permission Granted To Read External Storage",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            }
        }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Log.d("MainActivity", "onCreate Called")

        //Call getPermission()
        getPermissionOverO(this)
        loadData()
        binding.swRecord.setOnCheckedChangeListener { _, isChecked ->
            saveData(isChecked)
            if (binding.swRecord.isChecked) {

                    val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

                    val intent = Intent(this, BeepReceiver::class.java)
                    val pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0)

                    alarmManager.setRepeating(
                        AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(),
                        BUFFER_TIME.toLong(), pendingIntent
                    )


            } else {
                val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
                val intent = Intent(this, BeepReceiver::class.java)
                val pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0)
                alarmManager.cancel(pendingIntent)
                RecordUtil.stopRecording()
            }
        }


    }


    //get Permission for recording audio
    private fun getPermissionOverO(context: Context) {

        //check if the permission is granted else ask for permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (
                (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.RECORD_AUDIO
                ) == PackageManager.PERMISSION_GRANTED)
                && (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED)
                && (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED)
            ) {

                Toast.makeText(this, "All Permissions Granted", Toast.LENGTH_SHORT).show()

            } else {
                allowPermission.launch(
                    arrayOf(
                        Manifest.permission.RECORD_AUDIO,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    )
                )

            }
        } else {
            Log.d("MainActivity", "getPermissionOverO: SDK version < O")
        }
    }

    private fun saveData(isChecked: Boolean) {
        val sharedPreferences = getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.apply {
            putBoolean("SWITCH_STATE", isChecked)
        }.apply()
        Toast.makeText(this, "Data Saved: ${binding.swRecord.isChecked}", Toast.LENGTH_SHORT).show()
    }

    private fun loadData() {
        val sharedPreferences = getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)
        val savedSwitchState: Boolean = sharedPreferences.getBoolean("SWITCH_STATE", false)
        binding.swRecord.isChecked = savedSwitchState
    }
/*

    @RequiresApi(Build.VERSION_CODES.M)
    private fun hasActiveInternetConnection(): Boolean {
        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetwork
        val networkCapabilities = connectivityManager.getNetworkCapabilities(activeNetwork)
        return networkCapabilities != null && networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)


    }
*/


}


