package com.example.skygazers

import android.content.Context
import android.content.Context.SENSOR_SERVICE
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.Manifest
import android.content.pm.PackageManager
import android.hardware.camera2.CameraManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat.getSystemService
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.ImageView
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.activityViewModels
import android.widget.SeekBar
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.fragment.findNavController
import com.example.skygazers.databinding.FragmentSecondBinding
import java.util.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraCharacteristics
import kotlin.math.abs
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

    var cameraManager: CameraManager? = null
    private var horizViewAngle by Delegates.notNull<Float>()
    private var vertViewAngle by Delegates.notNull<Float>()
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
        cameraManager = context?.getSystemService(Context.CAMERA_SERVICE) as CameraManager

    }

    private fun setUpCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        cameraProviderFuture.addListener(Runnable {

            // CameraProvider
            cameraProvider = cameraProviderFuture.get()


            // Build and bind the camera use cases
            bindCameraUseCases()
        }, ContextCompat.getMainExecutor(requireContext()))
        try {
            val cameraId = cameraManager!!.cameraIdList[0]
            val characteristics = cameraManager!!.getCameraCharacteristics(cameraId)

            val horizontalViewAngles = characteristics.get(CameraCharacteristics.SENSOR_INFO_ACTIVE_ARRAY_SIZE)
            val verticalViewAngles = characteristics.get(CameraCharacteristics.SENSOR_INFO_PHYSICAL_SIZE)
//
//            // Calculate the horizontal and vertical focal lengths THIS IS WHERE IT BREAKS
            val horizontalFocalLength = characteristics.get(CameraCharacteristics.LENS_INFO_AVAILABLE_FOCAL_LENGTHS)!![0]
            val verticalFocalLength = characteristics.get(CameraCharacteristics.LENS_INFO_AVAILABLE_FOCAL_LENGTHS)!![1]
//
//            // Calculate the horizontal and vertical field of view in radians
//            val horizontalFieldOfView = 2 * Math.atan(horizontalViewAngles!!.width().toDouble() / (2 * horizontalFocalLength))
//            val verticalFieldOfView = 2 * Math.atan(verticalViewAngles!!.height.toDouble() / (2 * verticalFocalLength))
//
//            // Convert the field of view to degrees
//             horizViewAngle = Math.toDegrees(horizontalFieldOfView).toFloat()
//             vertViewAngle = Math.toDegrees(verticalFieldOfView).toFloat()
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
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

    // 1: phone, 2: sun
    fun displaySun(az1: Float, el1: Float, az2: Float, el2: Float) {

        if (abs(az1 - az2) < horizViewAngle / 2 && abs(el1 - el2) < vertViewAngle) {
            var az = (az2 - az1) + (horizViewAngle / 2)
            var el = -1 * (el2 - el1) + (vertViewAngle / 2)
            var x = az / horizViewAngle
            var y = el / vertViewAngle
            binding.sunView.setX(x)
            binding.sunView.setY(y)
        }

    }



    companion object {

        /** Convenience method used to check if all permissions required by this app are granted */
        fun hasPermissions(context: Context) = PERMISSIONS_REQUIRED.all {
            ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }
    }
}


