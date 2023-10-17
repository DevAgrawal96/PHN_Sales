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
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.phntechnolab.sales.R
import com.phntechnolab.sales.activity.MainActivity
import com.phntechnolab.sales.adapter.PendingApprovalAdapter
import com.phntechnolab.sales.databinding.FragmentPendingBinding
import com.phntechnolab.sales.model.InstallmentData
import com.phntechnolab.sales.model.MOADocumentData
import com.phntechnolab.sales.model.ProposeCostingData
import com.phntechnolab.sales.model.SchoolData
import com.phntechnolab.sales.util.NetworkResult
import com.phntechnolab.sales.viewmodel.PendingForApprovalViewModel
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber


@AndroidEntryPoint
class PendingForApprovalFragment : Fragment(), MenuProvider, PendingApprovalAdapter.CallBacks {
    private var _binding: FragmentPendingBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<PendingForApprovalViewModel>()

    private var _adapter: PendingApprovalAdapter? = null
    private val adapter get() = _adapter!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.getAllSchools()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPendingBinding.inflate(inflater, container, false)
        initializeAdapter()
        setOnBackPressed()
        return binding.root
    }

    private fun initializeAdapter() {
        _adapter = PendingApprovalAdapter(this)
        binding.pendingApprovalRv.adapter = adapter
    }

    private fun setOnBackPressed() {
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigate(R.id.homeFragment)
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(callback)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setActionBar()
        viewModel.schoolLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is NetworkResult.Success -> {
                    val filterData = ArrayList<SchoolData>().apply {
                        addAll(it.data?.filter { it.status == "MOA Pending" }
                            ?.sortedByDescending { it.updatedAt } ?: ArrayList<SchoolData>())
                    }
                    binding.noInternetConnection.visibility = View.GONE
                    binding.noInternetMessage.visibility = View.GONE
                    if (filterData.isNullOrEmpty()) {
                        binding.noDataLottie.visibility = View.VISIBLE
                        binding.pendingApprovalRv.visibility = View.GONE
//                        binding.progressBar.visibility = View.GONE
                        binding.progressIndicator.visibility = View.GONE
                    } else {
                        binding.pendingApprovalRv.visibility = View.VISIBLE
                        binding.noDataLottie.visibility = View.GONE
                        binding.progressIndicator.visibility = View.GONE
                        adapter.setData(filterData)
//                        binding.progressBar.visibility = View.GONE
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
                        getString(R.string.something_went_wrong) -> {
                            Toast.makeText(
                                requireContext(),
                                getText(R.string.something_went_wrong),
                                Toast.LENGTH_SHORT
                            ).show()
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

    private fun setDataToAdapter(data: ArrayList<SchoolData>) {
        adapter.setData(data)
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
                findNavController().navigate(R.id.action_pendingFragment_to_searchFragment)
                true
            }

            R.id.menu_notification -> {
                findNavController().navigate(R.id.action_pendingFragment_to_notificationFragment)
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        _adapter = null
    }

    override fun meetingNavigation(schoolData: SchoolData) {
//        requireView().findNavController()
//            .navigate(
//                PendingForApprovalFragmentDirections.actionPendingFragmentToMoaSignedFragment(
//                    schoolData.installmentData ?: InstallmentData()
//                )
//            )

        requireView().findNavController()
            .navigate(
                PendingForApprovalFragmentDirections.actionPendingFragmentToCostingMoaDocumentFragment(
                    schoolData.proposeCostingData
                        ?: ProposeCostingData(schoolId = schoolData.schoolId),
                    schoolData.moaDocumentData
                        ?: MOADocumentData(schoolId = schoolData.schoolId)
                )
            )
    }
}