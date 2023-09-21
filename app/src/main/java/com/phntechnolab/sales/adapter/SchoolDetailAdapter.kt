package com.phntechnolab.sales.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.phntechnolab.sales.R
import com.phntechnolab.sales.fragment.HomeFragmentDirections
import com.phntechnolab.sales.model.SchoolData

class SchoolDetailAdapter(var callBacks: CallBacks): RecyclerView.Adapter<SchoolDetailAdapter.SchoolViewHolder>() {

    private var data = ArrayList<SchoolData>()

    class SchoolViewHolder(view: View): RecyclerView.ViewHolder(view){
        val schoolName = view.findViewById<TextView>(R.id.school_name)
        val txtEmail = view.findViewById<TextView>(R.id.txt_email)
        val txtMono = view.findViewById<TextView>(R.id.txt_mono)
        val editIcon = view.findViewById<ImageView>(R.id.edit_icon)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SchoolViewHolder {
        val binding =
            LayoutInflater.from(parent.context).inflate(R.layout.adapter_home_inline, parent, false)
        return SchoolViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: SchoolViewHolder, position: Int) {
        val schoolDetail = data[position]
        holder.schoolName.text = schoolDetail.schoolName
        holder.txtEmail.text = schoolDetail.email
        holder.txtMono.text = schoolDetail.avgSchoolFees
        holder.editIcon.setOnClickListener {
            if(it != null)
                callBacks.openSchoolDetails(schoolDetail)
        }
    }

    fun setData(newData: ArrayList<SchoolData>) {
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

    interface CallBacks{
        fun openSchoolDetails(schoolData: SchoolData)
    }
}