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
import com.phntechnolab.sales.R
import com.phntechnolab.sales.databinding.FragmentMyAccountBinding
import com.phntechnolab.sales.util.AppEvent
import com.phntechnolab.sales.util.backPressHandle
import com.phntechnolab.sales.viewmodel.MyAccountViewModel
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class MyAccountFragment : Fragment() {
    private var _binding: FragmentMyAccountBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MyAccountViewModel by viewModels()

    private val args: MyAccountFragmentArgs by navArgs()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMyAccountBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        setUIData()
        backPressHandle {
            findNavController().popBackStack()
        }
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observables()
    }

    private fun observables() {
        viewModel.appEvent.observe(viewLifecycleOwner, ::appEvent)
    }

    private fun appEvent(appEvent: AppEvent?) {
        when (appEvent) {
            is AppEvent.BackScreen -> {
                if (appEvent.screenID == 0) {
                    findNavController().popBackStack()
                    viewModel._appEvent.postValue(null)
                }
            }

            else -> {}
        }
    }

    private fun setUIData() {
        Timber.e(args.userData?.name)
        viewModel.setNewUserData(args.userData)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}