package com.example.illuminote

import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.bumptech.glide.Glide
import com.example.illuminote.databinding.ActivityNewNoteBinding
import com.example.illuminote.databinding.ActivitySourcePanelBinding
import com.google.android.material.navigation.NavigationView

class SourcePanelActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySourcePanelBinding
    private lateinit var imageView: ImageView
    private lateinit var launcher: ActivityResultLauncher<PickVisualMediaRequest>
    private lateinit var toggle : ActionBarDrawerToggle
    private lateinit var textView: TextView

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

        binding = ActivitySourcePanelBinding.inflate(layoutInflater)
        setContentView(binding.root)

        enableEdgeToEdge()

        binding.backButton.setOnClickListener {
            finish()
        }

        imageView = findViewById(R.id.imageView)

        textView = findViewById(R.id.addImageButton)

        launcher = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { result: Uri? ->
            if (result == null) {
                Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show()
            } else {
                Glide.with(this).load(result).into(imageView)
                ImageTask(result, this) { resp ->
                    // Append the response to contentEditText
                    val newText = resp // Adjust formatting as needed
                    binding.sourceEditText.setText(newText)
                }.execute()
            }
        }

        binding.addImageButton.setOnClickListener{
            val request = PickVisualMediaRequest.Builder()
                .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly)
                .build()
            launcher.launch(request)
        }

        val drawerLayout : DrawerLayout = findViewById(R.id.drawerLayout)
        val navView : NavigationView = findViewById(R.id.nav_view)


        binding.sourcePanelButton.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.END)
        }

        toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        drawerLayout.addDrawerListener(object : DrawerLayout.DrawerListener {
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {}

            override fun onDrawerOpened(drawerView: View) {
                binding.sourcePanelButton.setImageResource(R.drawable.baseline_arrow_back_24)
            }

            override fun onDrawerClosed(drawerView: View) {
                binding.sourcePanelButton.setImageResource(R.drawable.baseline_density_medium_24)
            }

            override fun onDrawerStateChanged(newState: Int) {}
        })

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        navView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.nav_home -> Toast.makeText(this, "Clicked Home", Toast.LENGTH_SHORT).show()
            }
            true
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (toggle.onOptionsItemSelected(item)){
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}