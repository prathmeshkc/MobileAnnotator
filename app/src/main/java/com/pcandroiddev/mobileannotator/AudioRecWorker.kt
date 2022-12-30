package com.pcandroiddev.mobileannotator

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.pcandroiddev.mobileannotator.util.RecordUtil

class AudioRecWorker(ctx: Context, params: WorkerParameters) : Worker(ctx, params) {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun doWork(): Result {
        return try {
            Log.d("WorkerClass", "doWork method called")

//            RecordUtil.startRecording()
            Result.success()
        } catch (e: Exception) {
            Log.d("WorkerClass", "doWork: Exception caught ${e.printStackTrace()}")
            e.printStackTrace()
            Result.failure()
        }

    }
}