package com.elis.orderingapplication

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation
import androidx.navigation.Navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.elis.orderingapplication.adapters.OrderingGroupAdapter
import com.elis.orderingapplication.databinding.FragmentPosGroupBinding
import com.elis.orderingapplication.pojo2.OrderingGroup
import com.elis.orderingapplication.viewModels.OrderingGroupViewModel
import com.elis.orderingapplication.viewModels.ParamsViewModel

class PosGroupFragment : Fragment() {

    private lateinit var binding: FragmentPosGroupBinding
    private val sharedViewModel: ParamsViewModel by activityViewModels()
    private val orderingGroupViewModel: OrderingGroupViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //binding =
        //    DataBindingUtil.inflate(inflater, R.layout.fragment_pos_group, container, false)

        binding = FragmentPosGroupBinding.inflate(inflater)
        binding.lifecycleOwner = this
        binding.orderingGroupViewModel = orderingGroupViewModel
        binding.sharedViewModel = sharedViewModel

        binding.toolbar.title = getString(R.string.pos_group_title)
        binding.toolbar.setNavigationIcon(R.drawable.ic_back)
        binding.toolbar.setNavigationOnClickListener {
            view?.let { it ->
                Navigation.findNavController(it)
                    .navigate(R.id.action_posGroupFragment_to_deliveryAddressFragment)
            }
        }
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView: RecyclerView = binding.orderingGroupSelection
        sharedViewModel.setOrderingGroups(sharedViewModel.getOrder())

        val orderingGroupList: List<OrderingGroup>? = sharedViewModel.getOrderingGroups()

        val adapter = OrderingGroupAdapter()
        adapter.submitList(orderingGroupList)

        binding.orderingGroupSelection.adapter = adapter
        recyclerView.adapter = adapter
        adapter.setOnClickListener(object: OrderingGroupAdapter.OnClickListener {
            override fun onClick(position: Int, orderingGroup: OrderingGroup)
            {
                findNavController(view).navigate(R.id.action_posGroupFragment_to_posFragment)
            }
        })

    }
}
}