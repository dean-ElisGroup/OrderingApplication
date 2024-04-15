package com.elis.orderingapplication

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
import com.elis.orderingapplication.adapters.PosAdapter
import com.elis.orderingapplication.databinding.FragmentPosBinding
import com.elis.orderingapplication.pojo2.PointsOfService
import com.elis.orderingapplication.viewModels.ParamsViewModel
import com.elis.orderingapplication.viewModels.PosViewModel
import android.widget.SearchView
import com.elis.orderingapplication.pojo2.Order
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class ArticleFragment : Fragment() {

    private lateinit var binding: FragmentPosBinding
    private val sharedViewModel: ParamsViewModel by activityViewModels()
    private val posViewModel: PosViewModel by activityViewModels()
    private val args: PosFragmentArgs by navArgs()

    // private lateinit var searchView: SearchView
    private lateinit var recyclerView: RecyclerView


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_pos, container, false)

        binding.sharedViewModel = sharedViewModel
        binding.posViewModel = posViewModel
        binding.toolbar.title = getString(R.string.pos_title)
        binding.toolbar.setNavigationIcon(R.drawable.ic_back)
        binding.toolbar.setNavigationOnClickListener {
            view?.let { it ->
                Navigation.findNavController(it)
                    .navigate(R.id.action_posFragment_to_posGroupFragment)
            }
        }
        // Sets ordering group and ordering name to shared ViewModel
        sharedViewModel.setOrderingGroupNo(args.orderingGroupNo)
        sharedViewModel.setOrderingGroupName(args.orderingGroupName)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = binding.posSelection

        val spacingInPixels = resources.getDimensionPixelSize(R.dimen.spacing)
        val itemSpacingDecoration = CardViewDecoration(spacingInPixels)
        recyclerView.addItemDecoration(itemSpacingDecoration)

        val posList = sharedViewModel.getPos()
        val filteredPosList: List<PointsOfService>? =
            posList?.filter { it.pointOfServiceOrderingGroupNo == sharedViewModel.orderingGroupNo.value }
        filteredPosList?.size?.let { sharedViewModel.setPOSTotal(it) }

        sharedViewModel.setFilteredPointsOfService(filteredPosList)

        var filteredOrders: List<Order>? = filteredPosList?.find { it.orders!!.isNotEmpty() }?.orders

        var totalOrders: Int? = filteredOrders?.size

        /*filteredOrders?.forEach { order ->
            if (totalArticle != null) {
                order.articles?.let {
                    totalArticle += it.size
                }
            }
        }*/

        /*var orderDate = LocalDate.now()
        var orderDateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        var orderDateFormatted = orderDate.format(orderDateFormatter)

        val orders: List<Order>? = filteredPosList?.flatMap { it.orders!!.toList()}
        val filteredOrders = orders?.filter { it.orderDate == orderDateFormatted && it.orderType =="inventory" }
        */
        val adapter =
            PosAdapter(PosAdapter.PosListener { pos ->
                posViewModel.onPosClicked(pos)
                posViewModel.navigateToPos.observe(
                    viewLifecycleOwner,
                    Observer { pos ->
                        pos?.let {
                            this.findNavController().navigate(
                                PosFragmentDirections.actionPosFragmentToOrderFragment(pos.pointOfServiceNo
                                )
                            )
                            posViewModel.onPosNavigated()
                        }
                    })
            })
        adapter.submitList(filteredPosList)

        binding.posSelection.adapter = adapter
        recyclerView.adapter = adapter

    }

}