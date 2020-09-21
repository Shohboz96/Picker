package com.example.permissionsl13.utils

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Path
import android.graphics.RectF
import android.util.AttributeSet
import android.util.DisplayMetrics
import androidx.appcompat.widget.AppCompatImageView
import kotlin.math.roundToInt

class RoundRectCornerImageView : AppCompatImageView {
    private var radius: Float = 8.dpToPx(context).toFloat()
    private var path: Path? = null

    constructor(context: Context?) : super(context!!) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context!!, attrs) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyle: Int) : super(
        context!!,
        attrs,
        defStyle
    ) {
        init()
    }

    fun setCornerRadius(radius: Int) {
        this.radius = radius.dpToPx(context).toFloat()
        invalidate()
    }

    private fun init() {
        path = Path()
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        val rect = RectF(0.toFloat(), 0.toFloat(), this.width.toFloat(), this.height.toFloat())
        path!!.addRoundRect(rect, radius, radius, Path.Direction.CW)
        canvas.clipPath(path!!)
        super.onDraw(canvas)
    }
}

fun Int.dpToPx(context: Context): Int {
    return (this * (context.resources.displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT)).roundToInt()
}