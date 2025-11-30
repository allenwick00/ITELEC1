package com.example.sceneit; // Defines the package name for this app

import android.content.Intent; // Needed to start new activities
import android.media.MediaPlayer; // Used to play sound effects
import android.os.Bundle; // Used for passing data between activity states
import android.view.View; // Needed for handling view clicks
import android.widget.ImageButton; // UI component for image buttons
import androidx.appcompat.app.AppCompatActivity; // Base class for activities using the AppCompat library

public class MainActivity extends AppCompatActivity { // Main activity class

    private ImageButton btnPlayImage, btnSettings, btnInfo; // Declare image buttons for the UI
    private MediaPlayer clickSound; // MediaPlayer instance for button click sound

    @Override
    protected void onCreate(Bundle savedInstanceState) { // Called when the activity is created
        super.onCreate(savedInstanceState); // Call parent class onCreate method
        setContentView(R.layout.activity_main); // Set the layout from activity_main.xml

        // Initialize buttons by finding them in the layout
        btnPlayImage = findViewById(R.id.btnPlayImage);

        // Load the click sound from the raw resources
        clickSound = MediaPlayer.create(this, R.raw.button_click);

        // Set what happens when the Play button is clicked
        btnPlayImage.setOnClickListener(v -> {
            playClickSound(); // Play click sound when button is pressed
            startActivity(new Intent(MainActivity.this, GameActivity.class)); // Open GameActivity
        });
    }

    // Method to play the click sound
    private void playClickSound() {
        if (clickSound != null) { // Check if MediaPlayer is initialized
            clickSound.start(); // Play the sound
        }
    }

    @Override
    protected void onDestroy() { // Called when activity is destroyed
        super.onDestroy(); // Call parent class onDestroy
        if (clickSound != null) clickSound.release(); // Release MediaPlayer resources
    }
}
