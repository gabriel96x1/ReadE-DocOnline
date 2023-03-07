package com.mutablestate.readonline.presentation.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

class CustomRectangleView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : View(context, attrs, defStyle) {

    private val paint = Paint()

    init {
        paint.color = Color.WHITE
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 10f
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        val width = width.toFloat()
        val height = height.toFloat()
        val cornerRadius = 20f // set corner radius to 20 pixels
        val rectWidth = width * 0.8f // set rectangle width to 80% of the view width
        val rectHeight = height * 0.55f // set rectangle height to 60% of the view height
        val rectLeft = (width - rectWidth) / 2f // calculate left coordinate of the rectangle
        val rectTop = (height - rectHeight) / 2f // calculate top coordinate of the rectangle

        // Draw a rounded rectangle in the center of the view
        canvas?.drawRoundRect(
            rectLeft, rectTop, rectLeft + rectWidth, rectTop + rectHeight,
            cornerRadius, cornerRadius, paint
        )
    }
}