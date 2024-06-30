package com.example.illuminote

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import com.example.illuminote.databinding.ActivityUpdateNoteBinding

class UpdateNoteActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUpdateNoteBinding
    private lateinit var db: NotesDatabaseHelper
    private var noteId: Int = -1

    private fun enableEdgeToEdge() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR // This flag ensures dark icons in the status bar
                        or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR // This flag ensures dark icons in the navigation bar
                )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpdateNoteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        enableEdgeToEdge()

        db = NotesDatabaseHelper(this)

        noteId = intent.getIntExtra("note_id", -1)
        Log.d("UpdateNoteActivity", "Note ID received: $noteId")

        if (noteId == -1){
            finish()
            return
        }

        val note = db.getNoteByID(noteId)
        binding.updateTitleEditText.setText(note.title)
        binding.updateContentEditText.setText(note.content)

        binding.updateSaveButton.setOnClickListener{
            val newTitle = binding.updateTitleEditText.text.toString()
            val newContent = binding.updateContentEditText.text.toString()

            val updatedNote = Note(noteId, newTitle, newContent)
            db.updateNote(updatedNote)
            finish()
            Toast.makeText(this, "Changes saved", Toast.LENGTH_SHORT).show()
        }

        binding.updatePanelButton.setOnClickListener{
            val intent = Intent(this,SourcePanelActivity::class.java)
            startActivity(intent)
        }

        binding.updatePanelButton2.setOnClickListener {
            val content = binding.updateContentEditText.text.toString()
            // Execute PostTask to send request and receive response
            PostTask(content) { result ->
                // Append the response to contentEditText
                val newText = "$content\n\n$result" // Adjust formatting as needed
                binding.updateContentEditText.setText(newText)
            }.execute()
        }

    }
}