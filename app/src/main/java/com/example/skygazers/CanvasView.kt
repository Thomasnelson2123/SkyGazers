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
    private var currHour = 0

    private var suns = ArrayList<SunObject?>()



    init {
        paint.strokeWidth = 8f
        paint.color = Color.RED

    }

    fun setSuns(suns: ArrayList<SunObject?>) {
        this.suns = suns
    }

    fun updateHour(hour: Int) {
        currHour = hour
        if(suns.isEmpty()) {
            return
        }
        Log.d("sun status", suns.get(8)?.azimuth.toString())
        xPosition = suns.get(hour)?.xpos?.toInt() ?: 0
        yPosition = suns.get(hour)?.ypos?.toInt() ?: 0
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

        val size = 100

        drawable.setBounds(xPosition - (size / 2), yPosition - (size / 2), xPosition+(size / 2), yPosition+(size / 2));
        // Draw the image at the specified position


        if(suns.isEmpty()) {
            return
        }

//        this.canvas.drawLine(suns.get(currHour-1)!!.xpos, suns.get(currHour-1)!!.ypos, xPosition.toFloat(), yPosition.toFloat(), paint)
//        this.canvas.drawLine(xPosition.toFloat(), yPosition.toFloat(), suns.get(currHour+1)!!.xpos, suns.get(currHour+1)!!.ypos, paint)

        drawable.draw(this.canvas)
//        for(i in 2..22) {
//            this.canvas.drawLine(suns.get(i)!!.xpos+50, suns.get(i)!!.ypos+50, suns.get(i+1)!!.xpos+50, suns.get(i+1)!!.ypos+50, paint)
//            drawable.setBounds(suns.get(i)?.xpos?.toInt()!!, suns.get(i)?.ypos?.toInt()!!, suns.get(i)?.xpos?.toInt()!!+100, suns.get(i)?.ypos?.toInt()!!+100)
//            drawable.draw(this.canvas)
//        }
//        drawable.setBounds(suns.get(currHour+1)?.xpos?.toInt()!!- (size / 2), suns.get(currHour+1)?.ypos?.toInt()!!- (size / 2), suns.get(currHour+1)?.xpos?.toInt()!!+(size/2), suns.get(currHour+1)?.ypos?.toInt()!!+(size/2))
//        drawable.draw(this.canvas)
//        drawable.setBounds(suns.get(currHour-1)?.xpos?.toInt()!! - (size / 2), suns.get(currHour-1)?.ypos?.toInt()!! - (size / 2), suns.get(currHour-1)?.xpos?.toInt()!!+(size/2), suns.get(currHour-1)?.ypos?.toInt()!!+(size/2))
//        drawable.draw(this.canvas)


    }
}