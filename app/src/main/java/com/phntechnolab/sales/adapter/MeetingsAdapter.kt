package com.phntechnolab.sales.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.phntechnolab.sales.databinding.ActivityesItemBinding
import com.phntechnolab.sales.model.MeetingData
import com.phntechnolab.sales.viewHolder.ActivitiesViewHolder

class MeetingsAdapter(var callback: CallBacks) : Adapter<ActivitiesViewHolder>() {
    private var data = ArrayList<MeetingData>()
    private lateinit var context : Context
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActivitiesViewHolder {
        context = parent.context
        val binding =
            ActivityesItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ActivitiesViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: ActivitiesViewHolder, position: Int) {
        holder.binding.activityName.text = "You have a follow up with ${data[position].data?.schoolName}"
        holder.binding.mainConstraint.setOnClickListener {
            callback.meetingData(data[position])
        }
    }

    fun setData(newData: ArrayList<MeetingData>) {
        val activitiesDiffUtil = ActivitiesDiffUtil(data, newData)
        val activitiesDiff = DiffUtil.calculateDiff(activitiesDiffUtil)
        data = newData
        activitiesDiff.dispatchUpdatesTo(this)

    }

    class ActivitiesDiffUtil(private val oldData: ArrayList<MeetingData>,private val newData: ArrayList<MeetingData>) :
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

    interface CallBacks{
        fun meetingData(data: MeetingData)
    }
}