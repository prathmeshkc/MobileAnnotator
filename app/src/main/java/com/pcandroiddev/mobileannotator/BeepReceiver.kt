package com.pcandroiddev.mobileannotator

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.*
import android.util.Log
import androidx.annotation.RequiresApi
import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.speech.v1.*
import com.google.protobuf.ByteString
import com.pcandroiddev.mobileannotator.model.ActivityPrompt
import com.pcandroiddev.mobileannotator.util.CloudUtil
import com.pcandroiddev.mobileannotator.util.RecordUtil
import com.pcandroiddev.mobileannotator.util.TranscriptUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*


class BeepReceiver : BroadcastReceiver() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onReceive(context: Context?, intent: Intent?) {


        val speechClient: SpeechClient by lazy {
            context?.applicationContext?.resources?.openRawResource(R.raw.credentials)
                .use { inputStream ->

                    SpeechClient.create(
                        SpeechSettings.newBuilder()
                            .setCredentialsProvider { GoogleCredentials.fromStream(inputStream) }
                            .build()
                    )

                }
        }

        val mediaPlayer = MediaPlayer.create(context, R.raw.beep)
        mediaPlayer.start()
        mediaPlayer.setOnCompletionListener {
            //Release MediaPlayer
//            mediaPlayer.stop()
//            mediaPlayer.release()

            Log.d("BeepReceiver", "onReceive: Beep Complete")
            Log.d("BeepReceiver", "onReceive: Starting Recording...")

            RecordUtil.startRecording(30000) { file, isStarted ->

                if (!RecordUtil.isStarted) {
                    //Do all the backend work
                    Log.d("BeepReceiver", "onReceive: Recording Complete")
                    Log.d("BeepReceiver", "setOnCompletionListener: ${file.name} $isStarted")


                    CoroutineScope(Dispatchers.IO).launch {
                        val path = Paths.get("/storage/emulated/0/Music/${file.name}")
                        val data =
                            withContext(Dispatchers.IO) {
                                Files.readAllBytes(path)
                            }
                        val audioBytes = ByteString.copyFrom(data)
                        Log.d(
                            "BeepReceiver",
                            "setOnCompletionListener: data: ${File("/storage/emulated/0/Music/${file.name}").readBytes()}"
                        )
                        Log.d("setOnCompletionListener", "AudioBytes:  $audioBytes")

                        val req = RecognizeRequest.newBuilder()
                            .setConfig(
                                RecognitionConfig.newBuilder()
                                    .setEncoding(RecognitionConfig.AudioEncoding.AMR_WB)
                                    .setLanguageCode("en-US")
                                    .setSampleRateHertz(16000)
                                    .build()
                            )
                            .setAudio(
                                RecognitionAudio.newBuilder()
                                    .setContent(audioBytes)
                                    .build()
                            )
                            .build()

                        val response = speechClient.recognize(req)

                        speechClient.close()


                        Log.d("setOnCompletionListener", "Response, count ${response.resultsCount}")
                        val results = response.resultsList
                        val transcriptText = StringBuilder()
                        for (result in results) {
                            val alternative = result.alternativesList[0]
                            transcriptText.append(alternative.transcript + " ")
                        }
                        Log.d("setOnCompletionListener", "Transcription: $transcriptText")


                        /*
                          // Get the transcript from the response
                          val result = response.resultsList.firstOrNull()
                          val transcript = result?.alternativesList?.firstOrNull()?.transcript
                              ?: "No transcript found"
                          Log.d("setOnCompletionListener", "Transcription: $transcript")
                          */


                        //Upload audio to Google Cloud Storage
                        val credentialInputStream =
                            context?.applicationContext?.resources?.openRawResource(R.raw.credentials)
                        val gcloudUrl =
                            CloudUtil.storeToCloud(file.name, data, credentialInputStream)
                        Log.d("setOnCompletionListener", "gcloudUrl: $gcloudUrl")



/*

                        1. Make a val recognizedWords: ArrayList<String>,
                        2. Make a val knownKeywordsRecognized: ArrayList<String>,
                        3. Upload audio to firebase storage: val gcloudUrl = CloudUtil.storeToCloud(file.name, data)
                        4. Get val urlPathToRawAudioClip: String
                        5. Create a document on Firestore: val CloudUtil.storeToFireStore(activityPrompt: ActivityPrompt)
*/

                        //Rec_28122022_0124PM.wav
                        val nameList = file.nameWithoutExtension.split("_")
                        val timeStamp = nameList[1].substring(0,2) + "-" + nameList[1].substring(2,4) + "-" + nameList[1].substring(4) + "  " + nameList[2].substring(0,2) + ":" + nameList[2].substring(2,4) + "  " + nameList[2].substring(4)

                        Log.d(
                            "setOnCompletionListener",
                            "nameSplitList|timeStamp: $nameList | $timeStamp"
                        )
                        val recognizedWords =
                            TranscriptUtil.getRecognizedWords(transcriptText.toString())
                        val knownKeywordsRecognized =
                            TranscriptUtil.getKnownKeywords(recognizedWords = recognizedWords)


                        val activityPrompt = ActivityPrompt(
                            time = timeStamp,
                            recognizedWords = recognizedWords,
                            knownKeywordsRecognized = knownKeywordsRecognized,
                            pathToAudioClip = gcloudUrl
                        )
                        val credentials = context?.applicationContext?.resources?.openRawResource(R.raw.credentials)
                        CloudUtil.storeToFireStore(activityPrompt, credentialInputStream = credentials)
                    }
                }
            }
        }
    }


}
