package com.phntechnolab.sales.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.phntechnolab.sales.databinding.FragmentAssignedSchoolsStepperBinding
import com.phntechnolab.sales.databinding.FragmentHomeBinding
import com.phntechnolab.sales.viewmodel.HomeViewModel

private var _binding: FragmentHomeBinding? = null
private val binding get() = _binding!!
class AddSchoolFragment: Fragment() {

    private var _binding: FragmentAssignedSchoolsStepperBinding? = null
    private val binding get() = _binding!!

    var position = 0

    private val backPressHandler = object : OnBackPressedCallback(true){
        override fun handleOnBackPressed() {
            when (position) {
                0 -> {
                    findNavController().popBackStack()
                }
                1 -> {
                    binding.basicDetails.root.visibility = View.VISIBLE
                    binding.schoolDetails.root.visibility = View.GONE
                    position = 0
                    binding.stepView.done(false)
                    binding.stepView.go(position, true)
                }

                2-> {
                    binding.schoolDetails.root.visibility = View.VISIBLE
                    binding.followupDetails.root.visibility = View.GONE
                    position = 1
                    binding.stepView.done(false)
                    binding.stepView.go(position, true)
                }

                else -> {
                    position = 3
                    binding.stepView.done(true)
                    binding.stepView.go(0, true)
                }
            }
        }

    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentAssignedSchoolsStepperBinding.inflate(inflater, container, false)

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, backPressHandler)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        oncClickListener()
    }

    private fun oncClickListener() {
        binding.basicDetails.btnSave.setOnClickListener {
            setPositionView()
        }

        binding.schoolDetails.btnSave.setOnClickListener {
            setPositionView()
        }
    }

    private fun setPositionView(){
        when (position) {
            0 -> {
                binding.basicDetails.root.visibility = View.GONE
                binding.schoolDetails.root.visibility = View.VISIBLE
                position = 1
                binding.stepView.done(false)
                binding.stepView.go(position, true)
            }
            1 -> {
                binding.schoolDetails.root.visibility = View.GONE
                binding.followupDetails.root.visibility = View.VISIBLE
                position = 2
                binding.stepView.done(false)
                binding.stepView.go(position, true)
            }

            else -> {
                position = 0
                binding.stepView.done(true)
                binding.stepView.go(0, true)


            }
        }
    }
}