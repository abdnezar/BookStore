package com.androidcamp.bookstore

import android.annotation.SuppressLint
import android.os.AsyncTask
import android.util.Log
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.squareup.okhttp.MediaType
import com.squareup.okhttp.OkHttpClient
import com.squareup.okhttp.Request
import com.squareup.okhttp.RequestBody
import org.json.JSONObject

class FCMService : FirebaseMessagingService() {
    val TAG = "abd"

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.e(TAG, "onNewToken: ${token}")
        FirebaseMessaging.getInstance().subscribeToTopic("all")
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        NotificationManager.sendNotification(applicationContext, message.data["title"]!!, message.data["body"]!!)
    }

    companion object{
        @SuppressLint("StaticFieldLeak")
        fun sendRemoteNotification(title: String, body: String) {
            val url = "https://fcm.googleapis.com/fcm/send"
            val at =
            object : AsyncTask<Void,Void,String>() {
                override fun doInBackground(vararg params: Void?): String {
                    val JSON = MediaType.parse("application/json; charset=utf-8");
                    val client = OkHttpClient()
                    val json = JSONObject()
                    val jsonData = JSONObject()
                    try {
                        jsonData.put("body", body)
                        jsonData.put("title", title)
                        jsonData.put("condition", "!('anytopicyoudontwanttouse' in topics)")
                        json.put("data", jsonData)
//                        json.put("to", "dqJosCIqQLW0F-h9dZ7S8b:APA91bHg4rkQ41x4Bm9U7Hfyfuf8U4rVk0oV02848kezdJz-OtD7V-oRb0L2QRVix_5vyrTya6gqeZ3rxY3ePcmGYzX_wqp2ZEh3rEgYTeYuswIvjB8I3FpHuGE7FIZZgZ5IVVdsKmpF")
                        json.put("to", "/topics/all")
                        val body = RequestBody.create(JSON, json.toString())
                        val request = Request.Builder()
                            .header("Authorization", "key=AAAADgWfWGU:APA91bEFpuliC9PdJ3i1FyW4DwYb6EbcX8Jcz3Cw1EIneg0k1AehHdZ_v9PcseMo8X53cM2Kz8PDiSwQpg9fbvvIYQ1Bf--quL1BNU3TNL0b5LBGLS6YkwQheVvxUFEWKqtzLdkF_bLs")
                            .header("Content-Type", "application/json")
                            .url(url)
                            .post(body)
                            .build()
                        val response = client.newCall(request).execute()
                        val finalResponse: String = response.body().string()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    return "Void"
                }
            }.execute()
        }
    }

}