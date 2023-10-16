package com.phntechnolab.sales.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.view.MenuProvider
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.google.android.material.chip.Chip
import com.phntechnolab.sales.Modules.DataStoreProvider
import com.phntechnolab.sales.R
import com.phntechnolab.sales.adapter.SchoolDetailAdapter
import com.phntechnolab.sales.activity.MainActivity
import com.phntechnolab.sales.databinding.FragmentHomeBinding
import com.phntechnolab.sales.model.CoordinatorData
import com.phntechnolab.sales.model.DMData
import com.phntechnolab.sales.model.InstallmentData
import com.phntechnolab.sales.model.MOADocumentData
import com.phntechnolab.sales.model.ProposeCostingData
import com.phntechnolab.sales.model.SchoolData
import com.phntechnolab.sales.util.NetworkResult
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

        binding.all.isChecked = true
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
                val schoolData =
                    ((viewModel.schoolLiveData.value?.data?.filter { it.status != "MOA Pending" }
                        ?.sortedByDescending { it.updatedAt }
                        ?.filter {
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

                                "Not Interested" -> {
                                    it.status == "Not Interested"
                                }

                                else -> {
                                    it.status == chip.text
                                }
                            }
                        } as ArrayList<SchoolData>) as ArrayList<SchoolData>)
                adapter?.setData(schoolData)
                if (schoolData.isNullOrEmpty()) {
                    binding.noDataLottie.visibility = View.VISIBLE
                    binding.homeRecyclerView.visibility = View.GONE
                    binding.progressBar.visibility = View.GONE
                    binding.progressIndicator.visibility = View.GONE
                }else{
                    binding.homeRecyclerView.visibility = View.VISIBLE
                    binding.noDataLottie.visibility = View.GONE
                    binding.progressIndicator.visibility = View.GONE
                    binding.progressBar.visibility = View.GONE
                }
            }
        }

        binding.addSchool.setOnClickListener {
            it.findNavController()
                .navigate(HomeFragmentDirections.actionHomeFragmentToAddSchoolFragment(null))
        }
    }

    private fun observers() {
        viewModel.schoolLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is NetworkResult.Success -> {
                    Timber.e(it.message.toString())
                    binding.noInternetConnection.visibility = View.GONE
                    binding.noInternetMessage.visibility = View.GONE
                    if (it.data?.isEmpty()!!) {
                        binding.noDataLottie.visibility = View.VISIBLE
                        binding.homeRecyclerView.visibility = View.GONE
                        binding.progressBar.visibility = View.GONE
                        binding.progressIndicator.visibility = View.GONE
                    } else {
                        binding.homeRecyclerView.visibility = View.VISIBLE
                        binding.noDataLottie.visibility = View.GONE
                        binding.progressIndicator.visibility = View.GONE
                        binding.progressBar.visibility = View.GONE
                        adapter?.setData(ArrayList<SchoolData>().apply {
                            addAll(it.data.filter { it.status != "MOA Pending" }
                                .sortedByDescending { it.updatedAt })
                        })
                    }
                }

                is NetworkResult.Error -> {
                    Timber.e(it.message.toString())
                    when (it.message) {
                        getString(R.string.please_connection_message) -> {
                            binding.noInternetConnection.visibility = View.VISIBLE
                            binding.noInternetMessage.visibility = View.VISIBLE
                            binding.progressIndicator.visibility = View.GONE
                        }

                        else -> {
                        }
                    }
                }

                else -> {
                    Timber.e(it.message.toString())
                }
            }

        }
    }

    private fun initializeAdapter() {
        _adapter = SchoolDetailAdapter(this)
        binding.homeRecyclerView.adapter = _adapter
    }

    private fun getData() {
//        binding.progressBar.visibility = View.VISIBLE
        binding.progressIndicator.visibility = View.VISIBLE
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
                findNavController().navigate(R.id.action_homeFragment_to_searchFragment)
                true
            }

            R.id.menu_notification -> {
                findNavController().navigate(R.id.action_homeFragment_to_notificationFragment)
                true
            }

            else -> {
                false
            }
        }
    }

    override fun openSchoolDetails(schoolData: SchoolData) {
        findNavController()
            .navigate(HomeFragmentDirections.actionHomeFragmentToAddSchoolFragment(schoolData))
    }

    override fun meetingNavigation(schoolData: SchoolData) {
        when (schoolData.status) {
            "Assigned" -> {
                findNavController()
                    .navigate(
                        HomeFragmentDirections.actionHomeFragmentToAddSchoolFragment(
                            schoolData
                        )
                    )
            }

            "Not Interested" -> {
                findNavController()
                    .navigate(
                        HomeFragmentDirections.actionHomeFragmentToAddSchoolFragment(
                            schoolData
                        )
                    )
            }

            "Visited" -> {
                requireView().findNavController()
                    .navigate(
                        HomeFragmentDirections.actionHomeFragmentToMeetingFragment(
                            schoolData.coordinator
                                ?: CoordinatorData(schoolId = schoolData.schoolId),
                            schoolData.director ?: DMData(schoolId = schoolData.schoolId)
                        )
                    )
            }

            "Propose Costing" -> {
                requireView().findNavController()
                    .navigate(
                        HomeFragmentDirections.actionHomeFragmentToCostingMoaDocumentFragment(
                            schoolData.proposeCostingData
                                ?: ProposeCostingData(schoolId = schoolData.schoolId),
                            schoolData.moaDocumentData
                                ?: MOADocumentData(schoolId = schoolData.schoolId)
                        )
                    )
            }

//            "MOA Pending" -> {
//                requireView().findNavController()
//                    .navigate(
//                        HomeFragmentDirections.actionHomeFragmentToMoaSignedFragment(
//                            schoolData.installmentData
//                                ?: InstallmentData(schoolId = schoolData.schoolId)
//                        )
//                    )
//            }

            "Installment" -> {
                requireView().findNavController()
                    .navigate(
                        HomeFragmentDirections.actionHomeFragmentToMoaSignedFragment(
                            schoolData
                        )
                    )
            }

            "MOASigned" -> {
                requireView().findNavController()
                    .navigate(
                        HomeFragmentDirections.actionHomeFragmentToMoaSignedFragment(
                            schoolData
                        )
                    )
            }
        }
    }
}