package com.elis.orderingapplication.adapters.listAdapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.elis.orderingapplication.databinding.PosCardviewBinding
import com.elis.orderingapplication.pojo2.PointsOfService

class PointOfServiceAdapter(private val clickListener: MyClickListener) :
    RecyclerView.Adapter<PointOfServiceAdapter.PointsOfServiceViewHolder>() {

    private var data: List<PointsOfService> = emptyList()
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PointsOfServiceViewHolder {
        return PointsOfServiceViewHolder(
            PosCardviewBinding.inflate(
                LayoutInflater.from(
                    parent.context
                )
            )
        )
    }

    override fun onBindViewHolder(holder: PointsOfServiceViewHolder, position: Int) {
        val pointsOfService = data[position]
        holder.bind(clickListener, pointsOfService)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    fun setData(newData: List<PointsOfService>) {
        data = newData
        notifyDataSetChanged()
    }

    interface MyClickListener {
        fun onItemClick(myData: PointsOfService)
    }

    class PointsOfServiceViewHolder(private var binding: PosCardviewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(clickListener: MyClickListener, pointsOfService: PointsOfService) {
            binding.pos = pointsOfService
            binding.clickListener = clickListener
            binding.executePendingBindings()
        }
    }
}