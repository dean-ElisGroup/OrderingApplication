package com.elis.orderingapplication.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.elis.orderingapplication.R
import com.elis.orderingapplication.model.OrderingLoginResponseStruct
import com.elis.orderingapplication.databinding.FragmentLandingPageBinding

class LoginAdapter : RecyclerView.Adapter<LoginAdapter.LoginHolder>() {

    inner class LoginHolder(sessionKey: View) : RecyclerView.ViewHolder(sessionKey)

    private val differCallBack = object : DiffUtil.ItemCallback<OrderingLoginResponseStruct>() {
        override fun areItemsTheSame(
            oldItem: OrderingLoginResponseStruct,
            newItem: OrderingLoginResponseStruct
        ): Boolean {
            return oldItem.sessionKey == newItem.sessionKey
        }

        override fun areContentsTheSame(
            oldItem: OrderingLoginResponseStruct,
            newItem: OrderingLoginResponseStruct
        ): Boolean {
            return oldItem == newItem
        }

    }

    private val differ = AsyncListDiffer(this, differCallBack)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LoginHolder {
        return LoginHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.fragment_landing_page, parent, false)
        )

    }

    override fun onBindViewHolder(holder: LoginHolder, position: Int) {
        val loginSession = differ.currentList[position]
        holder.itemView.apply {
            FragmentLandingPageBinding.bind(this).apply {
                sessionKey.text = loginSession.sessionKey
            }
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }
}