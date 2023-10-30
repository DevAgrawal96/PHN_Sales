package com.phntechnolab.sales.fragment

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.activity.OnBackPressedCallback
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.phntechnolab.sales.R
import com.phntechnolab.sales.adapter.RecentMoaSAdapter
import com.phntechnolab.sales.databinding.FragmentChartAnalysisBinding
import com.phntechnolab.sales.databinding.FragmentRevenueBinding
import com.phntechnolab.sales.model.RecentMoaSData


class RevenueFragment : Fragment() {
    private var _binding: FragmentRevenueBinding? = null
    private val binding get() = _binding!!

    private var _adapter: RecentMoaSAdapter? = null
    private val adapter get() = _adapter!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRevenueBinding.inflate(layoutInflater)
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
        setChartData()
        initializeUI()
        setAdapterData()
        setDropDownData()
        initializeListener()
    }

    private fun initializeListener() {
        binding.autoTimeFrame.setOnItemClickListener { parent, view, position, id ->
            binding.lastMonths.text = parent.adapter.getItem(position) as String
        }
    }

    private fun setDropDownData() {
        val items = ArrayList<String>().apply {
            add("Last 3 months")
            add("Last 6 months")
            add("Last 9 months")
        }
        binding.autoTimeFrame.setText(items[0])

        val adapter: ArrayAdapter<String> =
            ArrayAdapter<String>(
                requireContext(),
                android.R.layout.simple_spinner_dropdown_item,
                items
            )
        binding.autoTimeFrame.setAdapter(adapter)
    }

    private fun setAdapterData() {
        adapter.setData(ArrayList<RecentMoaSData>().apply {
            add(
                RecentMoaSData(
                    "St Mira’s School",
                    "MG Road, Camp",
                    requireContext().resources.getString(R.string.total_revenue_value, "10000"),
                    ""
                )
            )
            add(
                RecentMoaSData(
                    "St Mira’s School",
                    "MG Road, Camp",
                    requireContext().resources.getString(R.string.total_revenue_value, "10000"),
                    ""
                )
            )
            add(
                RecentMoaSData(
                    "St Mira’s School",
                    "MG Road, Camp",
                    requireContext().resources.getString(R.string.total_revenue_value, "10000"),
                    ""
                )
            )
            add(
                RecentMoaSData(
                    "St Mira’s School",
                    "MG Road, Camp",
                    requireContext().resources.getString(R.string.total_revenue_value, "10000"),
                    ""
                )
            )
        })
    }

    private fun initializeUI() {
        binding.totalRevenueValue.text = requireContext().resources.getString(R.string.total_revenue_value,"99999999")
        binding.totalRevenueValueOfMonth.text = requireContext().resources.getString(R.string.total_revenue_value,"9999999999")
        binding.topBar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
        val callback = object : RecentMoaSAdapter.CallBack {
            override fun recentMoaSData(data: RecentMoaSData) {
            }
        }
        _adapter = RecentMoaSAdapter(callback)
        binding.recentMoaSRv.adapter = adapter
    }

    private fun setChartData() {
        //line chart
        val lineValues = ArrayList<Entry>().apply {
            add(Entry(10f, 20F))
            add(Entry(20f, 10F))
            add(Entry(30f, 40F))
            add(Entry(40f, 100F))
            add(Entry(50f, 5F))
        }

        val xAxisLabel = ArrayList<String>().apply {
            add("Week 1")
            add("Week 2")
            add("Week 3")
            add("Week 4")
            add("Week 5")
        }

        val lineDataset = LineDataSet(lineValues, "Visited School").apply {
            color = ContextCompat.getColor(requireContext(), R.color.dark_green)
            circleRadius = 0f
            setDrawFilled(true)
            valueTextSize = 12F
            valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    return value.toInt().toString()
                }

                override fun getAxisLabel(value: Float, axis: AxisBase?): String {
                    return xAxisLabel[value.toInt()]
                }
            }
            lineWidth = 2f
            fillColor = ContextCompat.getColor(requireContext(), R.color.light_green)
            mode = LineDataSet.Mode.CUBIC_BEZIER;
        }

        //We connect our data to the UI Screen
        binding.getTheGraph.apply {
            data = LineData(lineDataset)
            description.isEnabled = false
            setDrawBorders(false)
            axisLeft.isEnabled = false
            axisLeft.setDrawGridLines(false)
            axisLeft.setDrawAxisLine(false)
            axisRight.isEnabled = false
            axisRight.setDrawGridLines(false)
            axisRight.setDrawAxisLine(false)
            xAxis.setDrawGridLines(false)
            xAxis.isEnabled = false
            setBackgroundColor(
                Color.WHITE
            )
            animateXY(2000, 2000, Easing.EaseInCubic)
        }
    }

}