package com.phntechnolab.sales.fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.phntechnolab.sales.R
import com.phntechnolab.sales.adapter.SchoolDetailAdapter
import com.phntechnolab.sales.databinding.FragmentHomeBinding
import com.phntechnolab.sales.databinding.FragmentSearchBinding
import com.phntechnolab.sales.model.CoordinatorData
import com.phntechnolab.sales.model.DMData
import com.phntechnolab.sales.model.MOADocumentData
import com.phntechnolab.sales.model.ProposeCostingData
import com.phntechnolab.sales.model.SchoolData
import com.phntechnolab.sales.viewmodel.HomeViewModel
import com.phntechnolab.sales.viewmodel.SearchViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchFragment : Fragment(), SchoolDetailAdapter.CallBacks {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<SearchViewModel>()

    private var _adapter: SchoolDetailAdapter? = null
    private val adapter get() = _adapter

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
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initializeAdapter()

        observers()

        textWatchers()
    }

    private fun initializeAdapter() {
        _adapter = SchoolDetailAdapter(this)
        binding.recyclerView.adapter = _adapter
    }

    private fun textWatchers() {
        binding.autoSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                fetchData(s.toString())
            }
        })
    }

    private fun fetchData(schoolName: String) {
        if (!schoolName.isNullOrBlank()) {
            val refereshData = viewModel.schoolLiveData.value?.data?.filter {
                it.schoolName.toLowerCase().contains(schoolName.toLowerCase())
            }?.sortedByDescending { it.updatedAt }
            refereshData?.let { ArrayList(it) }?.let { adapter?.setData(it) }
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
//                adapter?.setData(ArrayList<SchoolData>().apply {  addAll(it.data.sortedByDescending { it.updatedAt }) })
                binding.progressBar.visibility = View.GONE
            }
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
                                ?: ProposeCostingData(schoolId = schoolData.schoolId),
                            schoolData.moaDocumentData
                                ?: MOADocumentData(schoolId = schoolData.schoolId)
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