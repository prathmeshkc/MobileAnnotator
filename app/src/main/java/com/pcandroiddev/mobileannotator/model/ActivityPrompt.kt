package com.pcandroiddev.mobileannotator.model

data class ActivityPrompt(
    val time: String,
    val recognizedWords: List<String>,
    val knownKeywordsRecognized: List<String>,
    val pathToAudioClip: String
)

