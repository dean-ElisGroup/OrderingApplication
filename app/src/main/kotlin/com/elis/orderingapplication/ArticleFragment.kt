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
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.widget.ViewPager2
import com.elis.orderingapplication.adapters.ArticleEntryAdapter
import com.elis.orderingapplication.constants.Constants
import com.elis.orderingapplication.constants.Constants.Companion.SHOW_BANNER
import com.elis.orderingapplication.databinding.FragmentArticleBinding
import com.elis.orderingapplication.pojo2.Order
import com.elis.orderingapplication.repositories.AppRepository
import com.elis.orderingapplication.utils.DeviceInfo
import com.elis.orderingapplication.utils.DeviceInfoDialog
import com.elis.orderingapplication.utils.FlavorBannerUtils
import com.elis.orderingapplication.viewModels.ArticleDataViewModel
import com.elis.orderingapplication.viewModels.ArticleViewModel
import com.elis.orderingapplication.viewModels.ParamsViewModel
import com.elis.orderingapplication.viewModels.SharedViewModelFactory
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton


class ArticleFragment : Fragment(), ArticleEntryCardFragment.LastArticleCallback, ArticleEntryCardFragment.OrderStatusCallback {

    private var _binding: FragmentArticleBinding? = null
    //private lateinit var binding: FragmentArticleBinding
    private val binding: FragmentArticleBinding get() = _binding!!
    private val sharedViewModel: ParamsViewModel by activityViewModels()
    private lateinit var viewPager: ViewPager2
    private lateinit var viewPagerAdapter: ArticleEntryAdapter
    private val articleViewModel: ArticleViewModel by viewModels {
        SharedViewModelFactory(sharedViewModel, requireActivity().application)
    }
    private val articleDataViewModel: ArticleDataViewModel by viewModels()
    private val args: ArticleFragmentArgs by navArgs()
    private var orderStatus: Int? = 0

    private var isDialogShown = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_article, container, false)

        binding.orderId = args.appOrderId
        binding.toolbar.title = getString(R.string.article_title)
        binding.toolbar.setNavigationIcon(R.drawable.ic_back)
        binding.toolbar.setTitleTextAppearance(requireContext(),R.style.titleTextStyle)
        binding.toolbar.setNavigationOnClickListener {
            if(orderStatus != Constants.APP_STATUS_SENT) {
                requireActivity().window.addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                orderNotSubmittedDialog(false)
            } else {
                findNavController().navigate(R.id.action_articleFragment_to_orderFragment)
            }
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
        viewPagerAdapter = ArticleEntryAdapter(
            childFragmentManager,
            lifecycle,
        )

        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if(SHOW_BANNER) {
            FlavorBannerUtils.setupFlavorBanner(
                resources,
                requireContext(),
                binding,
                sharedViewModel
            )
            binding.debugBanner.visibility = VISIBLE
        }

        binding.progressBar.visibility = VISIBLE

        articleViewModel.order.observe(viewLifecycleOwner) { order ->
            // Assuming you want to display the first article
            binding.orderData = order.firstOrNull()
        }

        articleViewModel.orderDate.observe(viewLifecycleOwner) { formattedDate ->
            binding.orderDateVal = formattedDate // Assuming you have a 'orderDate' variable in your layout
        }

            val fab = view.findViewById<ExtendedFloatingActionButton>(R.id.send_order_fab)
        fab.setOnClickListener {
            fab.isEnabled = false
            // Handle FAB click
            val currentFragment = getCurrentFragment()
            if (currentFragment is ArticleEntryCardFragment) {
                // Call a method in the ArticleEntryCardFragment to handle the FAB click
                currentFragment.startInternetCheckJob()
                fab.isEnabled = true
            }
        }

        sharedViewModel.setLastArticleCallback(this)
        sharedViewModel.setOrderStatusCallback(this)

        val orderDate = sharedViewModel.orderDate.value
        val orderId = sharedViewModel.orderId.value

        val appRepository = AppRepository()

        viewPagerAdapter.clearArticles()
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
                for (article in articles) {
                    articleDataViewModel.setArticleData(article)
                }
            }
        }, 500)
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
        if (!isDialogShown) {
            isDialogShown = true // Set the flag to true to indicate that the dialog is being shown
            requireActivity().window.addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)

            val builder = AlertDialog.Builder(requireContext())
            builder.setIcon(R.drawable.dialog_warning)
            builder.setTitle("Order entry")
            builder.setMessage("Order has not been submitted, Do you wish to continue?")
            builder.setPositiveButton("Yes") { _, _ ->
                // Handle positive button click, navigate back to OrderFragment
                if (homeClicked) {
                    findNavController().navigate(R.id.action_articleFragment_to_landingPageFragment)
                } else {
                    findNavController().navigate(R.id.action_articleFragment_to_orderFragment)
                }
                isDialogShown = false // Reset the flag when the dialog is dismissed
            }
            builder.setNegativeButton("No", null)

            val dialog = builder.create()
            dialog.setCancelable(false) // Prevent dismissing the dialog by tapping outside or pressing the back button

            dialog.setOnDismissListener {
                requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                isDialogShown = false // Reset the flag when the dialog is dismissed
            }
            dialog.show()
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Clear the binding reference
    }
}