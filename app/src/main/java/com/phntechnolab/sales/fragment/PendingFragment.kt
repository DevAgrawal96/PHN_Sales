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
import androidx.navigation.fragment.findNavController
import com.phntechnolab.sales.R
import com.phntechnolab.sales.activity.MainActivity
import com.phntechnolab.sales.adapter.PendingApprovalAdapter
import com.phntechnolab.sales.adapter.ProfileSettingAdapter
import com.phntechnolab.sales.databinding.FragmentPendingBinding
import com.phntechnolab.sales.model.PendingApprovalModel


class PendingFragment : Fragment(), MenuProvider {
    private var _binding: FragmentPendingBinding? = null
    private val binding get() = _binding!!

    private var _adapter: PendingApprovalAdapter? = null
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
        setActionBar()
        setDataToAdapter()
    }

    private fun setDataToAdapter() {
        adapter.setData(ArrayList<PendingApprovalModel>().apply {
            add(
                PendingApprovalModel(
                    R.drawable.pending_demo_img,
                    resources.getString(R.string.demo_text)
                )
            )
            add(
                PendingApprovalModel(
                    R.drawable.pending_demo_img,
                    resources.getString(R.string.demo_text)
                )
            )
            add(
                PendingApprovalModel(
                    R.drawable.pending_demo_img,
                    resources.getString(R.string.demo_text)
                )
            )
            add(
                PendingApprovalModel(
                    R.drawable.pending_demo_img,
                    resources.getString(R.string.demo_text)
                )
            )
            add(
                PendingApprovalModel(
                    R.drawable.pending_demo_img,
                    resources.getString(R.string.demo_text)
                )
            )
            add(
                PendingApprovalModel(
                    R.drawable.pending_demo_img,
                    resources.getString(R.string.demo_text)
                )
            )
            add(
                PendingApprovalModel(
                    R.drawable.pending_demo_img,
                    resources.getString(R.string.demo_text)
                )
            )
            add(
                PendingApprovalModel(
                    R.drawable.pending_demo_img,
                    resources.getString(R.string.demo_text)
                )
            )
            add(
                PendingApprovalModel(
                    R.drawable.pending_demo_img,
                    resources.getString(R.string.demo_text)
                )
            )
            add(
                PendingApprovalModel(
                    R.drawable.pending_demo_img,
                    resources.getString(R.string.demo_text)
                )
            )
        })
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
}