package com.phntechnolab.sales.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.view.MenuProvider
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.google.gson.Gson
import com.phntechnolab.sales.R
import com.phntechnolab.sales.activity.MainActivity
import com.phntechnolab.sales.adapter.MeetingsAdapter
import com.phntechnolab.sales.databinding.FragmentMeetingBinding
import com.phntechnolab.sales.model.CoordinatorData
import com.phntechnolab.sales.model.DMData
import com.phntechnolab.sales.model.MOADocumentData
import com.phntechnolab.sales.model.MeetingData
import com.phntechnolab.sales.model.ProposeCostingData
import com.phntechnolab.sales.viewmodel.MeetingViewModel
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber


@AndroidEntryPoint
class MeetingFragment : Fragment(), MenuProvider, MeetingsAdapter.CallBacks {
    private var _binding: FragmentMeetingBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<MeetingViewModel>()
    private var _adapter: MeetingsAdapter? = null
    private val adapter get() = _adapter!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.getAllSchools()
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
        _adapter = MeetingsAdapter(this)
        binding.meetingRv.adapter = adapter
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setActionBar()
        initializeListener()
        observers()
    }

    private fun observers() {
        viewModel.schoolLiveData.observe(viewLifecycleOwner) {
            if (it.data?.isNotEmpty() == true) {
                viewModel.segregateData()
            }
        }

        viewModel.meetingsData.observe(viewLifecycleOwner) {
            Timber.e("DATA OF FINAL")
            Timber.e(Gson().toJson(it))
            adapter.setData(ArrayList<MeetingData>(it.filter { it.taskDateFilter == "today" }
                .sortedByDescending { it.dateTime }))
            val meetingsData =
                (viewModel.meetingsData.value
                    ?: ArrayList()).filter { it.taskDateFilter == "today" }
                    .sortedByDescending { it.dateTime }
            if (meetingsData.isNullOrEmpty()) {
                binding.noDataLottie.visibility = View.VISIBLE
                binding.meetingRv.visibility = View.GONE
            } else {
                binding.noDataLottie.visibility = View.GONE
                binding.meetingRv.visibility = View.VISIBLE
                adapter.setData(ArrayList<MeetingData>(meetingsData))
            }
        }
    }

    private fun setOnBackPressed() {
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigate(R.id.homeFragment)
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(callback)
    }

    private fun initializeListener() {
        binding.materialButtonToggleGroup.addOnButtonCheckedListener { group, checkedId, isChecked ->
            if (isChecked) {
                when (checkedId) {
                    R.id.today_btn -> {
                        val meetingsData = (viewModel.meetingsData.value
                            ?: ArrayList()).filter { it.taskDateFilter == "today" }
                            .sortedByDescending { it.dateTime }
                        if (meetingsData.isNullOrEmpty()) {
                            binding.noDataLottie.visibility = View.VISIBLE
                            binding.meetingRv.visibility = View.GONE
                        } else {
                            binding.noDataLottie.visibility = View.GONE
                            binding.meetingRv.visibility = View.VISIBLE
                            adapter.setData(ArrayList<MeetingData>(meetingsData))
                        }
                    }

                    R.id.tomorrow_btn -> {
                        val meetingsData =
                            (viewModel.meetingsData.value
                                ?: ArrayList()).filter { it.taskDateFilter == "tomorrow" }
                                .sortedByDescending { it.dateTime }
                        (viewModel.meetingsData.value
                            ?: ArrayList()).filter { it.taskDateFilter == "tomorrow" }
                            .sortedByDescending { it.dateTime }
                        if (meetingsData.isNullOrEmpty()) {
                            binding.noDataLottie.visibility = View.VISIBLE
                            binding.meetingRv.visibility = View.GONE
                        } else {
                            binding.noDataLottie.visibility = View.GONE
                            binding.meetingRv.visibility = View.VISIBLE
                            adapter.setData(ArrayList<MeetingData>(meetingsData))
                        }
                    }

                    R.id.upcoming_btn -> {
                        val meetingsData =
                            (viewModel.meetingsData.value
                                ?: ArrayList()).filter { it.taskDateFilter == "upcoming" }
                                .sortedByDescending { it.dateTime }
                        if (meetingsData.isNullOrEmpty()) {
                            binding.noDataLottie.visibility = View.VISIBLE
                            binding.meetingRv.visibility = View.GONE
                        } else {
                            binding.noDataLottie.visibility = View.GONE
                            binding.meetingRv.visibility = View.VISIBLE
                            adapter.setData(ArrayList<MeetingData>(meetingsData))
                        }
                    }
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        binding.todayBtn.isChecked = true
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
                findNavController().navigate(R.id.action_meetingFragment_to_searchFragment)
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

    override fun meetingData(data: MeetingData) {
        when (data.taskName) {
            "proposecosting" -> {
                requireView().findNavController()
                    .navigate(
                        MeetingFragmentDirections.actionMeetingFragmentToCostingMoaDocumentFragment(
                            data.data?.proposeCostingData
                                ?: ProposeCostingData(schoolId = data.data?.schoolId),
                            data.data?.moaDocumentData
                                ?: MOADocumentData(schoolId = data.data?.schoolId)
                        )
                    )
            }

            "coordinator" -> {
                requireView().findNavController()
                    .navigate(
                        MeetingFragmentDirections.actionHomeFragmentToMeetingFragment(
                            data.data?.coordinator
                                ?: CoordinatorData(schoolId = data.data?.schoolId),
                            data.data?.director ?: DMData(schoolId = data.data?.schoolId)
                        )
                    )
            }

            "basicDetails" -> {

            }

            else -> {

            }
        }
    }
}