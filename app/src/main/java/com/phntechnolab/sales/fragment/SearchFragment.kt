package com.phntechnolab.sales.fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders
import com.phntechnolab.sales.R
import com.phntechnolab.sales.adapter.GenericAdapter
import com.phntechnolab.sales.databinding.AdapterHomeInlineBinding
import com.phntechnolab.sales.databinding.FragmentSearchBinding
import com.phntechnolab.sales.model.CoordinatorData
import com.phntechnolab.sales.model.DMData
import com.phntechnolab.sales.model.MOADocumentData
import com.phntechnolab.sales.model.ProposeCostingData
import com.phntechnolab.sales.model.SchoolData
import com.phntechnolab.sales.util.setupUI
import com.phntechnolab.sales.viewmodel.SearchViewModel
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<SearchViewModel>()

    private var _adapter: GenericAdapter<SchoolData, AdapterHomeInlineBinding>? = null
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
        setBackPressed()
        return binding.root
    }

    private fun setBackPressed() {
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().popBackStack()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(callback)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI(view)

        initializeAdapter()

        observers()

        textWatchers()
    }

    private fun initializeAdapter() {
        _adapter = GenericAdapter(AdapterHomeInlineBinding::inflate, onBind = {schoolDetail,adapterBinding,position,listSize->
            adapterBinding.apply {

                schoolName.text = schoolDetail.schoolName
                txtEmail.text = schoolDetail.email
                txtMono.text = schoolDetail.coMobileNo
                chipStatus.text = schoolDetail.status
                Log.e("Image url", schoolDetail.schoolImage ?: "")
                if (schoolDetail.email.isNullOrEmpty()) {
                    txtEmail.visibility = View.GONE
                    emailIcon.visibility = View.GONE
                } else {
                    txtEmail.visibility = View.VISIBLE
                    emailIcon.visibility = View.VISIBLE
                }
                if (schoolDetail.coMobileNo.isNullOrEmpty()) {
                    txtMono.visibility = View.GONE
                    callIcon.visibility = View.GONE

                } else {
                    txtMono.visibility = View.VISIBLE
                    callIcon.visibility = View.VISIBLE
                }
                if (schoolDetail.schoolImage?.isNotEmpty() == true && schoolDetail.schoolImage?.isNotEmpty() == true) {
                    val image = GlideUrl(
                        schoolDetail.schoolImage, LazyHeaders.Builder()
                            .addHeader("User-Agent", "5")
                            .build()
                    )
                    Glide.with(requireContext()).load(image).override(300, 200)
                        .error(R.drawable.demo_img).into(schoolImg)
                }

                if (schoolDetail.leadType?.isNotBlank() == true && schoolDetail.leadType?.isNotEmpty() == true) {
                    chipLeadStatus.text = schoolDetail.leadType
                    chipLeadStatus.visibility = View.VISIBLE
                } else {
                    chipLeadStatus.visibility = View.GONE
                }
                cardView.setOnClickListener {
                    meetingNavigation(schoolDetail)
                }

                if (schoolDetail.status == "MOASigned") {
                    editIcon.visibility = View.GONE
                } else {
                    editIcon.visibility = View.VISIBLE
                }
                editIcon.setOnClickListener {
                    if (it != null)
                        openSchoolDetails(schoolDetail)
                }
            }
        })
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
                if (!query.isNullOrEmpty()) {
                    fetchData(query.toString())
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (!newText.isNullOrEmpty()) {
                    fetchData(newText.toString())
                } else {
                    Timber.e("null")
                }
                return true
            }
        })


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
                (it.schoolName?.toLowerCase()?:"").contains(schoolName?.toLowerCase()?:"")
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


    private fun openSchoolDetails(schoolData: SchoolData) {
        findNavController()
            .navigate(SearchFragmentDirections.actionSearchFragmentToAddSchoolFragment(schoolData))
    }

    private  fun meetingNavigation(schoolData: SchoolData) {
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
                                ?: ProposeCostingData().apply {
                                    this.schoolId = schoolData.schoolId?:""
                                },
                            schoolData.moaDocumentData
                                ?: MOADocumentData().apply {
                                    this.schoolId = schoolData.schoolId?:""
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