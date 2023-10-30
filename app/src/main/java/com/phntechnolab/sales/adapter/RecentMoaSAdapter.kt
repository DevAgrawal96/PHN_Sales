package com.phntechnolab.sales.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.phntechnolab.sales.databinding.RecentMoaSItemBinding
import com.phntechnolab.sales.model.RecentMoaSData

class RecentMoaSAdapter(val callBack: CallBack) :
    Adapter<RecentMoaSAdapter.RecentMoaSViewHolder>() {
    private lateinit var context: Context
    private var data = ArrayList<RecentMoaSData>()

    class RecentMoaSViewHolder(val vHBinding: RecentMoaSItemBinding) :
        ViewHolder(vHBinding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecentMoaSViewHolder {
        context = parent.context
        val binding =
            RecentMoaSItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RecentMoaSViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: RecentMoaSViewHolder, position: Int) {
        holder.vHBinding.apply {
            schoolName.text = data[position].schoolName
            schoolAddress.text = data[position].schoolAddress
            totalRevenueValue.text = data[position].revenueAmount
            container.setOnClickListener {
                callBack.recentMoaSData(data[position])
            }
        }
    }

    fun setData(newData: ArrayList<RecentMoaSData>) {
        val recentMoaDiffUtil = RecentMoaDiffUtil(data, newData)
        val recentMoaDiff = DiffUtil.calculateDiff(recentMoaDiffUtil)
        data = newData
        recentMoaDiff.dispatchUpdatesTo(this)
    }

    class RecentMoaDiffUtil(
        private val oldData: ArrayList<RecentMoaSData>,
        private val newData: ArrayList<RecentMoaSData>
    ) : DiffUtil.Callback() {
        override fun getOldListSize(): Int {
            return oldData.size
        }

        override fun getNewListSize(): Int {
            return newData.size
        }

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldData == newData
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldData[oldItemPosition].schoolName == newData[newItemPosition].schoolName
        }

    }

    interface CallBack {
        fun recentMoaSData(data: RecentMoaSData)
    }
}