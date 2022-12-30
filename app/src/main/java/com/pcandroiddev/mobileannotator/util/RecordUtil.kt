package com.pcandroiddev.mobileannotator.util

import android.media.MediaRecorder
import android.os.Build
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.annotation.RequiresApi
import kotlinx.coroutines.*
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

object RecordUtil {

    var isStarted: Boolean = false

//    private var fileName: String = ""

    private fun getFileNameStamp(): String {
        /* val calendar = Calendar.getInstance()

        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val month = calendar.get(Calendar.MONTH) + 1
        val year = calendar.get(Calendar.YEAR)

        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)



        return "$day$month$year" + "_" + "$hour$minute"
*/

        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("MMddyyyy_hhmma")
        return dateFormat.format(calendar.time)

    }

    private var mr: MediaRecorder? = null
    private lateinit var file: File


    @RequiresApi(Build.VERSION_CODES.O)
    fun startRecording(duration: Long, onComplete: (file: File, isStarted: Boolean) -> Unit) {
        mr = MediaRecorder()
        mr?.setAudioSource(MediaRecorder.AudioSource.MIC)
        mr?.setOutputFormat(MediaRecorder.OutputFormat.AMR_WB)
        mr?.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_WB)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val path =
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC)
//                fileName = "Rec_${getFileNameStamp()}.wav"
//                file = File(path, fileName)
                file = File(path, "Rec_${getFileNameStamp()}.wav")
                path.mkdirs()

                mr?.setOutputFile(file)
                mr?.prepare()
                mr?.start()
                isStarted = true
                Log.d("RecordUtil", "startRecording: Recording Started ")


            } catch (e: IllegalStateException) {
                e.printStackTrace()
                isStarted = false
                Log.d(
                    "RecordUtil",
                    "startRecording: Recording Exception Caught ${e.printStackTrace()} "
                )

            } catch (e: IOException) {
                e.printStackTrace()
                isStarted = false
                Log.d(
                    "RecordUtil",
                    "startRecording: Recording Exception Caught ${e.printStackTrace()} "
                )

            }

            Handler(Looper.getMainLooper()).postDelayed({
                stopRecording()
                onComplete(file, isStarted, )
            }, duration)

        }


    }

    fun stopRecording() {

        if (isStarted) {
            isStarted = false
            mr?.stop()
            mr?.reset()
            mr?.release()
            mr = null
            Log.d("RecordUtil", "startRecording: Recording Stopped!! ")
        } else {
            Log.d("RecordUtil", "stopRecording: MediaRecorder was not started!")
        }

    }

 /*   fun getFileName(): String {
        return fileName
    }
*/
}