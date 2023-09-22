package com.phntechnolab.sales.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.navigation.fragment.findNavController
import com.phntechnolab.sales.R
import com.phntechnolab.sales.adapter.ProfileSettingAdapter
import com.phntechnolab.sales.databinding.FragmentProfileBinding
import com.phntechnolab.sales.model.SettingModel


class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private var _adapter: ProfileSettingAdapter? = null
    private val adapter get() = _adapter!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)

        initializeAdapter()
        setOnBackPressed()
        return binding.root
    }

    private fun initializeAdapter() {
        val callback = object : ProfileSettingAdapter.Callback {
            override fun openSetting(position: Int) {
                when (position) {
                    0 -> {
                        findNavController().navigate(R.id.action_profileFragment_to_myAccountFragment)
                    }

                    1 -> {
                        findNavController().navigate(R.id.action_profileFragment_to_activitiesFragment)
                    }

                    3 -> {
                        findNavController().navigate(R.id.action_profileFragment_to_changePasswordFragment)
                    }

                    else -> {

                    }
                }
            }
        }
        _adapter = ProfileSettingAdapter(callback)
        binding.settingRv.adapter = adapter
    }

    private fun setOnBackPressed() {
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigate(R.id.homeFragment)
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(callback)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setDataToAdapter()
    }

    private fun setDataToAdapter() {
        adapter.setData(ArrayList<SettingModel>().apply {
            add(
                SettingModel(
                    R.drawable.my_account,
                    resources.getString(R.string.my_account),
                    resources.getString(R.string.demo_text)
                )
            )
            add(
                SettingModel(
                    R.drawable.activity,
                    resources.getString(R.string.activity),
                    resources.getString(R.string.demo_text)
                )
            )
            add(
                SettingModel(
                    R.drawable.revenue,
                    resources.getString(R.string.revenue),
                    resources.getString(R.string.demo_text)
                )
            )
            add(
                SettingModel(
                    R.drawable.change_password,
                    resources.getString(R.string.change_password),
                    resources.getString(R.string.demo_text)
                )
            )
            add(
                SettingModel(
                    R.drawable.chart_analysis,
                    resources.getString(R.string.chart_analysis),
                    resources.getString(R.string.demo_text)
                )
            )
            add(
                SettingModel(
                    R.drawable.log_out,
                    resources.getString(R.string.log_out),
                    resources.getString(R.string.demo_text)
                )
            )
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        _adapter = null
    }
}