package com.example.androiduilab

import android.os.Bundle
import android.view.View
import android.widget.*
import android.widget.ViewSwitcher.ViewFactory
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {

    private lateinit var imageSwitcher: ImageSwitcher
    private var imageIndex = 0
    private val images = intArrayOf(
        R.drawable.image1,
        R.drawable.image2
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        imageSwitcher = findViewById(R.id.imageSwitcher)
        val buttonSwitchImage = findViewById<Button>(R.id.buttonSwitchImage)
        val textViewMessage = findViewById<TextView>(R.id.textViewMessage)

        // Set up ImageSwitcher factory
        imageSwitcher.setFactory(ViewFactory {
            val imageView = ImageView(this)
            imageView.scaleType = ImageView.ScaleType.CENTER_CROP
            imageView.layoutParams = ImageSwitcher.LayoutParams(
                ImageSwitcher.LayoutParams.MATCH_PARENT,
                ImageSwitcher.LayoutParams.MATCH_PARENT
            )
            imageView
        })

        // Set initial image
        imageSwitcher.setImageResource(images[imageIndex])

        // Animation (optional)
        imageSwitcher.setInAnimation(this, android.R.anim.fade_in)
        imageSwitcher.setOutAnimation(this, android.R.anim.fade_out)

        // Button click to switch images
        buttonSwitchImage.setOnClickListener {
            imageIndex = (imageIndex + 1) % images.size
            imageSwitcher.setImageResource(images[imageIndex])
            textViewMessage.text = "Image ${imageIndex + 1} displayed"
        }

        // Handle system bar insets (edge-to-edge)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}
