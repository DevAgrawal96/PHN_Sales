package com.phntechnolab.sales.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.phntechnolab.sales.R
import com.phntechnolab.sales.databinding.FragmentStagingBinding

class StagingFragment : Fragment() {
    private var _binding: FragmentStagingBinding? = null
    private val binding get() = _binding!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentStagingBinding.inflate(inflater, container, false)

        setDataToDropDown()

        return binding.root
    }

    private fun setDataToDropDown() {

        //agreement_duration
        val agreementDuration = resources.getStringArray(R.array.agreement_duration)
        val agreementDurationArrayAdapter =
            ArrayAdapter(requireContext(), R.layout.drop_down_item, agreementDuration)
        binding.autoAgreementDuration.setAdapter(agreementDurationArrayAdapter)

        //meeting_with_whom
        val meetingWithWhom = resources.getStringArray(R.array.meeting_with_whom)
        val meetingWithWhomArrayAdapter =
            ArrayAdapter(requireContext(), R.layout.drop_down_item, meetingWithWhom)
        binding.autoMeetingWithWhom.setAdapter(meetingWithWhomArrayAdapter)

        //conversion_ratio
        val conversionRatio = resources.getStringArray(R.array.conversion_ratio)
        val conversionRatioArrayAdapter =
            ArrayAdapter(requireContext(), R.layout.drop_down_item, conversionRatio)
        binding.autoConversionRatio.setAdapter(conversionRatioArrayAdapter)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}