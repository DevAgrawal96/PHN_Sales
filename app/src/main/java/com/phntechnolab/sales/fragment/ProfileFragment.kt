package com.phntechnolab.sales.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.phntechnolab.sales.R
import com.phntechnolab.sales.adapter.ProfileSettingAdapter
import com.phntechnolab.sales.databinding.FragmentProfileBinding
import com.phntechnolab.sales.model.SettingModel


class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private var _adapter : ProfileSettingAdapter? = null
    private val adapter get() = _adapter!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)

        initializeAdapter()

        return binding.root
    }

    private fun initializeAdapter() {
        _adapter = ProfileSettingAdapter()
        binding.settingRv.adapter = adapter
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setDataToAdapter()
    }

    private fun setDataToAdapter() {
        adapter.setData(ArrayList<SettingModel>().apply {
            add(SettingModel(R.drawable.my_account,resources.getString(R.string.my_account),""))
            add(SettingModel(R.drawable.my_account,resources.getString(R.string.my_account),""))
            add(SettingModel(R.drawable.my_account,resources.getString(R.string.my_account),""))
            add(SettingModel(R.drawable.my_account,resources.getString(R.string.my_account),""))
            add(SettingModel(R.drawable.my_account,resources.getString(R.string.my_account),""))
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}