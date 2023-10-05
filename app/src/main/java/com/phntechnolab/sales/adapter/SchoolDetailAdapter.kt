package com.phntechnolab.sales.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.phntechnolab.sales.R
import com.phntechnolab.sales.databinding.AdapterHomeInlineBinding
import com.phntechnolab.sales.fragment.HomeFragmentDirections
import com.phntechnolab.sales.model.SchoolData

class SchoolDetailAdapter(private var callBacks: CallBacks) :
    RecyclerView.Adapter<SchoolDetailAdapter.SchoolViewHolder>() {

    private var data = ArrayList<SchoolData>()

    class SchoolViewHolder(val binding: AdapterHomeInlineBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SchoolViewHolder {
        val binding = AdapterHomeInlineBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return SchoolViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: SchoolViewHolder, position: Int) {
        val schoolDetail = data[position]
        holder.binding.schoolName.text = schoolDetail.schoolName
        holder.binding.txtEmail.text = schoolDetail.email
        holder.binding.txtMono.text = schoolDetail.coMobileNo
        holder.binding.chipStatus.text = schoolDetail.status
        if(schoolDetail.leadType?.isNotBlank() == true && schoolDetail.leadType?.isNotEmpty() == true) {
            holder.binding.chipLeadStatus.text = schoolDetail.leadType
            holder.binding.chipLeadStatus.visibility = View.VISIBLE
        }else{
            holder.binding.chipLeadStatus.visibility = View.GONE
        }
        holder.binding.cardView.setOnClickListener {
            callBacks.meetingNavigation(schoolDetail)
        }
        holder.binding.editIcon.setOnClickListener {
            if (it != null)
                callBacks.openSchoolDetails(schoolDetail)
        }
    }

    fun setData(newData: ArrayList<SchoolData>) {
        Log.e("OldData", Gson().toJson(data))
        Log.e("newData", Gson().toJson(newData))
        val detailsDiffUtil = SchoolsDataDiffUtil(data, newData)
        val detailsDiff = DiffUtil.calculateDiff(detailsDiffUtil)
        data.clear()
        data.addAll(newData)
        detailsDiff.dispatchUpdatesTo(this)
    }

    class SchoolsDataDiffUtil(
        private val oldData: ArrayList<SchoolData>,
        private val newData: ArrayList<SchoolData>
    ) :
        DiffUtil.Callback() {
        override fun getOldListSize(): Int {
            return oldData.size
        }

        override fun getNewListSize(): Int {
            return newData.size
        }

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldData[oldItemPosition] == newData[newItemPosition]
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldData[oldItemPosition] == newData[newItemPosition]
        }

    }

    interface CallBacks {
        fun openSchoolDetails(schoolData: SchoolData)

        fun meetingNavigation(schoolData: SchoolData)
    }
}