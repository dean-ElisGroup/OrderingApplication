package com.elis.orderingapplication.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.elis.orderingapplication.databinding.PosCardviewBinding
import com.elis.orderingapplication.pojo2.PointsOfService

class PosAdapter(private val clickListener: PosListener) :
    ListAdapter<PointsOfService, PosAdapter.PosViewHolder>(DiffCallback) {


    class PosViewHolder(private var binding: PosCardviewBinding) :
        RecyclerView.ViewHolder(binding.root) {
            fun bind(clickListener: PosListener, pointsOfService: PointsOfService) {
            binding.pos = pointsOfService
            binding.clickListener = clickListener
            binding.executePendingBindings()
        }
    }
    companion object DiffCallback : DiffUtil.ItemCallback<PointsOfService>() {
        override fun areItemsTheSame(oldItem: PointsOfService, newItem: PointsOfService): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(
            oldItem: PointsOfService,
            newItem: PointsOfService
        ): Boolean {
            return oldItem.pointOfServiceNo == newItem.pointOfServiceNo
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PosViewHolder {
        return PosViewHolder(
            PosCardviewBinding.inflate(
                LayoutInflater.from(
                    parent.context
                )
            )
        )
    }

    override fun onBindViewHolder(holder: PosViewHolder, position: Int) {
        val pointsOfService = getItem(position)
        holder.bind(clickListener, pointsOfService)
    }


    class PosListener(val clickListener: (posName: String?) -> Unit) {
        fun onClick(posName: PointsOfService) =
            clickListener(posName.pointOfServiceName)
    }


}



