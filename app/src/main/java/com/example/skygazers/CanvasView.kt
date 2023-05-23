package com.example.skygazers

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView

class CanvasView(context: Context) : View(context) {
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)


        // Define the desired x and y screen positions
        val xPosition = 300
        val yPosition = 400

        val drawable = resources.getDrawable(R.drawable.sun)

        drawable.setBounds(xPosition, yPosition, xPosition+100, yPosition+100);
        // Draw the image at the specified position

        drawable.draw(canvas)
        drawable.setBounds(xPosition+30, yPosition+30, xPosition+100+30, yPosition+100+30);
        drawable.draw(canvas)


    }
}