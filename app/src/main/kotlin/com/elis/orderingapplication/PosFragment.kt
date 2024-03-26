package com.elis.orderingapplication

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation
import com.elis.orderingapplication.databinding.FragmentPosBinding
import com.elis.orderingapplication.viewModels.ParamsViewModel

class PosFragment : Fragment() {

    private lateinit var binding: FragmentPosBinding
    private val sharedViewModel: ParamsViewModel by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_pos, container, false)
        binding.apply { viewModel = sharedViewModel }
        binding.toolbar.title = getString(R.string.pos_title)
        binding.toolbar.setNavigationIcon(R.drawable.ic_back)
        binding.toolbar.setNavigationOnClickListener {
            view?.let { it ->
                Navigation.findNavController(it)
                    .navigate(R.id.action_posFragment_to_posGroupFragment)
            }
        }
        // Inflate the layout for this fragment
        return binding.root

    }
}