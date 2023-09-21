package com.phntechnolab.sales.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.phntechnolab.sales.databinding.PendingAdapterItemBinding
import com.phntechnolab.sales.model.PendingApprovalModel
import com.phntechnolab.sales.viewHolder.PendingApprovalViewHolder

class PendingApprovalAdapter : Adapter<PendingApprovalViewHolder>() {
private var data = ArrayList<PendingApprovalModel>()
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
        holder.binding.pendingTitle.text = data[position].pendingTitle
        holder.binding.schoolImg.setImageResource(data[position].schoolImg)
    }

    fun setData(newData: ArrayList<PendingApprovalModel>) {
        val pendingApprovalDiffUtil = PendingApprovalDiffUtil(data, newData)
        val pendingApprovalDiff = DiffUtil.calculateDiff(pendingApprovalDiffUtil)
        data = newData
        pendingApprovalDiff.dispatchUpdatesTo(this)

    }

    class PendingApprovalDiffUtil(private val oldData: ArrayList<PendingApprovalModel>,private val newData: ArrayList<PendingApprovalModel>) :
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
            return oldData[oldItemPosition].pendingTitle == newData[newItemPosition].pendingTitle
        }

    }
}