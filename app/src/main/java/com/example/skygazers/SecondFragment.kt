package com.example.skygazers

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ImageView
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.skygazers.databinding.FragmentSecondBinding
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class SecondFragment : Fragment() {

    private var _binding: FragmentSecondBinding? = null
    private val viewModel: SecondActivityViewModel by activityViewModels()
    var isRunning: Boolean = false;

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

    override fun onResume(){
        super.onResume()
    }

    private fun setUpPosLoop() {
        setUpButton()
        GlobalScope.launch{updatePosLoop()}
    }

    private fun setUpButton() {
        binding.posLoopButton.setOnClickListener {
            if(isRunning){
                isRunning = !isRunning
                binding.posLoopButton.text = "Start Loop"
            } else {
                isRunning = !isRunning
                binding.posLoopButton.text = "Stop Loop"
                GlobalScope.launch{updatePosLoop()}
            }
        }
        binding.buttonSecond.setOnClickListener {
            isRunning = false;
            findNavController().navigate(R.id.action_SecondFragment_to_FirstFragment)
        }
    }

    private fun updatePosLoop() {
        while(isRunning) {
            //TODO: get sun position based off of location/gyroscope parameters to set X,Y coordinates
            Thread.sleep(100)
            val randX = (0..1000).random()
            val randY = (0..1000).random()
            binding.sunView.setX(randX.toFloat())
            binding.sunView.setY(randY.toFloat())
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonSecond.setOnClickListener {
            findNavController().navigate(R.id.action_SecondFragment_to_FirstFragment)
        }

        viewModel.listenLatLong().observe(viewLifecycleOwner) {
            binding.textviewSecond.text = it

        }

        setUpPosLoop()

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
