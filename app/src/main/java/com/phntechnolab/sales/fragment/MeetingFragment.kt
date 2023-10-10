package com.phntechnolab.sales.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.view.MenuProvider
import androidx.navigation.fragment.findNavController
import com.phntechnolab.sales.R
import com.phntechnolab.sales.activity.MainActivity
import com.phntechnolab.sales.adapter.ActivitiesAdapter
import com.phntechnolab.sales.databinding.FragmentMeetingBinding


class MeetingFragment : Fragment(), MenuProvider {
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
        setActionBar()
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

    override fun onStop() {
        super.onStop()
        (requireActivity() as MainActivity).removeMenuProvider(this)
        activity?.removeMenuProvider(this)
    }

    private fun setActionBar() {
        (requireActivity() as MainActivity).setSupportActionBar(binding.homeTopBar)
        activity?.addMenuProvider(this)
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.home_top_bar_menu, menu)
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        return when (menuItem.itemId) {
            R.id.menu_search -> {
                true
            }

            R.id.menu_notification -> {
                findNavController().navigate(R.id.action_meetingFragment_to_notificationFragment)
                true
            }

            else -> {
                false
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        _adapter = null
    }
}