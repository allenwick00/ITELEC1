package com.example.sceneit; // Package name for the app

import android.content.SharedPreferences; // Used to save/load high score
import android.media.MediaPlayer; // For playing audio clips
import android.net.Uri; // For accessing video resources
import android.os.Bundle; // For activity state
import android.text.Editable; // For TextWatcher
import android.text.TextWatcher; // To listen for changes in EditText
import android.view.View; // For click listeners
import android.view.animation.Animation; // Animation class
import android.view.animation.AnimationUtils; // Load animations from resources
import android.widget.EditText; // Input field for guesses
import android.widget.ImageButton; // Buttons with images
import android.widget.TextView; // To display text like score and quotes
import android.widget.VideoView; // To play video clips
import android.widget.Toast; // To display short messages

import androidx.appcompat.app.AppCompatActivity; // Base activity class

import java.util.Random; // Random number generator

public class GameActivity extends AppCompatActivity {

    // --- UI Elements ---
    private TextView quoteText, scoreText, highScoreText; // Display quote, current score, high score
    private EditText guessInput; // Input field for user's guess
    private ImageButton submitGuessButton, hintButton, restartButton, audioButton, btnBack; // Buttons
    private VideoView movieVideoView; // Video player for movie clips

    // --- Game state ---
    private int score = 0; // Current score
    private int highScore = 0; // Stored high score
    private int hintsUsedThisRound = 0; // Track hints used per round
    private final int MAX_HINTS_PER_ROUND = 3; // Max hints allowed
    private String correctMovie; // Full correct movie name
    private String correctMovieCleaned; // Correct movie without spaces
    private String currentVideo; // Current video identifier

    // --- Quotes, Movies, Videos arrays ---
    private String[] quotes = { /* Quotes displayed to the user */ };
    private String[] movies = { /* Corresponding movie titles */ };
    private String[] videos = { /* Corresponding video resource names */ };

    private Random random = new Random(); // Random for selecting quotes/videos

    // --- Audio & click sound ---
    private MediaPlayer audioPlayer; // Plays the movie audio
    private MediaPlayer clickSound; // Plays button click sound
    private boolean isAudioPlaying = false; // Track if audio is playing

    // --- Animations ---
    private Animation quotePulseAnimation; // Animation for quote text
    private Animation videoInAnim, videoOutAnim; // Animations for video transitions

    // --- SharedPreferences for high score ---
    private SharedPreferences prefs; // Store high score
    private static final String PREFS_NAME = "game_prefs"; // Name for SharedPreferences
    private static final String HIGH_SCORE_KEY = "high_score"; // Key for storing high score

    @Override
    protected void onCreate(Bundle savedInstanceState) { // Called when activity starts
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game); // Set layout file

        // --- Initialize UI elements ---
        quoteText = findViewById(R.id.quoteText); // Quote display
        scoreText = findViewById(R.id.scoreText); // Score display
        highScoreText = findViewById(R.id.highScoreText); // High score display
        guessInput = findViewById(R.id.guessInput); // User input field
        submitGuessButton = findViewById(R.id.submitGuessButton); // Submit guess button
        hintButton = findViewById(R.id.hintButton); // Hint button
        restartButton = findViewById(R.id.restartButton); // Restart game button
        audioButton = findViewById(R.id.audioButton); // Play/Pause audio button
        movieVideoView = findViewById(R.id.movieVideoView); // Video player
        btnBack = findViewById(R.id.btnBack); // Back button

        // --- Load click sound ---
        clickSound = MediaPlayer.create(this, R.raw.button_click); // Load button click sound

        // --- Load animations ---
        quotePulseAnimation = AnimationUtils.loadAnimation(this, R.anim.pulse); // Pulse effect for quotes
        videoInAnim = AnimationUtils.loadAnimation(this, android.R.anim.fade_in); // Video fade in
        videoOutAnim = AnimationUtils.loadAnimation(this, android.R.anim.fade_out); // Video fade out
        quoteText.startAnimation(quotePulseAnimation); // Start pulse animation

        // --- SharedPreferences setup ---
        prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE); // Open preferences file
        highScore = prefs.getInt(HIGH_SCORE_KEY, 0); // Load high score
        updateHighScoreText(); // Display high score

        // --- Start first round ---
        nextRound(); // Load first quote/video

        // --- Button listeners ---
        submitGuessButton.setOnClickListener(v -> { // Submit guess button
            animateButtonPress(v); // Animate button
            playClickSound(); // Play sound
            checkGuess(); // Check user's input
        });

        hintButton.setOnClickListener(v -> { // Hint button
            animateButtonPress(v);
            playClickSound();
            giveHint(); // Give hint
        });

        restartButton.setOnClickListener(v -> { // Restart button
            animateButtonPress(v);
            playClickSound();
            resetGame(); // Reset game
        });

        audioButton.setOnClickListener(v -> { // Audio button
            animateButtonPress(v);
            toggleAudio(); // Play/pause audio
        });

        btnBack.setOnClickListener(v -> finish()); // Back button closes activity

        // --- Listen for input changes (currently unused) ---
        guessInput.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(Editable s) {}
        });
    }

    // --- Play click sound safely ---
    private void playClickSound() {
        if (clickSound != null) clickSound.start();
    }

    // --- Animate button press ---
    private void animateButtonPress(View button) {
        button.animate()
                .scaleX(0.95f).scaleY(0.95f).setDuration(100)
                .withEndAction(() -> button.animate()
                        .scaleX(1f).scaleY(1f).setDuration(100).start())
                .start();
    }

    // --- Reset game ---
    private void resetGame() {
        score = 0; // Reset score
        updateScoreText(); // Update UI
        nextRound(); // Start new round
    }

    // --- Load next quote/video ---
    private void nextRound() {
        int index = random.nextInt(quotes.length); // Pick random index
        currentVideo = videos[index]; // Set current video
        correctMovie = movies[index]; // Set correct movie
        correctMovieCleaned = correctMovie.replace(" ", ""); // Remove spaces for comparison
        hintsUsedThisRound = 0; // Reset hints

        guessInput.setText(""); // Clear input
        guessInput.setEnabled(true); // Enable typing
        guessInput.setTextColor(0xFF000000); // Reset color

        quoteText.setText(quotes[index]); // Show quote
        quoteText.setVisibility(View.VISIBLE); // Make quote visible

        if (movieVideoView != null) { // Hide video if visible
            movieVideoView.setVisibility(View.GONE);
            movieVideoView.stopPlayback();
        }

        // Reset audio
        if (audioPlayer != null) {
            audioPlayer.release(); // Release resources
            audioPlayer = null;
            audioButton.setImageResource(R.drawable.ic_play); // Reset icon
            isAudioPlaying = false; // Reset flag
        }

        updateScoreText(); // Update score UI
    }

    // --- Give hint ---
    private void giveHint() {
        if (hintsUsedThisRound >= MAX_HINTS_PER_ROUND) { // Max hints reached
            Toast.makeText(this, "No more hints this round!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (score < 5) { // Not enough points
            Toast.makeText(this, "Not enough score for a hint!", Toast.LENGTH_SHORT).show();
            return;
        }

        score -= 5; // Deduct points
        hintsUsedThisRound++; // Increment hint count

        String currentInput = guessInput.getText().toString();
        int nextIndex = currentInput.length();

        while (nextIndex < correctMovie.length() && correctMovie.charAt(nextIndex) == ' ') nextIndex++; // Skip spaces

        if (nextIndex < correctMovie.length()) { // Reveal next letter
            nextIndex++;
            guessInput.setText(correctMovie.substring(0, nextIndex));
            guessInput.setSelection(nextIndex); // Move cursor
            guessInput.setTextColor(0xFFFFFF00); // Highlight
            guessInput.postDelayed(() -> guessInput.setTextColor(0xFF000000), 150); // Reset color
        }

        updateScoreText(); // Update UI
    }

    // --- Play or pause audio ---
    private void toggleAudio() {
        if (audioPlayer == null) { // Initialize if null
            int audioResId = getResources().getIdentifier(currentVideo, "raw", getPackageName());
            if (audioResId != 0) audioPlayer = MediaPlayer.create(this, audioResId);
            else return;
        }

        if (isAudioPlaying) { // Pause
            audioPlayer.pause();
            audioButton.setImageResource(R.drawable.ic_play);
        } else { // Play
            audioPlayer.start();
            audioButton.setImageResource(R.drawable.ic_pause);
            audioPlayer.setOnCompletionListener(mp -> { // Reset icon when finished
                audioButton.setImageResource(R.drawable.ic_play);
                isAudioPlaying = false;
            });
        }
        isAudioPlaying = !isAudioPlaying; // Toggle flag
    }

    // --- Check user guess ---
    private void checkGuess() {
        String guess = guessInput.getText().toString().trim().replace(" ", "");
        if (guess.equalsIgnoreCase(correctMovieCleaned)) { // Correct
            guessInput.setTextColor(0xFF4CAF50); // Green color
            score += 10; // Add points
            updateScoreText();

            if (score > highScore) { // New high score
                highScore = score;
                saveHighScore();
                updateHighScoreText();
            }

            playMovieVideo(); // Show video
            guessInput.setEnabled(false); // Disable input
        } else {
            Toast.makeText(this, "Wrong guess!", Toast.LENGTH_SHORT).show(); // Incorrect
        }
    }

    // --- Play movie video ---
    private void playMovieVideo() {
        int videoResId = getResources().getIdentifier(currentVideo, "raw", getPackageName());
        if (videoResId == 0 || movieVideoView == null) return; // Safety check

        quoteText.setVisibility(View.GONE); // Hide quote
        movieVideoView.setVisibility(View.VISIBLE); // Show video
        movieVideoView.startAnimation(videoInAnim); // Fade in

        Uri videoURI = Uri.parse("android.resource://" + getPackageName() + "/" + videoResId);
        movieVideoView.setVideoURI(videoURI); // Set video

        movieVideoView.setOnPreparedListener(mp -> movieVideoView.start()); // Start when ready

        movieVideoView.setOnCompletionListener(mp -> { // After video ends
            movieVideoView.startAnimation(videoOutAnim); // Fade out
            videoOutAnim.setAnimationListener(new Animation.AnimationListener() {
                @Override public void onAnimationStart(Animation animation) {}
                @Override public void onAnimationEnd(Animation animation) {
                    movieVideoView.setVisibility(View.GONE); // Hide video
                    quoteText.setVisibility(View.VISIBLE); // Show next quote
                    nextRound(); // Next round
                }
                @Override public void onAnimationRepeat(Animation animation) {}
            });
        });
    }

    // --- Update score text ---
    private void updateScoreText() {
        scoreText.setText("Score: " + score);
    }

    // --- Update high score text ---
    private void updateHighScoreText() {
        highScoreText.setText("High Score: " + highScore);
    }

    // --- Save high score ---
    private void saveHighScore() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(HIGH_SCORE_KEY, highScore);
        editor.apply();
    }

    // --- Release resources on destroy ---
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (movieVideoView != null) movieVideoView.stopPlayback(); // Stop video
        if (audioPlayer != null) audioPlayer.release(); // Release audio
        if (clickSound != null) clickSound.release(); // Release click sound
    }
}
