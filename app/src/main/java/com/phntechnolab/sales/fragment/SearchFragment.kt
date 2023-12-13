package com.phntechnolab.sales.fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.SearchView
import androidx.activity.OnBackPressedCallback
import androidx.core.view.MenuProvider
import androidx.databinding.adapters.SearchViewBindingAdapter.setOnQueryTextListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.phntechnolab.sales.R
import com.phntechnolab.sales.activity.MainActivity
import com.phntechnolab.sales.adapter.SchoolDetailAdapter
import com.phntechnolab.sales.databinding.FragmentHomeBinding
import com.phntechnolab.sales.databinding.FragmentSearchBinding
import com.phntechnolab.sales.model.CoordinatorData
import com.phntechnolab.sales.model.DMData
import com.phntechnolab.sales.model.MOADocumentData
import com.phntechnolab.sales.model.ProposeCostingData
import com.phntechnolab.sales.model.SchoolData
import com.phntechnolab.sales.util.Debouncer
import com.phntechnolab.sales.util.NetworkResult
import com.phntechnolab.sales.util.setBackPressed
import com.phntechnolab.sales.util.setupUI
import com.phntechnolab.sales.viewmodel.HomeViewModel
import com.phntechnolab.sales.viewmodel.SearchViewModel
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class SearchFragment : Fragment(), SchoolDetailAdapter.CallBacks {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<SearchViewModel>()

    private var _adapter: SchoolDetailAdapter? = null
    private val adapter get() = _adapter

    @Inject
    lateinit var debouncer: Debouncer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.getAllSchools()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        setBackPressed {
            findNavController().popBackStack()
        }
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI(view)

        initializeAdapter()

        observers()

        textWatchers()

        debouncer.debounce(300) {
            requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
        }


    }

    private fun initializeAdapter() {
        _adapter = SchoolDetailAdapter(this)
        binding.recyclerView.adapter = _adapter
    }

    private fun textWatchers() {
        binding.backBtn.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.search.onActionViewExpanded()
        binding.search.setOnQueryTextListener(object :
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let { debouncer.debounce(300) { fetchData(it) } }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                performSearch(newText)
                return true
            }

            private fun performSearch(query: String?) {
                if (!query.isNullOrBlank()) {
                    query.let { debouncer.debounce(300) { fetchData(it) } }
                } else {
                    adapter?.setData(ArrayList())
                }
            }
        })
    }

    private fun fetchData(schoolName: String) {
        if (schoolName.isNotBlank()) {
            viewModel.searchQueryLiveData.postValue(schoolName)
        } else {
            adapter?.setData(java.util.ArrayList())
        }
    }


    private fun observers() {
        viewModel.schoolLiveData.observe(viewLifecycleOwner) {

            if (it.data?.isEmpty()!!) {
                binding.noDataLottie.visibility = View.VISIBLE
                binding.recyclerView.visibility = View.GONE
                binding.progressBar.visibility = View.GONE
            } else {

                binding.recyclerView.visibility = View.VISIBLE
                binding.noDataLottie.visibility = View.GONE
                binding.progressIndicator.visibility = View.GONE
                binding.progressBar.visibility = View.GONE
            }
        }

        viewModel.filteredSchoolsLiveData.observe(viewLifecycleOwner) { filteredSchools ->
            adapter?.setData(ArrayList<SchoolData>().apply {
                filteredSchools?.toList()?.let { addAll(it) }
            })
        }
    }


    override fun onDestroyView() {
        _binding = null
        _adapter = null
        super.onDestroyView()
    }


    override fun openSchoolDetails(schoolData: SchoolData) {
        findNavController()
            .navigate(SearchFragmentDirections.actionSearchFragmentToAddSchoolFragment(schoolData))
    }

    override fun meetingNavigation(schoolData: SchoolData) {
        when (schoolData.status) {
            "Assigned" -> {
                findNavController()
                    .navigate(
                        SearchFragmentDirections.actionSearchFragmentToAddSchoolFragment(
                            schoolData
                        )
                    )
            }

            "Not Interested" -> {
                requireView().findNavController()
                    .navigate(
                        SearchFragmentDirections.actionSearchFragmentToAddSchoolFragment(
                            schoolData
                        )
                    )
            }

            "Visited" -> {
                requireView().findNavController()
                    .navigate(
                        SearchFragmentDirections.actionSearchFragmentToMeetingFragment(
                            schoolData.coordinator
                                ?: CoordinatorData(schoolId = schoolData.schoolId),
                            schoolData.director ?: DMData(schoolId = schoolData.schoolId)
                        )
                    )
            }

            "Propose Costing" -> {
                requireView().findNavController()
                    .navigate(
                        SearchFragmentDirections.actionSearchFragmentToCostingMoaDocumentFragment(
                            schoolData.proposeCostingData
                                ?: ProposeCostingData().apply {
                                    this.schoolId = schoolData.schoolId ?: ""
                                },
                            schoolData.moaDocumentData
                                ?: MOADocumentData().apply {
                                    this.schoolId = schoolData.schoolId ?: ""
                                }
                        )
                    )
            }

            "MOASigned" -> {
                requireView().findNavController()
                    .navigate(R.id.action_searchFragment_to_moaSignedFragment)
            }
        }
    }
}