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

class PosFragment : Fragment() {

    private lateinit var binding: FragmentPosBinding
    private val sharedViewModel: ParamsViewModel by activityViewModels()
    private val posViewModel: PosViewModel by activityViewModels()
    private val args : PosFragmentArgs by navArgs()


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
        sharedViewModel.setOrderingGroupName(args.orderingGroup)
        binding.orderingGroupName = sharedViewModel.orderingGroupName.value
        binding.deliveryAddressName = sharedViewModel.deliveryAddressName.value


        // Inflate the layout for this fragment
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val recyclerView: RecyclerView = binding.posSelection
        val spacingInPixels = resources.getDimensionPixelSize(R.dimen.spacing)
        val itemSpacingDecoration = CardViewDecoration(spacingInPixels)
        recyclerView.addItemDecoration(itemSpacingDecoration)
        //sharedViewModel.setOrderingGroups(sharedViewModel.getOrder())

        val posList: List<PointsOfService>? = sharedViewModel.getPointsOfService()
        val totalPos = posList?.size

        val adapter =
            PosAdapter(PosAdapter.PosListener { pos ->
                posViewModel.onPosClicked(pos)
                posViewModel.navigateToPos.observe(
                    viewLifecycleOwner,
                    Observer { pos ->
                        pos?.let {
                            this.findNavController().navigate(
                                PosFragmentDirections.actionPosFragmentToArticleEntryFragment(
                                )
                            )
                            posViewModel.onPosNavigated()
                        }
                    })
            })

        adapter.submitList(posList)

        binding.posSelection.adapter = adapter
        recyclerView.adapter = adapter

    }
}