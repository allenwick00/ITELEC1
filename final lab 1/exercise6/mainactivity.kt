package com.example.androiduilab

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {

    private lateinit var imageSwitcher: ImageSwitcher
    private var imageIndex = 0
    private val images = intArrayOf(R.drawable.image1, R.drawable.image2)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        imageSwitcher = findViewById(R.id.imageSwitcher)
        val buttonSwitchImage = findViewById<Button>(R.id.buttonSwitchImage)
        val textViewMessage = findViewById<TextView>(R.id.textViewMessage)

        // Factory setup
        imageSwitcher.setFactory {
            val imageView = ImageView(this)
            imageView.scaleType = ImageView.ScaleType.CENTER_CROP
            imageView.layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
            imageView.contentDescription = "Displayed image number ${imageIndex + 1}"
            imageView
        }

        // Initial image
        imageSwitcher.setImageResource(images[imageIndex])
        textViewMessage.text = "Image 1 displayed"

        // Button listener
        buttonSwitchImage.setOnClickListener {
            imageIndex = (imageIndex + 1) % images.size
            imageSwitcher.setImageResource(images[imageIndex])
            textViewMessage.text = "Image ${imageIndex + 1} displayed"
            Toast.makeText(this, "Switched to Image ${imageIndex + 1}", Toast.LENGTH_SHORT).show()
        }

        // Handle system insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom)
            insets
        }
    }
}
