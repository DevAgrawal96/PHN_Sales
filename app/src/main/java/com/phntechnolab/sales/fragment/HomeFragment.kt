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
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.filter
import androidx.paging.insertSeparators
import com.google.android.material.chip.Chip
import com.google.gson.Gson
import com.phntechnolab.sales.Modules.DataStoreProvider
import com.phntechnolab.sales.R
import com.phntechnolab.sales.adapter.SchoolDetailAdapter
import com.phntechnolab.sales.activity.MainActivity
import com.phntechnolab.sales.databinding.FragmentHomeBinding
import com.phntechnolab.sales.model.CoordinatorData
import com.phntechnolab.sales.model.DMData
import com.phntechnolab.sales.model.MOADocumentData
import com.phntechnolab.sales.model.ProposeCostingData
import com.phntechnolab.sales.model.SchoolData
import com.phntechnolab.sales.paging.SchoolPagingAdapter
import com.phntechnolab.sales.util.DataStoreManager.setToken
import com.phntechnolab.sales.util.NetworkResult
import com.phntechnolab.sales.viewmodel.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : Fragment(), MenuProvider, SchoolPagingAdapter.CallBacks {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<HomeViewModel>()

    private var schoolPagingAdapter: SchoolPagingAdapter? = null

    @Inject
    lateinit var dataStoreProvider: DataStoreProvider

    override fun onResume() {
        super.onResume()
        Timber.e("onResume")
        binding.all.isChecked = true
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

        checkedChangeListener()

        viewModel.getAllSchoolsPagination().observe(viewLifecycleOwner){
            viewModel.setPagingData(it)
            binding.swipeReferesh.isRefreshing = false

            binding.noInternetConnection.visibility = View.GONE
            binding.noInternetMessage.visibility = View.GONE

            val selectedChip =
                binding.chipGroup.findViewById<Chip>(binding.chipGroup.checkedChipId)
            Timber.e("SELECTED CHIP TEXT" + selectedChip.text.toString())
            schoolPagingAdapter?.updateOnlyChipText(selectedChip.text.toString())
            schoolPagingAdapter?.submitData(lifecycle, it)
        }

        binding.swipeReferesh.setOnRefreshListener {
            schoolPagingAdapter?.refresh()
        }

        schoolPagingAdapter?.addLoadStateListener {loadState ->
            Timber.e("LOAD STATE")
            Timber.e(loadState.toString())
            if (loadState.source.refresh is LoadState.NotLoading && loadState.append.endOfPaginationReached && schoolPagingAdapter?.itemCount?:0 < 1) {
                hideViewShowEmptyState()
            } else {
                showViewHideEmptyState()
            }
        }
    }

    private fun hideViewShowEmptyState(){
        binding.noDataLottie.visibility = View.VISIBLE
        binding.homeRecyclerView.visibility = View.GONE
        binding.progressBar.visibility = View.GONE
        binding.progressIndicator.visibility = View.GONE
    }

    private fun showViewHideEmptyState(){
        binding.homeRecyclerView.visibility = View.VISIBLE
        binding.noDataLottie.visibility = View.GONE
        binding.progressIndicator.visibility = View.GONE
        binding.progressBar.visibility = View.GONE
    }

    private fun checkListSize(count: Int){
        if(count <= 0)
            hideViewShowEmptyState()
        else
            showViewHideEmptyState()
    }
    private fun checkedChangeListener() {
        binding.chipGroup.setOnCheckedChangeListener { chipGroup, id ->
            val chip = chipGroup.findViewById<Chip>(id)

            if (chip != null) {
                for (i in 0 until chipGroup.childCount) {
                    chipGroup.getChildAt(i).isClickable = true
                }
                chip.isClickable = false

                Timber.e("CHIP SELECTED TEXT ${chip.text.toString()}")
                schoolPagingAdapter?.updateOnlyChipText(chip.text.toString())

                when (chip.text) {
                    "All" -> {
                        viewModel._schoolPagingData.value?.let {
                            schoolPagingAdapter?.submitData(lifecycle,
                                it
                            )
                        }
                    }

                    "Visited" -> {
                        viewModel._schoolPagingData.value?.filter {it.status == "Visited"}
                            ?.let { schoolPagingAdapter?.submitData(lifecycle, it) }
                    }

                    "Assigned" -> {
                        viewModel._schoolPagingData.value?.filter {it.status == "Assigned"}
                            ?.let { schoolPagingAdapter?.submitData(lifecycle, it) }
                    }

                    "Propose Costing" -> {
                        viewModel._schoolPagingData.value?.filter {it.status == "Propose Costing"}
                            ?.let { schoolPagingAdapter?.submitData(lifecycle, it) }
                    }

                    "MOA Signed" -> {
                        schoolPagingAdapter?.updateOnlyChipText("MOASigned")
                        viewModel._schoolPagingData.value?.filter {it.status == "MOASigned"}
                            ?.let { schoolPagingAdapter?.submitData(lifecycle, it) }
                    }

                    "Installment" -> {
                        viewModel._schoolPagingData.value?.filter {it.status == "Installment"}
                            ?.let { schoolPagingAdapter?.submitData(lifecycle, it) }
                    }

                    "Not Interested" -> {
                        viewModel._schoolPagingData.value?.filter {it.status == "Not Interested"}
                            ?.let { schoolPagingAdapter?.submitData(lifecycle, it) }
                    }

                    else -> {
                        viewModel._schoolPagingData.value?.filter {it.status == chip.text.toString()}
                            ?.let { schoolPagingAdapter?.submitData(lifecycle, it) }
                    }
                }


                Timber.e("LOAD COUNT")
                Timber.e(schoolPagingAdapter?.itemCount.toString())
                checkListSize(schoolPagingAdapter?.itemCount?:0)
            }
        }

        binding.addSchool.setOnClickListener {
            it.findNavController()
                .navigate(HomeFragmentDirections.actionHomeFragmentToAddSchoolFragment(null))
        }
    }

    private fun initializeAdapter() {
        schoolPagingAdapter = SchoolPagingAdapter(this)
        binding.homeRecyclerView.adapter = schoolPagingAdapter
    }


    override fun onDestroyView() {
        _binding = null
        schoolPagingAdapter = null
        super.onDestroyView()
        Timber.e("onDestroyView")
    }

    private fun setOnBackPressed() {
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                requireActivity().finishAffinity()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(callback)
    }

    override fun onStart() {
        super.onStart()
        Timber.e("onStart")
    }
    override fun onStop() {
        super.onStop()
        (requireActivity() as MainActivity).removeMenuProvider(this)
        activity?.removeMenuProvider(this)
    }

    private fun setActionBar() {
        Timber.e("setActionBar")
        (requireActivity() as MainActivity).setSupportActionBar(binding.homeTopBar)
        activity?.addMenuProvider(this)
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.home_top_bar_menu, menu)
        menu.findItem(R.id.menu_home).isVisible = false
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

            R.id.menu_home -> {
                findNavController().navigate(R.id.homeFragment)
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