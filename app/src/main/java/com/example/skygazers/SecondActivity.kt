package com.example.skygazers

import android.Manifest
import android.content.Context
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.camera2.CameraManager
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.skygazers.databinding.ActivitySecondBinding
import com.google.android.gms.location.*
import java.util.concurrent.ExecutorService


class SecondActivity : AppCompatActivity() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val viewModel: SecondActivityViewModel by viewModels()
    private lateinit var cameraExecutor: ExecutorService
    private lateinit var viewBinding: ActivitySecondBinding
    private final val TAG = "StarGazersApp"
    public lateinit var Sun: SunObject

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivitySecondBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)


    }




    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        // get year, month, day from user in first activity
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
            Log.d("checking permissions", "requesting permissions")
            requestPermissions(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                ), 1
            )
            return
        } else {
            fusedLocationClient.requestLocationUpdates(
                LocationRequest.create(), object :
                    LocationCallback() {
                    // successfully got location permissions, update viewModel with location data
                    override fun onLocationResult(p0: LocationResult) {
                        val location = p0.lastLocation
                        //viewModel.updateLatLong(location, year, month, day)
                        Sun = SunObject(location, year, month, day, 0)
                    }
                },
                Looper.getMainLooper()
            )
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }
}
