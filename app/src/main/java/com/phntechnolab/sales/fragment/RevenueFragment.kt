package com.phntechnolab.sales.fragment

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import androidx.core.content.ContextCompat
import com.phntechnolab.sales.R
import com.phntechnolab.sales.databinding.FragmentChartAnalysisBinding
import com.phntechnolab.sales.databinding.FragmentRevenueBinding


class RevenueFragment : Fragment() {
    private var _binding: FragmentRevenueBinding? = null
    private val binding get() = _binding!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRevenueBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setChartData()
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