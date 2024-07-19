package com.elis.orderingapplication

import android.app.AlertDialog
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.PopupMenu
import androidx.compose.ui.unit.Constraints
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.widget.ViewPager2
import com.elis.orderingapplication.adapters.ArticleEntryAdapter
import com.elis.orderingapplication.constants.Constants
import com.elis.orderingapplication.constants.Constants.Companion.SHOW_BANNER
import com.elis.orderingapplication.databinding.FragmentArticleBinding
import com.elis.orderingapplication.pojo2.Article
import com.elis.orderingapplication.pojo2.Order
import com.elis.orderingapplication.repositories.UserLoginRepository
import com.elis.orderingapplication.utils.DeviceInfo
import com.elis.orderingapplication.utils.DeviceInfoDialog
import com.elis.orderingapplication.viewModels.ArticleEntryViewModel
import com.elis.orderingapplication.viewModels.ArticleEntryViewModelFactory
import com.elis.orderingapplication.viewModels.ArticleViewModel
import com.elis.orderingapplication.viewModels.ParamsViewModel
import com.elis.orderingapplication.viewModels.SharedViewModelFactory
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.floatingactionbutton.FloatingActionButton


class ArticleFragment : Fragment(), ArticleEntryCardFragment.LastArticleCallback, ArticleEntryCardFragment.OrderStatusCallback {

    private lateinit var binding: FragmentArticleBinding
    private val sharedViewModel: ParamsViewModel by activityViewModels()
    private lateinit var viewPager: ViewPager2
    private lateinit var viewPagerAdapter: ArticleEntryAdapter
    private val articleViewModel: ArticleViewModel by viewModels {
        SharedViewModelFactory(sharedViewModel, requireActivity().application)
    }
    private val args: ArticleFragmentArgs by navArgs()
    private var orderStatus: Int? = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_article, container, false)

        //binding.sharedViewModel = sharedViewModel
        binding.orderData = args.orderData
        binding.orderDateVal = args.order
        binding.toolbar.title = getString(R.string.article_title)
        binding.toolbar.setNavigationIcon(R.drawable.ic_back)
        binding.toolbar.setTitleTextAppearance(requireContext(),R.style.titleTextStyle)
        binding.toolbar.setNavigationOnClickListener {
            if(orderStatus != Constants.APP_STATUS_SENT) {
                orderNotSubmittedDialog(false)
            } else {
                findNavController().navigate(R.id.action_articleFragment_to_orderFragment)
            }
            /*view?.let { it ->
                Navigation.findNavController(it)
                    .navigate(R.id.action_articleFragment_to_orderFragment)
            }*/
        }
        binding.toolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.overflow -> {
                    val deviceInfo = DeviceInfo(requireContext())
                    DeviceInfoDialog.showAlertDialog(requireContext(), deviceInfo.getDeviceInfo())
                    true
                }

                R.id.home_button -> {
                    if (orderStatus != Constants.APP_STATUS_SENT) {
                        orderNotSubmittedDialog(true)
                    } else
                        findNavController().navigate(R.id.action_articleFragment_to_landingPageFragment)
                    false
                }
                else -> false
            }
        }

        viewPager = binding.articleEntryViewpager

        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if(SHOW_BANNER) {
            setFlavorBanner()
            binding.debugBanner.visibility = VISIBLE
        }

        binding.progressBar.visibility = View.VISIBLE

        val fab = view.findViewById<ExtendedFloatingActionButton>(R.id.send_order_fab)
        fab.setOnClickListener {
            // Handle FAB click
            val currentFragment = getCurrentFragment()
            if (currentFragment is ArticleEntryCardFragment) {
                // Call a method in the ArticleEntryCardFragment to handle the FAB click
                currentFragment.startInternetCheckJob()
            }
        }

        sharedViewModel.setLastArticleCallback(this)
        sharedViewModel.setOrderStatusCallback(this)

        val userLoginRepository = UserLoginRepository()
        val articleEntryViewModel: ArticleEntryViewModel by viewModels {
            ArticleEntryViewModelFactory(
                sharedViewModel,
                requireActivity().application,
                userLoginRepository
            )
        }
        viewPager = binding.articleEntryViewpager
        viewPagerAdapter = ArticleEntryAdapter(
            childFragmentManager,
            lifecycle,
            emptyList()
        )
        viewPager.adapter = viewPagerAdapter
        Handler(Looper.getMainLooper()).postDelayed({
            articleViewModel.articles.observe(viewLifecycleOwner) { articles ->
                requireActivity().window.setFlags(
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                )
                viewPagerAdapter.updateData(articles)
                binding.progressBar.visibility = View.GONE
                requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                sharedViewModel.setArticleTotal(articles.size)
            }
        }, 500)
    }

    private fun setFlavorBanner() {
        when (sharedViewModel.flavor.value) {
            "development" -> {
                binding.debugBanner.visibility = View.VISIBLE
                binding.bannerText.visibility = View.VISIBLE
                binding.debugBanner.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.purple_200
                    )
                )
                binding.bannerText.text = resources.getString(R.string.devFlavorText)
            }
            "production" -> {
                binding.debugBanner.visibility = View.GONE
                binding.bannerText.visibility = View.GONE
                binding.debugBanner.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.elis_transparent
                    )
                )
            }
            "staging" -> {
                binding.debugBanner.visibility = View.VISIBLE
                binding.bannerText.visibility = View.VISIBLE
                binding.debugBanner.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.elis_orange
                    )
                )
                binding.bannerText.text = resources.getString(R.string.testFlavorText)
            }
        }
    }


    private fun getCurrentFragment(): Fragment? {
        return childFragmentManager.findFragmentByTag("f${viewPager.currentItem}")
    }

    override fun onLastArticleChanged(isLastArticle: Boolean) {
        val fab = requireView().findViewById<ExtendedFloatingActionButton>(R.id.send_order_fab)
        fab.isVisible = isLastArticle
    }

    override fun onOrderStatusDataReceived(orderData: Order?) {
        // Handle the received Order data here
        orderStatus = orderData?.appOrderStatus?.toInt()
    }

    private fun orderNotSubmittedDialog(homeClicked: Boolean) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setIcon(R.drawable.dialog_warning)
        builder.setTitle("Order entry")
        builder.setMessage("Order has not been submitted, Do you wish to continue?")
        builder.setPositiveButton("Yes") { _, _ ->
            // Handle positive button click, navigate back to OrderFragment
            if(homeClicked){
                findNavController().navigate(R.id.action_articleFragment_to_landingPageFragment)
            }else
            findNavController().navigate(R.id.action_articleFragment_to_orderFragment)
        }
        builder.setNegativeButton("No", null)
        builder.show()
    }
}