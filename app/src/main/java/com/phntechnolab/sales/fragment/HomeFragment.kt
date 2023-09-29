package com.phntechnolab.sales.fragment

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.chip.Chip
import com.phntechnolab.sales.Modules.DataStoreProvider
import com.phntechnolab.sales.R
import com.phntechnolab.sales.adapter.SchoolDetailAdapter
import com.phntechnolab.sales.activity.MainActivity
import com.phntechnolab.sales.databinding.FragmentHomeBinding
import com.phntechnolab.sales.model.CoordinatorData
import com.phntechnolab.sales.model.DMData
import com.phntechnolab.sales.model.SchoolData
import com.phntechnolab.sales.viewmodel.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject


@AndroidEntryPoint
class HomeFragment : Fragment(), MenuProvider, SchoolDetailAdapter.CallBacks {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<HomeViewModel>()

    private var _adapter: SchoolDetailAdapter? = null
    private val adapter get() = _adapter

    @Inject
    lateinit var dataStoreProvider: DataStoreProvider

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onResume() {
        super.onResume()

        getData()

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        initializeAdapter()

        setOnBackPressed()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setActionBar()

        observers()

        checkedChangeListener()
    }


    private fun checkedChangeListener() {
        binding.chipGroup.setOnCheckedChangeListener { chipGroup, id ->
            val chip = chipGroup.findViewById<Chip>(id)
//            val chip = chipGroup.getChildAt(chipGroup.checkedChipId)
            if (chip != null) {
                for (i in 0 until chipGroup.childCount) {
                    chipGroup.getChildAt(i).isClickable = true
                }
                chip.isClickable = false
                adapter?.setData(viewModel.schoolLiveData.value?.data?.filter {
                    when (chip.text) {
                        "All" -> {
                            true
                        }

                        "Propose Costing" -> {
                            it.status == "Propose Costing"
                        }

                        "MOA Signed" -> {
                            it.status == "MOASigned"
                        }

                        else -> {
                            it.status == chip.text
                        }
                    }
                } as ArrayList<SchoolData>)
            }
        }

        binding.addSchool.setOnClickListener {
            it.findNavController()
                .navigate(HomeFragmentDirections.actionHomeFragmentToAddSchoolFragment(null))
        }
    }

    private fun observers() {
        viewModel.schoolLiveData.observe(viewLifecycleOwner) {
            adapter?.setData(it.data as ArrayList<SchoolData>)
        }
    }

    private fun initializeAdapter() {
        _adapter = SchoolDetailAdapter(this)
        binding.homeRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.homeRecyclerView.adapter = _adapter
    }

    private fun getData() {
        lifecycleScope.launch(Dispatchers.IO) {
            viewModel.getAllSchools()
        }
    }

    override fun onDestroyView() {
        _binding = null
        _adapter = null
        super.onDestroyView()
    }

    private fun setOnBackPressed() {
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                requireActivity().finishAffinity()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(callback)
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
                Toast.makeText(requireContext(), "search", Toast.LENGTH_SHORT).show()
                true
            }

            R.id.menu_notification -> {
                findNavController().navigate(R.id.action_homeFragment_to_notificationFragment)
                Toast.makeText(requireContext(),"notification",Toast.LENGTH_SHORT).show()
                true
            }

            else -> {
                false
            }
        }
    }

    override fun openSchoolDetails(schoolData: SchoolData) {

        requireView()?.findNavController()
            ?.navigate(HomeFragmentDirections.actionHomeFragmentToAddSchoolFragment(schoolData))
    }

    override fun meetingNavigation(schoolData: SchoolData) {
//        requireView().findNavController().navigate(R.id.action_homeFragment_to_meetingFragment)
        if(schoolData.status == "Visited"){
            requireView().findNavController()
                .navigate(HomeFragmentDirections.actionHomeFragmentToMeetingFragment(schoolData.coordinator?: CoordinatorData(schoolId = schoolData.schoolId), schoolData.director?: DMData(schoolId = schoolData.schoolId)))
        }else if(schoolData.status == "Propose Costing"){
            requireView().findNavController().navigate(R.id.action_homeFragment_to_costingMoaDocumentFragment)
        }
    }
}