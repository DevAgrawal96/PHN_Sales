package com.phntechnolab.sales.paging

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.paging.PagingData
import androidx.paging.PagingDataAdapter
import androidx.paging.filter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders
import com.phntechnolab.sales.R
import com.phntechnolab.sales.adapter.SchoolDetailAdapter
import com.phntechnolab.sales.databinding.AdapterHomeInlineBinding
import com.phntechnolab.sales.model.SchoolData
import timber.log.Timber

class SchoolPagingAdapter(private var callBacks: CallBacks) :
    PagingDataAdapter<SchoolData, SchoolPagingAdapter.ViewHolder>(COMPARATOR) {

    private var data = ArrayList<SchoolData>()

    private var context: Context? = null

    private var selectedChip: String? = null

    class ViewHolder(val binding: AdapterHomeInlineBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val schoolDetail = getItem(position)
        val status = schoolDetail?.status == selectedChip

        Timber.e("chip selected text apdater ${selectedChip}")
        if (schoolDetail?.status != "MOA Pending") {
            if (schoolDetail != null && (status || selectedChip == "All")) {
                holder.binding.cardView.visibility = View.VISIBLE
                holder.binding.schoolName.text = schoolDetail.schoolName
                holder.binding.txtEmail.text = schoolDetail.email
                holder.binding.txtMono.text = schoolDetail.coMobileNo
                holder.binding.chipStatus.text = schoolDetail.status
                if (schoolDetail.email.isNullOrEmpty()) {
                    holder.binding.txtEmail.visibility = View.GONE
                    holder.binding.emailIcon.visibility = View.GONE
                } else {
                    holder.binding.txtEmail.visibility = View.VISIBLE
                    holder.binding.emailIcon.visibility = View.VISIBLE
                }
                if (schoolDetail.coMobileNo.isNullOrEmpty()) {
                    holder.binding.txtMono.visibility = View.GONE
                    holder.binding.callIcon.visibility = View.GONE

                } else {
                    holder.binding.txtMono.visibility = View.VISIBLE
                    holder.binding.callIcon.visibility = View.VISIBLE
                }
                if (schoolDetail.schoolImage?.isNotEmpty() == true && schoolDetail.schoolImage?.isNotEmpty() == true) {
                    val image = GlideUrl(
                        schoolDetail.schoolImage, LazyHeaders.Builder()
                            .addHeader("User-Agent", "5")
                            .build()
                    )
                    Glide.with(context!!).load(image).override(300, 200)
                        .error(R.drawable.demo_img).into(holder.binding.schoolImg)
                }

                if (schoolDetail.leadType?.isNotBlank() == true && schoolDetail.leadType?.isNotEmpty() == true) {
                    holder.binding.chipLeadStatus.text = schoolDetail.leadType
                    holder.binding.chipLeadStatus.visibility = View.VISIBLE
                } else {
                    holder.binding.chipLeadStatus.visibility = View.GONE
                }

                if (schoolDetail.status == "MOASigned") {
                    holder.binding.editIcon.visibility = View.GONE
                } else {
                    holder.binding.editIcon.visibility = View.VISIBLE
                }

                holder.binding.cardView.setOnClickListener {
                    callBacks.meetingNavigation(schoolDetail)
                }

                holder.binding.editIcon.setOnClickListener {
                    if (it != null)
                        callBacks.openSchoolDetails(schoolDetail)
                }
            } else {
                holder.binding.cardView.visibility = View.GONE
            }
        } else {
        }
    }

    fun updateOnlyChipText(text: String) {
        this.selectedChip = text

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            AdapterHomeInlineBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        context = parent.context
        return ViewHolder(binding)
    }

    companion object {
        private val COMPARATOR = object : DiffUtil.ItemCallback<SchoolData>() {
            override fun areItemsTheSame(oldItem: SchoolData, newItem: SchoolData): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: SchoolData, newItem: SchoolData): Boolean {
                return oldItem.schoolName == newItem.schoolName
            }

        }
    }

    interface CallBacks {
        fun openSchoolDetails(schoolData: SchoolData)

        fun meetingNavigation(schoolData: SchoolData)
    }
}