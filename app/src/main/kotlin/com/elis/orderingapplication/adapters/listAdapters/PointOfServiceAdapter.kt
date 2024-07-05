package com.elis.orderingapplication.adapters.listAdapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.elis.orderingapplication.databinding.PosCardviewBinding
import com.elis.orderingapplication.pojo2.PointsOfService
import com.elis.orderingapplication.pojo2.PointsOfServiceWithTotalOrders

class PointOfServiceAdapter(
    private val clickListener: MyClickListener,
    private val totalPOSCallback: TotalPOSCallback
) :
    RecyclerView.Adapter<PointOfServiceAdapter.PointsOfServiceViewHolder>() {

    //private var data: List<PointsOfService> = emptyList()
    private var data: List<PointsOfServiceWithTotalOrders> = emptyList()

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
        //val pointsOfService = data[position]
        val pointsOfServiceWithTotalOrders = data[position]
        holder.bind(clickListener, pointsOfServiceWithTotalOrders)
        totalPOSCallback.onTotalPOSUpdated(pointsOfServiceWithTotalOrders.totalPOS)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    fun setData(newData: List<PointsOfServiceWithTotalOrders>) {
        data = newData
        notifyDataSetChanged()
    }


    interface MyClickListener {
        fun onItemClick(myData: PointsOfService)
    }

    class PointsOfServiceViewHolder(private var binding: PosCardviewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(clickListener: MyClickListener, pointsOfService: PointsOfServiceWithTotalOrders) {
            //binding.pos = pointsOfService
            binding.pos = pointsOfService.pointsOfService
            binding.totalOrders = pointsOfService.totalOrders.toString()
            binding.clickListener = clickListener
            binding.executePendingBindings()
        }
    }

    interface TotalPOSCallback {
        fun onTotalPOSUpdated(totalPOS: Int)
    }
}