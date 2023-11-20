package com.phntechnolab.sales.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.navigation.fragment.findNavController
import com.phntechnolab.sales.adapter.GenericAdapter
import com.phntechnolab.sales.databinding.ActivityesItemBinding
import com.phntechnolab.sales.databinding.FragmentNotificationBinding


class NotificationFragment : Fragment() {
    private var _binding: FragmentNotificationBinding? = null
    private val binding get() = _binding!!

    private var _adapter: GenericAdapter<String, ActivityesItemBinding>? = null
    private val adapter get() = _adapter!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentNotificationBinding.inflate(inflater, container, false)
        initializeAdapter()
        setbackPressed()
        return binding.root
    }

    private fun setbackPressed() {
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().popBackStack()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(callback)
    }

    private fun initializeAdapter() {
        _adapter =
            GenericAdapter(ActivityesItemBinding::inflate, onBind = { data, binding, position,listSize ->
                binding.activityName.text = data
            })
        binding.notificationRv.adapter = adapter
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setDataToAdapter()
        initializeListener()
    }

    private fun initializeListener() {
        binding.topAppBar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun setDataToAdapter() {
//        adapter.setData(ArrayList<String>().apply {
//            add(getString(R.string.demo_text))
//            add(getString(R.string.demo_text))
//            add(getString(R.string.demo_text))
//            add(getString(R.string.demo_text))
//            add(getString(R.string.demo_text))
//            add(getString(R.string.demo_text))
//            add(getString(R.string.demo_text))
//        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        _adapter = null
    }
}