package com.example.illuminote

import android.content.Intent
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import com.example.illuminote.databinding.ActivityNewNoteBinding
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import org.json.JSONObject
import java.io.IOException

class NewNoteActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNewNoteBinding
    private lateinit var db: NotesDatabaseHelper

    @RequiresApi(Build.VERSION_CODES.O)
    fun enableEdgeToEdge() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR // This flag ensures dark icons in the status bar
                        or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR // This flag ensures dark icons in the navigation bar
                )
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewNoteBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()

        db = NotesDatabaseHelper(this)
        binding.saveButton.setOnClickListener {
            val title = binding.titleEditText.text.toString()
            val content = binding.contentEditText.text.toString()
            val note = Note(0, title, content)
            db.insertNote(note)
            finish()
            Toast.makeText(this, "Note Saved", Toast.LENGTH_SHORT).show()

        }

        binding.panelButton.setOnClickListener {
            val intent = Intent(this, SourcePanelActivity::class.java)
            startActivity(intent)
        }

        binding.panelButton2.setOnClickListener {
            val content = binding.contentEditText.text.toString()
            // Execute PostTask to send request and receive response
            PostTask(content) { result ->
                // Append the response to contentEditText
                val newText = "$content\n\n$result" // Adjust formatting as needed
                binding.contentEditText.setText(newText)
            }.execute()
        }
    }

    private fun fetchServerResponse(content: String) {
        val client = OkHttpClient()

        val json = JSONObject().apply {
            put("inputText", content)
        }

        val body = RequestBody.create("application/json; charset=utf-8".toMediaType(), json.toString())

        val request = Request.Builder()
            .url("http://192.xxx.xx.xxx:5000/") 
            .get()
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    binding.contentEditText.setText("Error: ${e.message}")
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val responseText = response.body?.string()
                runOnUiThread {
                    binding.contentEditText.setText(responseText)
                }
            }
        })
    }
}






