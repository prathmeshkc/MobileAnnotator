package com.pcandroiddev.mobileannotator.util

import android.os.Environment
import android.util.Log
import com.opencsv.CSVWriter
import com.pcandroiddev.mobileannotator.model.ActivityPrompt
import java.io.File
import java.io.FileWriter


object CSVUtil {

    fun writeToCSV(promptData: ActivityPrompt) {
        val path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
        val file = File(path, "promptData.csv")
        path.mkdirs()
        val fileWriter = FileWriter(file, true)
        val csvWriter = CSVWriter(fileWriter)
        val data = arrayOf(
            promptData.time,
            promptData.recognizedWords.joinToString(","),
            promptData.knownKeywordsRecognized.joinToString(","),
            promptData.pathToAudioClip
        )
        Log.d("CSVUtil", "writeToCSV: ${data.contentToString()}")
        csvWriter.writeNext(data)
        csvWriter.close()
    }


}
