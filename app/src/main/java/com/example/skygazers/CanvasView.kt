package com.example.skygazers

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.Log
import android.view.View

class CanvasView(context: Context) : View(context) {

    private var xPosition = 0
    private var yPosition = 0
    private var paint = Paint()
    private lateinit var canvas: Canvas

    private var startX = 0f
    private var startY = 0f
    private var endX = 0f
    private var endY = 0f

    private var suns = ArrayList<SunObject?>()



    init {
        paint.strokeWidth = 8f
        paint.color = Color.RED

    }

    fun setSuns(suns: ArrayList<SunObject?>) {
        this.suns = suns
    }

    fun updateHour(hour: Int) {
        if(suns.isEmpty()) {
            return
        }
        Log.d("sun status", suns.get(8)?.azimuth.toString())
        xPosition = suns.get(hour)?.xpos?.toInt() ?: 0
        yPosition = suns.get(hour)?.xpos?.toInt() ?: 0
    }

    fun setSunCoords(x: Float?, y: Float?){
        //invalidate()
        if (x != null) {
            this.xPosition = x.toInt()
        };
        if (y != null) {
            this.yPosition = y.toInt()
        };

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