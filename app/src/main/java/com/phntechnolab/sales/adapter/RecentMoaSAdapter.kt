package com.phntechnolab.sales.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.phntechnolab.sales.databinding.RecentMoaSItemBinding

class RecentMoaSAdapter : Adapter<RecentMoaSAdapter.RecentMoaSViewHolder>() {
    private lateinit var context: Context
//    private var data = ArrayList<>()

    class RecentMoaSViewHolder(private val vHBinding: RecentMoaSItemBinding) :
        ViewHolder(vHBinding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecentMoaSViewHolder {
        context = parent.context
        val binding =
            RecentMoaSItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RecentMoaSViewHolder(binding)
    }

    override fun getItemCount(): Int {
        TODO("Not yet implemented")
    }

    override fun onBindViewHolder(holder: RecentMoaSViewHolder, position: Int) {
        TODO("Not yet implemented")
    }
}