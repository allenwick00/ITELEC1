package com.example.androiduilab

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Connect UI elements
        val buttonClick = findViewById<Button>(R.id.buttonClick)
        val toggleButton = findViewById<ToggleButton>(R.id.toggleButton)
        val imageButton = findViewById<ImageButton>(R.id.imageButton)

        // Button — show Toast when clicked
        buttonClick.setOnClickListener {
            Toast.makeText(this, "Standard Button clicked!", Toast.LENGTH_SHORT).show()
        }

        // ToggleButton — show Toast for ON/OFF state
        toggleButton.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                Toast.makeText(this, "Toggle is ON", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Toggle is OFF", Toast.LENGTH_SHORT).show()
            }
        }

        // ImageButton — show Toast when clicked
        imageButton.setOnClickListener {
            Toast.makeText(this, "Image Button clicked!", Toast.LENGTH_SHORT).show()
        }

        // Handle edge-to-edge layout insets (optional)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}
