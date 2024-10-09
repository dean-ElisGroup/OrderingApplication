package com.elis.orderingapplication.utils

import android.content.Context
import android.content.res.Resources
import android.view.View
import com.elis.orderingapplication.viewModels.ParamsViewModel
import android.view.View.VISIBLE
import android.view.View.GONE
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.ViewDataBinding
import com.elis.orderingapplication.R

/* Object controls the showing of the flavour banner in the app. This is a global object and all Fragments relate back to this code */

object FlavorBannerUtils {
    fun <T : ViewDataBinding> setupFlavorBanner(resources: Resources, context: Context, binding: T, sharedViewModel: ParamsViewModel){
        when (sharedViewModel.flavor.value) {
            "development" -> {
                val debugBanner = binding.root.findViewById<View>(R.id.debug_banner)
                val bannerText = binding.root.findViewById<TextView>(R.id.banner_text)

                debugBanner?.visibility = VISIBLE
                bannerText?.visibility = VISIBLE
                bannerText?.text = resources.getString(R.string.devFlavorText)
                debugBanner?.setBackgroundColor(
                    ContextCompat.getColor(
                        context,
                        R.color.purple_200
                    )
                )
            }
            "production" -> {
                val debugBanner = binding.root.findViewById<View>(R.id.debug_banner)
                val bannerText = binding.root.findViewById<TextView>(R.id.banner_text)

                debugBanner?.visibility = GONE
                bannerText?.visibility = GONE
                debugBanner?.setBackgroundColor(
                    ContextCompat.getColor(
                        context,
                        R.color.elis_transparent
                    )
                )
            }
            "staging" -> {
                val debugBanner = binding.root.findViewById<View>(R.id.debug_banner)
                val bannerText = binding.root.findViewById<TextView>(R.id.banner_text)

                debugBanner?.visibility = VISIBLE
                bannerText?.visibility = VISIBLE
                bannerText?.text = resources.getString(R.string.testFlavorText)
                debugBanner?.setBackgroundColor(
                    ContextCompat.getColor(
                        context,
                        R.color.elis_orange
                    )
                )
            }
        }
    }
}