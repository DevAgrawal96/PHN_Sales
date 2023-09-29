package com.phntechnolab.sales.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.phntechnolab.sales.databinding.FragmentCostingMoaDocumentBinding

class CostingMOADocumentFragment: Fragment() {

    private var _binding: FragmentCostingMoaDocumentBinding? = null
    private val binding get() = _binding!!


//    private val args: CoordinatorDmMeetingFragmentArgs by navArgs()

    private val backPressHandler = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
//            setButtonName(viewModel.oldSchoolData.value)

            when (position) {
                0 -> {
                    findNavController().popBackStack()
                }

                1 -> {
                    binding.proposeCostingStage.root.visibility = View.VISIBLE
                    binding.moaDocument.root.visibility = View.GONE
                    position = 0
                    binding.stepView.done(false)
                    binding.stepView.go(position, true)
                }

                else -> {
                    position = 1
                    binding.stepView.done(true)
                    binding.stepView.go(1, true)
                }
            }
        }
    }

    var position = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCostingMoaDocumentBinding.inflate(inflater, container, false)

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, backPressHandler)


        return binding.root
    }

    private fun setPositionView() {
//        setButtonName(viewModel.oldSchoolData.value)

        when (position) {
            0 -> {
                binding.proposeCostingStage.root.visibility = View.GONE
                binding.moaDocument.root.visibility = View.VISIBLE
                position = 1
                binding.stepView.done(false)
                binding.stepView.go(position, true)
            }

            1 -> {
                binding.proposeCostingStage.root.visibility = View.GONE
                binding.moaDocument.root.visibility = View.VISIBLE
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}