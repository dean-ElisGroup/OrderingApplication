package com.elis.orderingapplication

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

/*class to control the spacing between each single cardview*/
class CardViewDecoration(private val spacing: Int) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        // Set spacing for all sides (left, top, right, bottom)
        outRect.left = spacing
        outRect.right = spacing
        outRect.top = spacing
        outRect.bottom = spacing
    }
}