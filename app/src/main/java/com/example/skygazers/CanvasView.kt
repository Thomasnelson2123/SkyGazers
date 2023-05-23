package com.example.skygazers

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView

class CanvasView(context: Context) : View(context) {

    private var xPosition = 0
    private var yPosition = 0
    private var paint = Paint()
    private lateinit var canvas: Canvas

    private var startX = 0f
    private var startY = 0f
    private var endX = 0f
    private var endY = 0f



    init {
        paint.strokeWidth = 8f
        paint.color = Color.RED

    }

    fun setSunCoords(x: Int, y: Int){
        //invalidate()
        this.xPosition = x;
        this.yPosition = y;

    }
    fun drawLine(startX: Float, startY: Float, endX: Float, endY: Float){

        this.startX = startX
        this.startY = startY
        this.endX = endX
        this.endY = endY

    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        this.canvas = canvas
        invalidate()

        // Define the desired x and y screen positions
        //val xPosition = 300
        //val yPosition = 400

        val drawable = resources.getDrawable(R.drawable.sun)

        drawable.setBounds(xPosition, yPosition, xPosition+100, yPosition+100);
        // Draw the image at the specified position

        this.canvas.drawLine(xPosition.toFloat(), yPosition.toFloat(), xPosition+600f, yPosition+600f, paint)

        drawable.draw(this.canvas)
        drawable.setBounds(xPosition+500, yPosition+500, xPosition+600, yPosition+600)
        drawable.draw(this.canvas)



    }
}