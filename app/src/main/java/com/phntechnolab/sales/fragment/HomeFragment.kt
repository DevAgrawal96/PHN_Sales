package com.phntechnolab.sales.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.chip.Chip
import com.phntechnolab.sales.Modules.DataStoreProvider
import com.phntechnolab.sales.SchoolDetailAdapter
import com.phntechnolab.sales.databinding.FragmentHomeBinding
import com.phntechnolab.sales.model.SchoolData
import com.phntechnolab.sales.viewmodel.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject


@AndroidEntryPoint
class HomeFragment: Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<HomeViewModel>()

    private var _adapter: SchoolDetailAdapter?= null
    private val adapter get() = _adapter

    @Inject
    lateinit var dataStoreProvider: DataStoreProvider

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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

        getData()

        observers()

        checkedChangeListener()
    }

    private fun checkedChangeListener() {
        binding.chipGroup.setOnCheckedChangeListener { chipGroup, id ->
            val chip = chipGroup.getChildAt(chipGroup.checkedChipId)
            if (chip != null) {
                for (i in 0 until chipGroup.childCount) {
                    chipGroup.getChildAt(i).isClickable = true
                }
                chip.isClickable = false
            }
        }

        binding.addSchool.setOnClickListener {

        }
    }

    private fun observers() {
        viewModel.schoolLiveData.observe(viewLifecycleOwner) {
            adapter?.setData(it.data as ArrayList<SchoolData>)
        }
    }

    private fun initializeAdapter() {
        _adapter = SchoolDetailAdapter()
        binding.homeRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.homeRecyclerView.adapter = _adapter
    }

    private fun getData() {
        lifecycleScope.launch(Dispatchers.IO) {
            Timber.e("APi CALLED")
            viewModel.getAllSchools()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

//    private fun setActionBar() {
//        (requireActivity() as MainActivity).setSupportActionBar(binding.topAppBar)
//    }
    private fun setOnBackPressed() {
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                requireActivity().finishAffinity()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(callback)
    }
}