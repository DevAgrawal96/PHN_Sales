package com.phntechnolab.sales.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.navigation.fragment.findNavController
import com.phntechnolab.sales.R
import com.phntechnolab.sales.adapter.PendingApprovalAdapter
import com.phntechnolab.sales.adapter.ProfileSettingAdapter
import com.phntechnolab.sales.databinding.FragmentPendingBinding
import com.phntechnolab.sales.model.PendingApprovalModel


class PendingFragment : Fragment() {
    private var _binding: FragmentPendingBinding? = null
    private val binding get() = _binding!!

    private var _adapter : PendingApprovalAdapter? = null
    private val adapter get() = _adapter!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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
        _adapter = PendingApprovalAdapter()
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
        setDataToAdapter()
    }

    private fun setDataToAdapter() {
        adapter.setData(ArrayList<PendingApprovalModel>().apply {
            add(PendingApprovalModel(R.drawable.pending_demo_img,resources.getString(R.string.demo_text)))
            add(PendingApprovalModel(R.drawable.pending_demo_img,resources.getString(R.string.demo_text)))
            add(PendingApprovalModel(R.drawable.pending_demo_img,resources.getString(R.string.demo_text)))
            add(PendingApprovalModel(R.drawable.pending_demo_img,resources.getString(R.string.demo_text)))
            add(PendingApprovalModel(R.drawable.pending_demo_img,resources.getString(R.string.demo_text)))
            add(PendingApprovalModel(R.drawable.pending_demo_img,resources.getString(R.string.demo_text)))
            add(PendingApprovalModel(R.drawable.pending_demo_img,resources.getString(R.string.demo_text)))
            add(PendingApprovalModel(R.drawable.pending_demo_img,resources.getString(R.string.demo_text)))
            add(PendingApprovalModel(R.drawable.pending_demo_img,resources.getString(R.string.demo_text)))
            add(PendingApprovalModel(R.drawable.pending_demo_img,resources.getString(R.string.demo_text)))
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        _adapter = null
    }
}