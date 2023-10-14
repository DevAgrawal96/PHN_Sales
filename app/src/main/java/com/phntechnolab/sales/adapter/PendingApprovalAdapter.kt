package com.phntechnolab.sales.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.bumptech.glide.Glide
import com.phntechnolab.sales.R
import com.phntechnolab.sales.databinding.PendingAdapterItemBinding
import com.phntechnolab.sales.model.PendingApprovalModel
import com.phntechnolab.sales.model.SchoolData
import com.phntechnolab.sales.viewHolder.PendingApprovalViewHolder

class PendingApprovalAdapter(private var callBacks: CallBacks) : Adapter<PendingApprovalViewHolder>() {
private var data = ArrayList<SchoolData>()
private lateinit var context : Context
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PendingApprovalViewHolder {
        context = parent.context
        val binding =
            PendingAdapterItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PendingApprovalViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: PendingApprovalViewHolder, position: Int) {
        val schoolData = data[position]
        holder.binding.pendingTitle.text = "${ data[position].schoolName.capitalize() } price discussion is pending"
        Glide.with(context!!).load(data[position].schoolImage).override(300,200).error(R.drawable.demo_img).into(holder.binding.schoolImg)
        holder.binding.mainConstraint.setOnClickListener { callBacks.meetingNavigation(schoolData) }
    }

    fun setData(newData: ArrayList<SchoolData>) {
        val pendingApprovalDiffUtil = PendingApprovalDiffUtil(data, newData)
        val pendingApprovalDiff = DiffUtil.calculateDiff(pendingApprovalDiffUtil)
        data = newData
        pendingApprovalDiff.dispatchUpdatesTo(this)

    }

    class PendingApprovalDiffUtil(private val oldData: ArrayList<SchoolData>,private val newData: ArrayList<SchoolData>) :
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
        fun meetingNavigation(schoolData: SchoolData)
    }
}