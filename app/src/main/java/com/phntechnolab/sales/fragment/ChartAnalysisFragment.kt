package com.phntechnolab.sales.fragment

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.phntechnolab.sales.R
import com.phntechnolab.sales.databinding.FragmentChartAnalysisBinding


class ChartAnalysisFragment : Fragment() {

    private var _binding: FragmentChartAnalysisBinding? = null
    private val binding get() = _binding!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChartAnalysisBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        showChart()
        setDropDown()
        initializeListener()
    }

    private fun initializeListener() {
        binding.topBar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        binding.autoTimeFrame.setOnItemClickListener { parent, view, position, id ->
            parent.adapter.getItem(position) as String
        }
    }

    private fun setDropDown() {
        val dropdown: MaterialAutoCompleteTextView = binding.autoTimeFrame
        val items = ArrayList<String>().apply {
            add("Weekly")
            add("Monthly")
        }
        binding.autoTimeFrame.setText(items[0])

        val adapter: ArrayAdapter<String> =
            ArrayAdapter<String>(
                requireContext(),
                android.R.layout.simple_spinner_dropdown_item,
                items
            )
        dropdown.setAdapter(adapter)
    }

    private fun showChart() {
        //pie chart
        val pieEntries = ArrayList<PieEntry>()
        val label = "School status"

        val schoolStatusAndValueMap: MutableMap<String, Int> = HashMap()
        schoolStatusAndValueMap["Assigned School"] = 200
        schoolStatusAndValueMap["Visited School"] = 230
        schoolStatusAndValueMap["Propose Costing"] = 100
        schoolStatusAndValueMap["MOA’s Signed"] = 500
        schoolStatusAndValueMap["Pending for approval"] = 50

        val colors = ArrayList<Int>().apply {
            add(Color.parseColor("#FF6E42"))
            add(Color.parseColor("#C5E363"))
            add(Color.parseColor("#FFAD42"))
            add(Color.parseColor("#4BB963"))
            add(Color.parseColor("#FFD642"))
        }


        for (type in schoolStatusAndValueMap.keys) {
            pieEntries.add(PieEntry(schoolStatusAndValueMap[type]!!.toFloat(), type))
        }

        val pieDataSet = PieDataSet(pieEntries, label)
        pieDataSet.valueTextSize = 12f
        pieDataSet.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return "${value.toInt()} %"
            }
        }
        pieDataSet.valueTextColor = Color.WHITE
        pieDataSet.colors = colors
        val pieData = PieData(pieDataSet)
        binding.pieChart.data = pieData
        binding.pieChart.isRotationEnabled = false
        binding.pieChart.setDrawEntryLabels(false)
        binding.pieChart.invalidate()


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
            lineWidth = 3f
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
            setBackgroundColor(
                Color.WHITE
            )
            animateXY(2000, 2000, Easing.EaseInCubic)
        }

    }


}