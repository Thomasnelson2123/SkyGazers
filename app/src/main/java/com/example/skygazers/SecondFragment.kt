package com.example.skygazers

import android.content.Context.SENSOR_SERVICE
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.skygazers.databinding.FragmentSecondBinding
import java.util.*

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


    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentSecondBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonSecond.setOnClickListener {
            findNavController().navigate(R.id.action_SecondFragment_to_FirstFragment)
        }

        viewModel.listenLatLong().observe(viewLifecycleOwner) {
            binding.textviewSecond.text = it
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        sensor.stopSensors()
        _binding = null
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        sensor = Sensors(
            requireActivity().getSystemService(SENSOR_SERVICE) as SensorManager
        )
        sensor.startSensors()
    }

    override fun onResume() {
        super.onResume()
        sensor.startSensors()
        timer = Timer()
        timer.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                Handler(Looper.getMainLooper()).post {
                    var accValues = sensor.getAccelerometerValues()
                    binding.accelerometerValuesTextView.text = "x: " + accValues[0] + " y: " + accValues[1] + " z: " + accValues[2]
                    var magValues = sensor.getMagneticFieldValues()
                    binding.magneticFieldValuesTextView.text = "x: " + magValues[0] + " y: " + magValues[1] + " z: " + magValues[2]
                }
            }
        }, 0, 1000)
    }


    override fun onPause() {
        super.onPause()
        sensor.stopSensors()
    }


}