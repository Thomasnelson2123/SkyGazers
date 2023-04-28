package com.example.skygazers

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.TextView
import androidx.activity.viewModels
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.skygazers.databinding.ActivitySecondBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationRequest;
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class SecondActivity : AppCompatActivity() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val viewModel: SecondActivityViewModel by viewModels()
    private lateinit var cameraExecutor: ExecutorService
    private lateinit var viewBinding: ActivitySecondBinding
    private final val TAG = "StarGazersApp"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivitySecondBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
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
            ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
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

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }
}
