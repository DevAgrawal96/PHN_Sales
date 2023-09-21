package com.phntechnolab.sales.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.phntechnolab.sales.databinding.ProfileSettingItemBinding
import com.phntechnolab.sales.model.SettingModel
import com.phntechnolab.sales.viewHolder.ProfileSettingViewHolder
import timber.log.Timber

class ProfileSettingAdapter(val callback : Callback) : Adapter<ProfileSettingViewHolder>() {
    private var data = ArrayList<SettingModel>()
    private lateinit var context: Context
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProfileSettingViewHolder {
        context = parent.context
        val binding =
            ProfileSettingItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProfileSettingViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: ProfileSettingViewHolder, position: Int) {
        holder.binding.settingImg.setImageResource(data[position].settingImg)
        holder.binding.settingName.text = data[position].settingName
        holder.binding.settingDetails.text = data[position].settingDetails
        holder.binding.container.setOnClickListener {
            callback.openSetting(position)
        }
        if (data.size == position+1) {
            Timber.e("position :$position dataSize: ${data.size}")
            holder.binding.divider.visibility = View.GONE
        } else {
            Timber.e("position :$position dataSize: ${data.size}")
            holder.binding.divider.visibility = View.VISIBLE
        }
    }

    fun setData(newData: ArrayList<SettingModel>) {
        val profileSettingDiffUtil = ProfileSettingDiffUtil(data, newData)
        val profileSettingDiff = DiffUtil.calculateDiff(profileSettingDiffUtil)
        data = newData
        profileSettingDiff.dispatchUpdatesTo(this)

    }

    class ProfileSettingDiffUtil(
        private val oldData: ArrayList<SettingModel>,
        private val newData: ArrayList<SettingModel>
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
            return oldData[oldItemPosition].settingName == newData[newItemPosition].settingName
        }

    }
    interface Callback{
        fun openSetting(position : Int)
    }
}