package com.cukurova.cark

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class PrizeAdapter(
    private var prizes: List<Prize>,
    private val onDeleteClick: (Prize) -> Unit
) : RecyclerView.Adapter<PrizeAdapter.PrizeViewHolder>() {

    class PrizeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvName: TextView = view.findViewById(R.id.tvPrizeName)
        val btnDelete: ImageButton = view.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PrizeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_prize, parent, false)
        return PrizeViewHolder(view)
    }

    override fun onBindViewHolder(holder: PrizeViewHolder, position: Int) {
        val prize = prizes[position]
        holder.tvName.text = prize.name
        holder.btnDelete.setOnClickListener { onDeleteClick(prize) }
    }

    override fun getItemCount() = prizes.size

    fun updateData(newPrizes: List<Prize>) {
        prizes = newPrizes
        notifyDataSetChanged()
    }
}