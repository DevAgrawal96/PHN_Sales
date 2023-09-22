package com.phntechnolab.sales.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.navigation.fragment.findNavController
import com.phntechnolab.sales.R
import com.phntechnolab.sales.adapter.ActivitiesAdapter
import com.phntechnolab.sales.databinding.FragmentMeetingBinding


class MeetingFragment : Fragment() {
    private var _binding: FragmentMeetingBinding? = null
    private val binding get() = _binding!!

    private var _adapter: ActivitiesAdapter? = null
    private val adapter get() = _adapter!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMeetingBinding.inflate(inflater, container, false)
        initializeAdapter()
        setOnBackPressed()
        return binding.root
    }

    private fun initializeAdapter() {
        _adapter = ActivitiesAdapter()
        binding.meetingRv.adapter = adapter
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setDataToAdapter()
        initializeListener()
    }
    private fun setOnBackPressed() {
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigate(R.id.homeFragment)
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(callback)
    }

    private fun setDataToAdapter() {
        adapter.setData(ArrayList<String>().apply {
            add(getString(R.string.demo_text))
            add(getString(R.string.demo_text))
            add(getString(R.string.demo_text))
            add(getString(R.string.demo_text))
            add(getString(R.string.demo_text))
            add(getString(R.string.demo_text))
        })
    }

    private fun initializeListener() {
        binding.materialButtonToggleGroup.addOnButtonCheckedListener { group, checkedId, isChecked ->
            if (isChecked) {
                when (checkedId) {
                    R.id.today_btn -> {
                        Toast.makeText(requireContext(), "today button", Toast.LENGTH_SHORT).show()
                    }

                    R.id.tomorrow_btn -> {
                        Toast.makeText(requireContext(), "tomorrow button", Toast.LENGTH_SHORT)
                            .show()
                    }

                    R.id.upcoming_btn -> {
                        Toast.makeText(requireContext(), "upcoming button", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}