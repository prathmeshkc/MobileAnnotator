package com.pcandroiddev.mobileannotator.util

import android.util.Log
import com.pcandroiddev.mobileannotator.util.Constants.Companion.MASTER_KEYWORDS_LIST


object TranscriptUtil {

//    val MASTER_KEYWORDS_LIST = listOf("cooking", "dinner", "meal", "computer")


    fun getRecognizedWords(transcript: String): List<String> {
        val list = transcript.trim().split(" ")
        Log.d("TranscriptUtil", "getRecognizedWords: $transcript")
        Log.d("TranscriptUtil", "getRecognizedWords: $list")
        return list
    }

    fun getKnownKeywords(recognizedWords: List<String>): List<String> {
        val knownKeywords = mutableListOf<String>()
        recognizedWords.forEachIndexed { index, string ->
            if (string in MASTER_KEYWORDS_LIST) {
                knownKeywords.add(string)
            }
        }
        Log.d("TranscriptUtil", "getKnownKeywords: $knownKeywords")

        return knownKeywords
    }

}
