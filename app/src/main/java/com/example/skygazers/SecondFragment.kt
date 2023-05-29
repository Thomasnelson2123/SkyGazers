package com.example.skygazers


import android.content.Context
import android.content.Context.SENSOR_SERVICE
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.hardware.SensorManager
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.example.skygazers.databinding.FragmentSecondBinding
import kotlinx.coroutines.*
import java.lang.Runnable
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.collections.ArrayList
import java.util.Timer


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
    var suns: ArrayList<SunObject?>? = ArrayList()

    private lateinit var container: FrameLayout
    private lateinit var imageView: ImageView
    private lateinit var canvasView: CanvasView


    var isRunning: Boolean = false

    private var lensFacing = CameraSelector.LENS_FACING_BACK
    private var preview: Preview? = null
    private var camera: Camera? = null
    private var cameraProvider: ProcessCameraProvider? = null
    private lateinit var windowManager: WindowManager

    private var horizonalAngle: Float = 0.0F
    private var verticalAngle: Float = 0.0F

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
        binding.showDebug.setOnClickListener {
            if(binding.debugWindow.visibility == View.VISIBLE){
                binding.debugWindow.visibility = View.GONE;
            } else {
                binding.debugWindow.visibility = View.VISIBLE;
            }
        }
//        sun = viewModel.getSunObject()
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

        val manager = activity?.getSystemService(Context.CAMERA_SERVICE) as CameraManager?
        if (manager != null) {
            calculateFOV(manager)
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
        //sun = viewModel.getSunObject()
        sensor.startSensors()
        lifecycleScope.launch {
            sensor.subscribeOrientation().collect {
                binding.textView2.text =
                    "az: " + it[0] + " pitch: " + it[1] + " roll: " + it[2]
                displaySun(it[0], it[1])
            }
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonSecond.setOnClickListener{
            val intent = Intent(context, MainActivity::class.java);
            startActivity(intent)
        }


        val seek = binding.seekBar;
        //val sunImg = binding.sunPicture;
        seek?.setOnSeekBarChangeListener(object:
        SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val curTime =seekBar?.progress.toString()
                binding.curNum.text = curTime
                viewModel.updateTime(progress) // updates the sun's time
                suns = viewModel.getSunObject().value
//                sunPos = viewModel.updateTime(progress)
                binding.curAzimuth.text = suns?.get(progress)?.azimuth.toString()
                binding.curElevation.text = suns?.get(progress)?.elevation.toString()
                canvasView.updateHour(progress)
                val el = suns?.get(0)?.elevation ?: 0f
                if (el < 10 && el > 0){
                    //sunset / sunrise
                    //sunImg.setImageResource(R.drawable.sunset);
                } else {
                    //day
                    //sunImg.setImageResource(R.drawable.sun);
                }
            }


            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })

        container = view.findViewById(R.id.container)
        //imageView = view.findViewById(R.id.sunPicture)
        val coroutineScope = CoroutineScope(Dispatchers.Main)

        canvasView = CanvasView(requireContext())
        container.addView(canvasView)

//        coroutineScope.launch {
//            for(i in 0..10){
//                canvasView.drawLine(i*30f, i*30f, i*30f, (i*30)+400f)
//
//                canvasView.setSunCoords(i*30, i*30)
//
//                delay(900)
//            }
//        }



        viewModel.getSunObject().observe(viewLifecycleOwner) {
            suns = it
            suns?.get(0)?.updateHour(0)
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

    fun displaySun(azimuth: Float, elevation: Float) {
        for (i in 0 .. 23) {
            getScreenCoords(azimuth, elevation, i)
        }
        if(suns?.isEmpty() == true) {
            return
        }
        canvasView.setSuns(suns!!)


//        binding.sunPicture.setX(suns?.get(0)?.xpos ?: 0f)
//        binding.sunPicture.setY(suns?.get(0)?.ypos ?: 0f)



    }

    fun getScreenCoords(azimuth: Float, elevation: Float, index: Int) {

        // Get the screen dimensions
        val displayMetrics = Resources.getSystem().displayMetrics
        val screenWidth = displayMetrics.widthPixels
        val screenHeight = displayMetrics.heightPixels

        if(suns?.isEmpty() == true) {
            return
        }
        var sunAz = suns?.get(index)?.azimuth ?: 0f
        var sunEl = suns?.get(index)?.elevation ?: 0f

        Log.d("Sun Angles", "azim:" + sunAz + "elev" + sunEl)
        var min = (azimuth - (horizonalAngle / 2))
        if (min < 0) {
            min += 360
        }
        var max = (azimuth + (horizonalAngle / 2))
        var x = 0f
        if (min <= max) {
            x = (((sunAz - (min))) * screenWidth) / (max - min)
        }
        else {
            x = (((sunAz - (min))) % 360 ) / (max - min + 360) * screenWidth
        }
        val y = screenHeight - (((sunEl - (elevation - (verticalAngle / 2))) * screenHeight) / verticalAngle).toFloat()

        suns?.get(index)?.xpos = x
        suns?.get(index)?.ypos = y

    }

    private fun calculateFOV(cManager: CameraManager) {
        try {
            for (cameraId in cManager.cameraIdList) {
                val characteristics = cManager.getCameraCharacteristics(cameraId)
                val cOrientation = characteristics.get(CameraCharacteristics.LENS_FACING)!!
                if (cOrientation == CameraCharacteristics.LENS_FACING_BACK) {
                    val maxFocus =
                        characteristics.get(CameraCharacteristics.LENS_INFO_AVAILABLE_FOCAL_LENGTHS)
                    val size = characteristics.get(CameraCharacteristics.SENSOR_INFO_PHYSICAL_SIZE)
                    val w = size!!.width
                    val h = size.height
                    horizonalAngle =
                        (2 * Math.atan((w / (maxFocus!![0] * 2)).toDouble())).toFloat()
                    verticalAngle = (2 * Math.atan((h / (maxFocus[0] * 2)).toDouble())).toFloat()
                    horizonalAngle = Math.toDegrees(horizonalAngle.toDouble()).toFloat()
                    verticalAngle = Math.toDegrees(verticalAngle.toDouble()).toFloat()

                }
            }
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
    }

    companion object {

        /** Convenience method used to check if all permissions required by this app are granted */
        fun hasPermissions(context: Context) = PERMISSIONS_REQUIRED.all {
            ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }
    }
}


