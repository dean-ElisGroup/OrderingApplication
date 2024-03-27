package com.elis.orderingapplication

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation.findNavController
import androidx.navigation.fragment.navArgs
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
    private val args : PosGroupFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentPosGroupBinding.inflate(inflater)
        binding.lifecycleOwner = this
        binding.orderingGroupViewModel = orderingGroupViewModel
        binding.sharedViewModel = sharedViewModel

        binding.toolbar.title = getString(R.string.pos_group_title)
        binding.toolbar.setNavigationIcon(R.drawable.ic_back)
        binding.toolbar.setNavigationOnClickListener {
            view?.let { it ->
                findNavController(it)
                    .navigate(R.id.action_posGroupFragment_to_deliveryAddressFragment)
            }
        }
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView: RecyclerView = binding.orderingGroupSelection
        val spacingInPixels = resources.getDimensionPixelSize(R.dimen.spacing)
        val itemSpacingDecoration = CardViewDecoration(spacingInPixels)
        recyclerView.addItemDecoration(itemSpacingDecoration)
        sharedViewModel.setOrderingGroups(sharedViewModel.getOrder())

        val orderingGroupList: List<OrderingGroup>? = sharedViewModel.getOrderingGroups()

        val adapter = OrderingGroupAdapter()
        adapter.submitList(orderingGroupList)

        binding.orderingGroupSelection.adapter = adapter
        recyclerView.adapter = adapter

    }
}
