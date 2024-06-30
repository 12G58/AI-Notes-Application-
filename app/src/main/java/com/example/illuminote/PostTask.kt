package com.example.illuminote

import android.os.AsyncTask
import android.util.Log
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

class PostTask(
    private val inputText: String,
    private val callback: (String) -> Unit
) : AsyncTask<Void, Void, String>() {

    @Deprecated("Deprecated in Java")
    override fun doInBackground(vararg params: Void?): String {
        val apiUrl = "http://192.168.1.19:5000/summarize"
        val url = URL(apiUrl)
        val connection = url.openConnection() as HttpURLConnection

        return try {
            connection.apply {
                requestMethod = "POST"
                doOutput = true
                setRequestProperty("Content-Type", "application/json")
            }

            // Create JSON object with input text
            val jsonInput = JSONObject().apply {
                put("inputText", inputText)
            }

            // Write payload to request body
            OutputStreamWriter(connection.outputStream).apply {
                write(jsonInput.toString())
                flush()
            }

            // Read response
            val response = StringBuilder()
            BufferedReader(InputStreamReader(connection.inputStream)).use { reader ->
                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    response.append(line)
                }
            }

            response.toString()
        } catch (e: Exception) {
            Log.e("PostTask", "Error: ${e.message}")
            ""
        } finally {
            connection.disconnect()
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onPostExecute(result: String) {
        super.onPostExecute(result)
        callback(result)
    }
}

