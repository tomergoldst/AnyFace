package com.tomergoldst.anyface.utils

import android.content.Context
import android.util.TypedValue

object DimensionUtils {

    fun dp2Px(ctx: Context, value: Int): Float {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value.toFloat(), ctx.resources.displayMetrics)
    }

}
