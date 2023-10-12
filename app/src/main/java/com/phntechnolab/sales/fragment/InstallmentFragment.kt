package com.phntechnolab.sales.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.phntechnolab.sales.databinding.FragmentInstalmentBinding
import com.phntechnolab.sales.viewmodel.InstallmentViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class InstallmentFragment : Fragment() {
    private var _binding: FragmentInstalmentBinding? = null
    private val binding get() = _binding!!
//    private val args: InstallmentFragmentArgs by navArgs()
//    private val viewModel: InstallmentViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentInstalmentBinding.inflate(inflater, container, false)
        setOnBackPressed()
//        viewModel.setOldSchoolData(args.moaSchoolData?)
//        binding.viewModel = viewModel
//        binding.lifecycleOwner = this
        return binding.root
    }

    private fun setOnBackPressed() {
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().popBackStack()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(callback)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeListener()
    }

    private fun initializeListener() {
        binding.topAppBar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}