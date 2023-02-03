package com.example.skygazers

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.widget.TextView
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationRequest;

class SecondActivity : AppCompatActivity() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val viewModel: SecondActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        val year = intent?.extras?.getString("year").toString().toInt()
        val month = intent?.extras?.getString("month").toString().toInt()
        val day = intent?.extras?.getString("day").toString().toInt()


        //get latitude and longitude
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ), 1
            )
            return
        } else {
            fusedLocationClient.requestLocationUpdates(
                LocationRequest.create(), object :
                    LocationCallback() {
                    override fun onLocationResult(p0: LocationResult) {
                        val location = p0.lastLocation
                        viewModel.updateLatLong(location, year, month, day)
                    }
                },
                Looper.getMainLooper()
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        val year = intent?.extras?.getString("year").toString().toInt()
        val month = intent?.extras?.getString("month").toString().toInt()
        val day = intent?.extras?.getString("day").toString().toInt()

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.requestLocationUpdates(
                LocationRequest.create(), object :
                    LocationCallback() {
                    override fun onLocationResult(p0: LocationResult) {
                        val location = p0.lastLocation
                        viewModel.updateLatLong(location, year, month, day)
                    }
                },
                Looper.getMainLooper()
            )
        }

    }
}
