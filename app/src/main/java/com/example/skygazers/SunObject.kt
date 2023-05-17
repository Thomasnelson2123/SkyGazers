package com.example.skygazers

import android.location.Location
import android.widget.ImageView
class SunObject(var loc: Location, year: Int, var month: Int, var day: Int, var hour: Int) {
    var year = 0
    var azimuth: Float
    var elevation: Float
    var img: ImageView? = null
    var xpos = 0
    var ypos = 0
    val sunPosCode = SunPosition(loc.latitude, loc.longitude, -8)

    init {
        azimuth = 0f
        elevation = 0f
        calcPosition(sunPosCode)
    }

    fun calcPosition(sunPosCode: SunPosition) {
        val position = sunPosCode.calculateSunPosition(year, month, day, hour, 0)
        elevation = position[0].toFloat()
        azimuth = position[1].toFloat()
    }

    fun updateHour(hour: Int) {
        this.hour = hour
        calcPosition(sunPosCode)
    }

    fun setImage(image: ImageView?) {
        img = image
    }
}