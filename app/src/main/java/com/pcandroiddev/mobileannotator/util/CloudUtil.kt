package com.pcandroiddev.mobileannotator.util

import android.util.Log
import com.google.auth.oauth2.ServiceAccountCredentials
import com.google.cloud.firestore.FirestoreOptions
import com.google.cloud.storage.Storage
import com.google.cloud.storage.StorageOptions
import com.pcandroiddev.mobileannotator.model.ActivityPrompt

import java.io.InputStream
import java.util.concurrent.TimeUnit

object CloudUtil {

    /*
    private val storage = StorageOptions
        .newBuilder()
        .setCredentials(credentials)
        .build()
        .service

    private val bucket = storage.get("prod-mobileannotator")
*/
    // Set the object-level ACL to allow all users to read the file
//    private val acl = Acl.of(Acl.User.ofAllUsers(), Acl.Role.READER)

    /* private val fireStore = FirestoreOptions.getDefaultInstance().service
     private val collectionRef = fireStore.collection("promptData")

 */

    fun storeToCloud(
        fileName: String,
        byteArray: ByteArray,
        credentialInputStream: InputStream?
    ): String {
        val credentials = ServiceAccountCredentials.fromStream(credentialInputStream)
        val storage = StorageOptions
            .newBuilder()
            .setCredentials(credentials)
            .build()
            .service

        val bucket = storage.get("prod-mobileannotator")
        val blob = bucket.create("recordings/$fileName", byteArray)
        Log.d(
            "CloudUtil",
            "storeToCloud: ${
                blob.signUrl(
                    0,
                    TimeUnit.SECONDS,
                    Storage.SignUrlOption.withContentType()
                )
            }"
        )
        return blob.signUrl(0, TimeUnit.SECONDS, Storage.SignUrlOption.withContentType()).toString()
    }


    fun storeToFireStore(activityPrompt: ActivityPrompt, credentialInputStream: InputStream?) {
        val credentials = ServiceAccountCredentials.fromStream(credentialInputStream)
        val fireStore = FirestoreOptions
            .newBuilder()
            .setCredentials(credentials)
            .build()
            .service

        val collectionRef = fireStore.collection("promptData")


        val task = collectionRef.add(activityPrompt)
        try {
            val result = task.get()
            Log.d("CloudUtil", "storeToFireStore: ${result.id}")
        } catch (e: Exception) {
            Log.d("CloudUtil", "storeToFireStore: ${e.printStackTrace()}")
        }

    }


}