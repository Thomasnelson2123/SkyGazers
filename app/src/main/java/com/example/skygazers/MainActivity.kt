package com.example.skygazers

import android.content.Intent
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.DatePicker
import com.example.skygazers.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)



        val submit = findViewById<Button>(R.id.submit)

        submit.setOnClickListener {
            val date = findViewById<DatePicker>(R.id.datePicker)
            val day = date.dayOfMonth.toString()
            val month = date.month.toString()
            val year = date.year.toString()

            val intent = Intent(this, SecondActivity::class.java)
            intent.putExtra("day", day)
            intent.putExtra("month", month)
            intent.putExtra("year", year)
            startActivity(intent)

        }
    }


}