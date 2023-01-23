package com.example.skygazers

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

class SecondActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)

        val datePrint = findViewById<TextView>(R.id.thisDate)

        val yr = intent?.extras?.getString("year").toString().toInt()
        val mth = intent?.extras?.getString("month").toString().toInt() +1
        val dy = intent?.extras?.getString("day").toString().toInt()
        val string = "date: $dy - $mth - $yr"
        datePrint.text = string
    }
}