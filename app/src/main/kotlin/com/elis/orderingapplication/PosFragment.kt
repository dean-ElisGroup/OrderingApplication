package com.elis.orderingapplication


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
import com.elis.orderingapplication.databinding.FragmentPosBinding
import com.elis.orderingapplication.pojo2.PointsOfService
import com.elis.orderingapplication.viewModels.ParamsViewModel
import com.elis.orderingapplication.viewModels.PosViewModel
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.viewModelScope
import com.elis.orderingapplication.adapters.listAdapters.PointOfServiceAdapter
import com.elis.orderingapplication.viewModels.SharedViewModelFactory
import kotlinx.coroutines.launch

class PosFragment : Fragment() {

    private lateinit var binding: FragmentPosBinding
    private val sharedViewModel: ParamsViewModel by activityViewModels()
    private val args: PosFragmentArgs by navArgs()
    private lateinit var pointOfServiceAdapter: PointOfServiceAdapter
    private val posViewModel: PosViewModel by viewModels {
        SharedViewModelFactory(sharedViewModel, requireActivity().application)
    }
    private lateinit var recyclerView: RecyclerView
    private var deliveryAddressForArgs: String = ""
    private var orderingGroupForArgs: String? = null


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
                val action = PosFragmentDirections.actionPosFragmentToPosGroupFragment(
                    // Pass arguments here
                    deliveryAddressForArgs
                )
                Navigation.findNavController(it).navigate(action)
            }
        }

        val deliveryAddressFromArgs =
            sharedViewModel.argsBundleFromTest.value?.getString("DELIVERY_ADDRESS_NAME", "")
        orderingGroupForArgs =
            sharedViewModel.argsBundleFromTest.value?.getString("ORDERING_GROUP", "")
        if (deliveryAddressFromArgs != null) {
            binding.deliveryAddress.text = deliveryAddressFromArgs
            binding.orderingGroup.text = orderingGroupForArgs
        } else {
            sharedViewModel.argsBundleFromTest.observe(viewLifecycleOwner, Observer {
                deliveryAddressForArgs = it.getString("DELIVERY_ADDRESS_NAME", "")
                orderingGroupForArgs = it.getString("ORDERING_GROUP", "")
                binding.deliveryAddress.text = deliveryAddressForArgs
                binding.orderingGroup.text = orderingGroupForArgs
            })
        }

        // Sets ordering group and ordering name to shared ViewModel
        args.orderingGroupNo?.let { sharedViewModel.setOrderingGroupNo(it) }
        sharedViewModel.setOrderingGroupName(args.orderingGroupName)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setFlavorBanner()
        recyclerView = binding.posSelection

        val spacingInPixels = resources.getDimensionPixelSize(R.dimen.spacing)
        val itemSpacingDecoration = CardViewDecoration(spacingInPixels)
        recyclerView.addItemDecoration(itemSpacingDecoration)

        pointOfServiceAdapter =
            PointOfServiceAdapter(object : PointOfServiceAdapter.MyClickListener {
                override fun onItemClick(myData: PointsOfService) {
                    posViewModel.onPosClicked(myData)
                    sharedViewModel.setPosNum(myData.pointOfServiceNo)
                    posViewModel.navigateToPos.observe(
                        viewLifecycleOwner,
                        Observer { pointOfService ->
                            pointOfService?.let {
                                findNavController().navigate(
                                    PosFragmentDirections.actionPosFragmentToOrderFragment(
                                        pointOfService.pointOfServiceNo,
                                        pointOfService.deliveryAddressName,
                                        pointOfService.pointOfServiceName
                                    )
                                )
                                posViewModel.onPosNavigated()
                            }
                        })

                }
            })
        binding.posSelection.adapter = pointOfServiceAdapter
        // Observe the LiveData from the ViewModel
        posViewModel.pointOfService.observe(viewLifecycleOwner) { pointsOfService ->
            pointOfServiceAdapter.setData(pointsOfService)
            sharedViewModel.setPOSTotal(pointOfServiceAdapter.itemCount)
        }
        sharedViewModel.posTotal.observe(
            viewLifecycleOwner,
            Observer { totalPos -> binding.totalPOS.text = totalPos.toString() })

    }

    private fun setFlavorBanner() {
        // sets banner text
        if (sharedViewModel.flavor.value == "development") {
            binding.debugBanner.setBackgroundColor(ContextCompat.getColor(requireContext(),R.color.purple_200))
            binding.bannerText.text = resources.getString(R.string.devFlavorText)
        }
        // hides banner if PROD application
        if (sharedViewModel.flavor.value == "production") {
        }
        // sets banner text and banner color
        if (sharedViewModel.flavor.value == "staging") {
            binding.debugBanner.setBackgroundColor(ContextCompat.getColor(requireContext(),R.color.elis_orange))
            binding.bannerText.text = resources.getString(R.string.devFlavorText)
        }
    }
}