package com.example.skygazers

import android.content.Context
import android.content.Context.SENSOR_SERVICE
import android.hardware.SensorManager
import android.Manifest
import android.content.pm.PackageManager
import android.content.res.Resources
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.view.WindowManager
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import android.widget.SeekBar
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.skygazers.databinding.FragmentSecondBinding
import java.util.*
import kotlinx.coroutines.launch
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import android.opengl.Matrix
import android.widget.LinearLayout
import android.widget.Toast
import java.io.Console
import kotlin.properties.Delegates


private val PERMISSIONS_REQUIRED = arrayOf(Manifest.permission.CAMERA)

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class SecondFragment : Fragment() {

    private lateinit var timer: Timer
    private var _binding: FragmentSecondBinding? = null
    private val viewModel: SecondActivityViewModel by activityViewModels()
    private lateinit var sensor: Sensors
    private lateinit var accelerometerValuesTextView: TextView
    private lateinit var magneticFieldValuesTextView: TextView

    var isRunning: Boolean = false

    private var lensFacing = CameraSelector.LENS_FACING_BACK
    private var preview: Preview? = null
    private var camera: Camera? = null
    private var cameraProvider: ProcessCameraProvider? = null
    private lateinit var windowManager: WindowManager

    private var sunPos by Delegates.notNull<DoubleArray>()


    private lateinit var cameraExecutor: ExecutorService

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!hasPermissions(requireContext())) {
            // Request camera-related permissions
            requestPermissions(PERMISSIONS_REQUIRED, 10)
        }

    }

    private fun setUpCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        cameraProviderFuture.addListener(Runnable {

            // CameraProvider
            cameraProvider = cameraProviderFuture.get()


            // Build and bind the camera use cases
            bindCameraUseCases()
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentSecondBinding.inflate(inflater, container, false)
        return binding.root
    }

    private fun bindCameraUseCases() {


        val rotation = binding.viewFinder.display.rotation

        // CameraProvider
        val cameraProvider = cameraProvider
            ?: throw IllegalStateException("Camera initialization failed.")

        // CameraSelector
        val cameraSelector = CameraSelector.Builder().requireLensFacing(lensFacing).build()

        // Preview
        preview = Preview.Builder()
            // Set initial target rotation
            .setTargetRotation(rotation)
            .build().also {
                it.setSurfaceProvider(binding.viewFinder.surfaceProvider)
            }


        // Must unbind the use-cases before rebinding them
        cameraProvider.unbindAll()

        try {
            // A variable number of use-cases can be passed here -
            // camera provides access to CameraControl & CameraInfo
            camera = cameraProvider.bindToLifecycle(
                this, cameraSelector, preview)

            // Attach the viewfinder's surface provider to preview use case
        } catch (exc: Exception) {
            Log.e("Sky Gazers Camera", "Use case binding failed", exc)
        }
    }

    override fun onResume() {
        super.onResume()
        sensor.startSensors()
        timer = Timer()
        timer.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                Handler(Looper.getMainLooper()).post {
                    var orientation = sensor.getOrientationValues()
                    binding.orientationTextView.text = "az: " + orientation[0] + " pitch: " + orientation[1] + " roll: " + orientation[2]
                    displaySun()
                    //var accValues = sensor.getAccelerometerValues()
                    //binding.accelerometerValuesTextView.text = "x: " + accValues[0] + " y: " + accValues[1] + " z: " + accValues[2]
                    //var magValues = sensor.getMagneticFieldValues()
                    //binding.magneticFieldValuesTextView.text = "x: " + magValues[0] + " y: " + magValues[1] + " z: " + magValues[2]
                    //displaySun(orientation[0], orientation[1], sunPos[1].toFloat(), sunPos[0].toFloat())
                }
            }
        }, 0, 1000)
    }
//    private fun setUpPosLoop() {
//        setUpButton()
//        GlobalScope.launch{updatePosLoop()}
//    }

//    private fun setUpButton() {
//        binding.posLoopButton.setOnClickListener {
//            if(isRunning){
//                isRunning = !isRunning
//                binding.posLoopButton.text = "Start Loop"
//            } else {
//                isRunning = !isRunning
//                binding.posLoopButton.text = "Stop Loop"
//                GlobalScope.launch{updatePosLoop()}
//            }
//        }
////        binding.buttonSecond.setOnClickListener {
////            isRunning = false;
////            findNavController().navigate(R.id.action_SecondFragment_to_FirstFragment)
////        }
//    }

//    private fun updatePosLoop() {
//        while(isRunning) {
//            //TODO: get sun position based off of location/gyroscope parameters to set X,Y coordinates
//            Thread.sleep(100)
//            val randX = (0..1000).random()
//            val randY = (0..1000).random()
//            binding.sunView.setX(randX.toFloat())
//            binding.sunView.setY(randY.toFloat())
//        }
//    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonSecond.setOnClickListener {
            findNavController().navigate(R.id.action_SecondFragment_to_FirstFragment)
        }
        val seek = binding.seekBar;
        seek?.setOnSeekBarChangeListener(object:
        SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val curTime =seekBar?.progress.toString()
                binding.curNum.text = curTime
                sunPos = viewModel.updateTime(curTime.toInt())
                binding.curAzimuth.text= sunPos.get(1).toString()
                binding.curElevation.text= sunPos.get(0).toString()




            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })


        viewModel.listenLatLong().observe(viewLifecycleOwner) {
            binding.textviewSecond.text = it
            sunPos = viewModel.updateTime(0)
        }

//        setUpPosLoop()

        // Initialize our background executor
        cameraExecutor = Executors.newSingleThreadExecutor()



        // Wait for the views to be properly laid out
        binding.viewFinder.post {


            // Set up the camera and its use cases
            lifecycleScope.launch {
                setUpCamera()
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        sensor.stopSensors()
        _binding = null
        cameraExecutor.shutdown()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        sensor = Sensors(
            requireActivity().getSystemService(SENSOR_SERVICE) as SensorManager
        )
        sensor.startSensors()
    }




    override fun onPause() {
        super.onPause()
        sensor.stopSensors()
    }

    fun displaySun() {
        var azimuth = 0.0
        var elevation = 0.0
        try {
            azimuth = sunPos[1]
            elevation = sunPos[0]
        }
        catch(e: Exception) {

        }
        val R = sensor.getRotationMatrixValues()
        val coords = getScreenCoords(R, azimuth, elevation)
        Log.d("MyTag", "" + coords.first + ", " + coords.second)

        binding.sunPicture.setX(coords.first)
        binding.sunPicture.setY(coords.second)



    }

    fun getScreenCoords(R: FloatArray, azimuth: Double, elevation: Double): Pair<Float, Float> {
        // Convert the azimuth and elevation to radians
        val azimuthRadians = Math.toRadians(azimuth.toDouble())
        val elevationRadians = Math.toRadians(elevation.toDouble())

        // Calculate the vector pointing to the sun in 3D space
        val x = Math.cos(azimuthRadians) * Math.cos(elevationRadians)
        val y = Math.sin(azimuthRadians) * Math.cos(elevationRadians)
        val z = Math.sin(elevationRadians)

        // Create a float array containing the vector coordinates
        val vector = floatArrayOf(x.toFloat(), y.toFloat(), z.toFloat(), 1.0f)

        // Create a new float array to hold the transformed coordinates
        val transformedVector = FloatArray(4)

        // Multiply the rotation matrix by the vector to transform it to the device coordinate system
        Matrix.multiplyMV(transformedVector, 0, R, 0, vector, 0)

        // Normalize the transformed vector by dividing by its W coordinate
        transformedVector[0] /= transformedVector[3]
        transformedVector[1] /= transformedVector[3]
        transformedVector[2] /= transformedVector[3]

        // Get the screen dimensions
        val displayMetrics = Resources.getSystem().displayMetrics
        val screenWidth = displayMetrics.widthPixels
        val screenHeight = displayMetrics.heightPixels

        // Convert the normalized vector coordinates to screen coordinates
        val xScreen = ((transformedVector[0] + 1.0f) / 2.0f * screenWidth)
        val yScreen = ((1.0f - transformedVector[1]) / 2.0f * screenHeight)

        // Return the screen coordinates as a Pair
        return Pair(xScreen, yScreen)
    }



    companion object {

        /** Convenience method used to check if all permissions required by this app are granted */
        fun hasPermissions(context: Context) = PERMISSIONS_REQUIRED.all {
            ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }
    }
}


