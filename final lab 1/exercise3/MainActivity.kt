package com.example.androiduilab

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // Connect views
        val editTextName = findViewById<EditText>(R.id.editTextName)
        val autoCountry = findViewById<AutoCompleteTextView>(R.id.autoCountry)
        val spinnerOptions = findViewById<Spinner>(R.id.spinnerOptions)
        val textViewMessage = findViewById<TextView>(R.id.textViewMessage)

        // 🔹 AutoCompleteTextView setup
        val countries = arrayOf("Philippines", "Japan", "USA", "France", "Brazil")
        val countryAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, countries)
        autoCountry.setAdapter(countryAdapter)

        // 🔹 Spinner setup
        val options = arrayOf("Option A", "Option B", "Option C", "Option D", "Option E")
        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, options)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerOptions.adapter = spinnerAdapter

        // Spinner selection listener
        spinnerOptions.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val name = editTextName.text.toString().ifEmpty { "User" }
                val country = autoCountry.text.toString().ifEmpty { "Unknown Country" }
                val option = options[position]
                textViewMessage.text = "Hello, $name from $country! You chose $option."
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                textViewMessage.text = ""
            }
        }

        // Handle system bar insets (Edge-to-Edge layout)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}
