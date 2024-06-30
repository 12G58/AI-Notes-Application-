package com.example.illuminote

import android.content.Context
import android.net.Uri
import android.os.AsyncTask
import android.util.Log
import okhttp3.*
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.IOException

class ImageTask(
    private val uri: Uri,
    private val context: Context,
    private val callback: (String) -> Unit
) : AsyncTask<Void, Void, String>() {

    override fun doInBackground(vararg params: Void?): String? {
//        val context = MyApplication.context // Replace with your context if needed
        val file = File(context.cacheDir, "temp_image.jpg")

        // Copy the image from the URI to a temp file
        context.contentResolver.openInputStream(uri).use { inputStream ->
            file.outputStream().use { outputStream ->
                inputStream?.copyTo(outputStream)
            }
        }

        val requestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("file", file.name, file.asRequestBody())
            .build()

        val request = Request.Builder()
            .url("http://FLASK_SERVER/detect")
            .post(requestBody)
            .build()

        val client = OkHttpClient()

        return try {
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    Log.e("ImageTask", "Unexpected code $response")
                    null
                } else {
                    response.body?.string()
                }
            }
        } catch (e: IOException) {
            Log.e("ImageTask", "IOException", e)
            null
        }
    }

    override fun onPostExecute(result: String?) {
        result?.let { callback(it) } ?: callback("Error: Could not get a response")
    }
}
